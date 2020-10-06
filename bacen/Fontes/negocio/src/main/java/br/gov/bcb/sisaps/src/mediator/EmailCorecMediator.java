package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.bcmail.BcMailAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dao.EmailCorecDao;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.CargaParticipante;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.util.UtilFormatarEmail;

@Service
@Transactional(readOnly = true)
public class EmailCorecMediator {
    private static final String EMPTY = "";
    private static final String NOME_ES = "%es%";
    private static final String LOCALIZACAO = "%localização%";
    private static final String DATA_COREC = "%datacorec%";
    private static final String LOCAL = "%local%";
    private static final String HORARIO = "%horário%";
    private static final String TABELA = "%tabelacomite%";
    private static final String SEPARADOR_BARRA = "|";

    @Autowired
    private EmailCorecDao emailCorecDao;

    public static EmailCorecMediator get() {
        return SpringUtils.get().getBean(EmailCorecMediator.class);
    }

    public List<EmailCorec> buscarEmailParticipantePorTipo(Integer agendaPK, TipoEmailCorecEnum tipo) {
        if (agendaPK == null) {
            return new ArrayList<EmailCorec>();
        }
        return emailCorecDao.buscarListEmailParticipante(agendaPK, tipo);
    }

    @Transactional
    public void excluirEmailParticipantes(List<EmailCorec> emailParticipante) {
        for (EmailCorec emailParticipanteAgendaCorec : emailParticipante) {
            emailCorecDao.delete(emailParticipanteAgendaCorec);
        }
    }

    public EmailCorec buscarEmailParticipante(Integer agendaPK, String matricula, TipoEmailCorecEnum tipo) {
        return emailCorecDao.buscarEmailParticipante(agendaPK, matricula, tipo);
    }

    public EmailCorec retornarDataSolicitacaoAssinatura(Integer pkAgenda, String matricula) {
        EmailCorec emailParticipante =
                EmailCorecMediator.get().buscarEmailParticipante(pkAgenda, matricula,
                        TipoEmailCorecEnum.SOLICITACAO_ASSINATURA);
        return emailParticipante;
    }

    public void enviarEmailParticipantesCorec(AgendaCorec agenda, TipoEmailCorecEnum tipo) {
        List<String> destinatarios = new ArrayList<String>();
        List<String> matriculaParticipantes = new ArrayList<String>();
        for (ParticipanteAgendaCorec participante : agenda.getParticipantes()) {
            EmailCorec emailParticipante =
                    EmailCorecMediator.get().buscarEmailParticipante(agenda.getPk(), participante.getMatricula(), tipo);
            if (emailParticipante == null) {
                String destinatario = getDestinatario(participante);
                if (destinatario.equals(EMPTY)) {
                    System.out.println("Participante de matrícula " 
                            + participante.getMatricula() + " não possui e-mail válido.");
                } else {
                    destinatarios.add(destinatario);
                    matriculaParticipantes.add(participante.getMatricula());
                }
            }
        }

        for (String matricula : matriculaParticipantes) {
            EmailCorec emailCorec = new EmailCorec();
            emailCorec.setAgenda(agenda);
            emailCorec.setTipo(tipo);
            emailCorec.setMatricula(matricula);
            emailCorecDao.save(emailCorec);
        }

        if (!destinatarios.isEmpty()) {
            ParametroEmail parametro = ParametroEmailMediator.get().buscarPorTipo(tipo);
            if (parametro != null) {
                List<String> listaDadosCorpo = new ArrayList<String>();
                String tituloEmail = montarTituloCorpoEmail(parametro.getTitulo(), agenda, listaDadosCorpo, false);
                String corpoEmail = montarTituloCorpoEmail(parametro.getCorpo(), agenda, listaDadosCorpo, true);
                String montarTabela = montarLinhasEmail(listaDadosCorpo);
                corpoEmail = corpoEmail.replace(TABELA, montarTabela);
                enviarEmail(destinatarios, corpoEmail, tituloEmail, parametro.getRemetente());
            }
        }

    }

    public String montarTituloCorpoEmail(String tituloCorpo, AgendaCorec agenda, List<String> listaDadosCorpo,
            boolean isTabela) {
        String tituloCorpoEmail = tituloCorpo;
        EntidadeSupervisionavel es = agenda.getCiclo().getEntidadeSupervisionavel();
        tituloCorpoEmail = tituloCorpoEmail.replace(NOME_ES, es.getNome());
        tituloCorpoEmail = tituloCorpoEmail.replace(LOCALIZACAO, es.getLocalizacao());
        tituloCorpoEmail = tituloCorpoEmail.replace(DATA_COREC, agenda.getCiclo().getDataPrevisaoFormatada());
        tituloCorpoEmail = tituloCorpoEmail.replace(LOCAL, agenda.getLocal() == null ? " " : agenda.getLocal());
        tituloCorpoEmail = tituloCorpoEmail.replace(HORARIO, agenda.getHoraCorecFormatada());
        if (isTabela) {
            listaDadosCorpo.add(es.getNome() + SEPARADOR_BARRA + es.getLocalizacao() + SEPARADOR_BARRA
                    + es.getNomeSupervisor());
        }
        return tituloCorpoEmail;
    }

    public String getDestinatario(ParticipanteAgendaCorec participante) {
        String retorno = "";
        CargaParticipante cargaParticipante = CargaParticipanteMediator.get().buscarCargaPorMatricula(participante.getMatricula());
        if (cargaParticipante != null && StringUtils.isNotBlank(cargaParticipante.getEmail())) {
            retorno = cargaParticipante.getEmail();
        } else {
            ServidorVO servidor = BcPessoaAdapter.get().buscarServidor(participante.getMatricula());
            if (servidor != null && StringUtils.isNotBlank(servidor.getEmail())) {
                retorno = servidor.getEmail();
            }
        }
        return retorno;
    }

    public String getRemetente(TipoEmailCorecEnum tipo) {
        ParametroEmail parametro = ParametroEmailMediator.get().buscarPorTipo(tipo);
        return parametro == null ? EMPTY : parametro.getRemetente();
    }

    public void enviarEmail(List<String> destinatarios, String corpoEmail, String assunto, String remetente) {
        Email email = new Email();
        email.setDestinatarios(destinatarios);
        email.setAssunto(UtilFormatarEmail.removerMarcadoresEmail(assunto));
        email.setCorpo(UtilFormatarEmail.removerMarcadoresEmail(corpoEmail));
        email.setRemetente(remetente);
        BcMailAdapter.get().enviarEmail(email);
    }

    public String montarLinhasEmail(List<String> listaRecomendacoesAssunto) {
        StringBuffer sb = new StringBuffer();
        montarCabecalhoTabela(sb);

        for (String linha : listaRecomendacoesAssunto) {
            addLinha(sb, linha);
        }
        montarRodapeTabela(sb);
        return sb.toString();
    }

    private void montarCabecalhoTabela(StringBuffer sb) {
        sb.append("<table width='90%' border='1'>").append(
                "<thead><tr style='background-color:#EBEEF1;'>" + "<th width='25%' style='text-align: left'>ES</th>"
                        + "<th width='25%' style='text-align: left'>Localização</th>"
                        + "<th width='25%' style='text-align: left'>Supervisor titular</th></tr></thead><tbody>");
    }

    private void addLinha(StringBuffer sb, String linha) {

        String[] linhaDiv = StringUtils.split(linha, SEPARADOR_BARRA);
        sb.append("<tr>");
        for (String conteudo : linhaDiv) {
            sb.append("<td>");
            sb.append(conteudo);
            sb.append("</td>");
        }
        sb.append("</tr>");
    }

    private void montarRodapeTabela(StringBuffer sb) {
        sb.append("</tbody></table>");
    }
    
    
    @Transactional
    public void saveOrUpdate(EmailCorec email) {
        emailCorecDao.saveOrUpdate(email);
    }

}
