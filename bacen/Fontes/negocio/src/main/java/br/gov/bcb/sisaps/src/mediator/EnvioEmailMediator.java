package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class EnvioEmailMediator {
    private static final String TERMINOU_EM = "Terminou em: ";
    private static final String DETINATARIO = "detinatario: ";
    private static final String CICLOS_EM_ANDAMENTO_E_COREC_ENCONTRADOS =
            " ciclos 'Em andamento' e 'Corec' encontrados.";
    private static final String BUSCAR_TODOS_OS_CICLOS_EM_ANDAMENTO_E_COREC =
            "Buscar todos os ciclos 'Em andamento' e 'Corec'";
    private static final String INICIADO_EM = "Iniciado em: ";
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchMediator");

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void rotinaBatchDisponibilidade() {
        DateTime dateTimeAtual = DataUtil.getDateTimeAtual();
        LOG.info(INICIADO_EM + dateTimeAtual);

        CicloMediator cicloMediator = CicloMediator.get();
        AgendaCorecMediator agendaCorecMediator = AgendaCorecMediator.get();
        ParametroEmailMediator parametroEmailMediator = ParametroEmailMediator.get();
        EmailCorecMediator emailCorecMediator = EmailCorecMediator.get();
        ParametroEmail parametroDisponibilidade =
                parametroEmailMediator.buscarPorTipo(TipoEmailCorecEnum.DISPONIBILIDADE);
        DateTime dataParametro;
        List<CicloVO> ciclos = new ArrayList<CicloVO>();
        if (parametroDisponibilidade != null) {
            dataParametro = dateTimeAtual.plusDays(parametroDisponibilidade.getPrazo());
            // Buscar todos os ciclos 'Em andamento'
            LOG.info(BUSCAR_TODOS_OS_CICLOS_EM_ANDAMENTO_E_COREC);
            ciclos = cicloMediator.consultaCiclosParaEnvioEmailDisponibilidade(dataParametro);
            LOG.info(ciclos.size() + CICLOS_EM_ANDAMENTO_E_COREC_ENCONTRADOS);
        }
        envioEmailDisponibilidade(dateTimeAtual, cicloMediator, agendaCorecMediator, emailCorecMediator,
                parametroDisponibilidade, ciclos);

        LOG.info(TERMINOU_EM + dateTimeAtual);
    }

    private void envioEmailDisponibilidade(DateTime dateTimeAtual, CicloMediator cicloMediator,
            AgendaCorecMediator agendaCorecMediator, EmailCorecMediator emailCorecMediator,
            ParametroEmail parametroDisponibilidade, List<CicloVO> ciclos) {
        // Para cada ciclo
        for (CicloVO ciclo : ciclos) {
            AgendaCorec agenda = agendaCorecMediator.buscarAgendaCorecPorCiclo(ciclo.getPk());
            LOG.info("### Verificar Se ('Data e-mail disponibilidade' for vazio) E ('Corec previsto' - "
                    + "'prazo definido no parâmetro e-mail disponibilidade' é menor ou igual que a data corrente)");
            Ciclo cicloObj = cicloMediator.buscarCicloPorPK(ciclo.getPk());
            cicloMediator.evict(cicloObj);
            if (agenda == null) {
                agenda = new AgendaCorec();
                agenda.setCiclo(cicloObj);
            }
            agenda.setDataEnvioDisponibilidade(dateTimeAtual);
            List<ParticipanteComiteVO> participantesDestinatarios =
                    montarDestinatariosDisponibilidade(cicloObj, agenda);
            List<String> destinatarios = new ArrayList<String>();

            for (ParticipanteComiteVO participanteComiteVO : participantesDestinatarios) {
                destinatarios.add(participanteComiteVO.getEmail());
                LOG.info(DETINATARIO + participanteComiteVO.getEmail());
            }

            if (!destinatarios.isEmpty()) {
                ObservacaoAgendaCorecMediator.get().evict(agenda.getObservacoes());
                agendaCorecMediator.saveOrUpdate(agenda);
                List<String> listaDadosCorpo = new ArrayList<String>();
                String tituloEmail =
                        emailCorecMediator.montarTituloCorpoEmail(parametroDisponibilidade.getTitulo(), agenda,
                                listaDadosCorpo, false);
                String corpoEmail =
                        emailCorecMediator.montarTituloCorpoEmail(parametroDisponibilidade.getCorpo(), agenda,
                                listaDadosCorpo, true);
                String montarTabela = emailCorecMediator.montarLinhasEmail(listaDadosCorpo);
                corpoEmail = corpoEmail.replace(Constantes.TABELA, montarTabela);
                emailCorecMediator.enviarEmail(destinatarios, corpoEmail, tituloEmail,
                        parametroDisponibilidade.getRemetente());
                criarRegristroEmailParticipante(participantesDestinatarios, agenda);

            }
        }
    }

    private void criarRegristroEmailParticipante(List<ParticipanteComiteVO> participantesDestinatarios,
            AgendaCorec agenda) {

        for (ParticipanteComiteVO participanteComiteVO : participantesDestinatarios) {
            EmailCorec email = new EmailCorec();
            email.setAgenda(agenda);
            email.setTipo(TipoEmailCorecEnum.DISPONIBILIDADE);
            email.setMatricula(participanteComiteVO.getMatricula());
            EmailCorecMediator.get().saveOrUpdate(email);
        }
    }

    private List<ParticipanteComiteVO> montarDestinatariosDisponibilidade(Ciclo cicloObj, AgendaCorec agenda) {
        LOG.info("LISTA DE PARTICIPANTES ENCONTRADOS NA CARGA");
        List<ParticipanteComiteVO> listaParticipantes =
                CargaParticipanteMediator.get().buscarParticipantesPossiveisES(cicloObj.getEntidadeSupervisionavel());
        LOG.info(listaParticipantes.size() + " QUANTIDADE DE PARTICIPANTES ENCONTRADOS");
        LOG.info("LISTA DE EMAIL COREC ENCONTRADOS NA CARGA");
        List<EmailCorec> listaEmailCorec =
                EmailCorecMediator.get().buscarEmailParticipantePorTipo(agenda.getPk(),
                        TipoEmailCorecEnum.DISPONIBILIDADE);
        LOG.info(listaEmailCorec.size() + " QUANTIDADE DE EMAIL COREC ENCONTRADOS");

        List<ParticipanteComiteVO> destinatario = new ArrayList<ParticipanteComiteVO>();
        for (ParticipanteComiteVO participante : listaParticipantes) {
            boolean possui = false;
            for (EmailCorec emailCorec : listaEmailCorec) {
                if (emailCorec.getMatricula().equals(participante.getMatricula())) {
                    possui = true;
                    break;
                }
            }
            if (!possui) {
                LOG.info("ADICIONOU E-MAIL PARTICIPANTE");
                destinatario.add(participante);
            }
        }

        return destinatario;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void rotinaBatchApresentacao() {

        DateTime dateTimeAtual = DataUtil.getDateTimeAtual();
        LOG.info(INICIADO_EM + dateTimeAtual);

        CicloMediator cicloMediator = CicloMediator.get();
        AgendaCorecMediator agendaCorecMediator = AgendaCorecMediator.get();
        ParametroEmailMediator parametroEmailMediator = ParametroEmailMediator.get();
        EmailCorecMediator emailCorecMediator = EmailCorecMediator.get();

        ParametroEmail parametroApresentacao = parametroEmailMediator.buscarPorTipo(TipoEmailCorecEnum.APRESENTACAO);
        DateTime dataParametro;
        List<CicloVO> ciclosApresen = new ArrayList<CicloVO>();
        if (parametroApresentacao != null) {
            dataParametro = dateTimeAtual.plusDays(parametroApresentacao.getPrazo());
            // Buscar todos os ciclos 'Em andamento'
            LOG.info(BUSCAR_TODOS_OS_CICLOS_EM_ANDAMENTO_E_COREC);
            ciclosApresen = cicloMediator.consultaCiclosParaEnvioEmailApresentacao(dataParametro);
            LOG.info(ciclosApresen.size() + CICLOS_EM_ANDAMENTO_E_COREC_ENCONTRADOS);
        }

        envioEmailApresentacao(dateTimeAtual, cicloMediator, agendaCorecMediator, emailCorecMediator,
                parametroApresentacao, ciclosApresen);

        LOG.info(TERMINOU_EM + dateTimeAtual);
    }

    private void envioEmailApresentacao(DateTime dateTimeAtual, CicloMediator cicloMediator,
            AgendaCorecMediator agendaCorecMediator, EmailCorecMediator emailCorecMediator,
            ParametroEmail parametroApresentacao, List<CicloVO> ciclosApresen) {
        // Para cada ciclo
        for (CicloVO ciclo : ciclosApresen) {
            Ciclo cicloCarga = cicloMediator.buscarCicloPorPK(ciclo.getPk());
            cicloMediator.evict(cicloCarga);
            AgendaCorec agenda = agendaCorecMediator.buscarAgendaCorecPorCiclo(ciclo.getPk());
            LOG.info("### Verificar Se ('Data e-mail apresentação' for vazio) E ('Corec previsto' - "
                    + "'prazo definido no parâmetro e-mail apresentação'" + " é menor ou igual que a data corrente)");
            if (agenda == null) {
                agenda = new AgendaCorec();
                agenda.setCiclo(cicloCarga);
            }
            agenda.setDataEnvioApresentacao(dateTimeAtual);
            List<String> destinatarios = montarDestinatariosApresentacao(cicloCarga);

            if (!destinatarios.isEmpty() && parametroApresentacao != null) {
                ObservacaoAgendaCorecMediator.get().evict(agenda.getObservacoes());
                agendaCorecMediator.saveOrUpdate(agenda);
                List<String> listaDadosCorpo = new ArrayList<String>();
                String tituloEmail =
                        emailCorecMediator.montarTituloCorpoEmail(parametroApresentacao.getTitulo(), agenda,
                                listaDadosCorpo, false);
                String corpoEmail =
                        emailCorecMediator.montarTituloCorpoEmail(parametroApresentacao.getCorpo(), agenda,
                                listaDadosCorpo, true);
                String montarTabela = emailCorecMediator.montarLinhasEmail(listaDadosCorpo);
                corpoEmail = corpoEmail.replace(Constantes.TABELA, montarTabela);
                emailCorecMediator.enviarEmail(destinatarios, corpoEmail, tituloEmail,
                        parametroApresentacao.getRemetente());

            }

        }
    }

    private List<String> montarDestinatariosApresentacao(Ciclo cicloObj) {
        List<String> email = new ArrayList<String>();
        ServidorVO supervisor =
                BcPessoaAdapter.get().buscarServidor(
                        CicloMediator.get().buscarChefeAtual(cicloObj.getEntidadeSupervisionavel().getLocalizacao())
                                .getMatricula());
        ServidorVO substituto = BcPessoaAdapter.get().buscarServidor(supervisor.getMatriculaSubstituto());
        email.add(supervisor.getEmail());
        LOG.info(DETINATARIO + supervisor.getEmail());
        if (substituto != null) {
            email.add(substituto.getEmail());
            LOG.info("detinatarios: " + substituto.getEmail());
        }
        return email;
    }
}
