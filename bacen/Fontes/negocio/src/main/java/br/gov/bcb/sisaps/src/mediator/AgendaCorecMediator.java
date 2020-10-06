package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AgendaCorecDao;
import br.gov.bcb.sisaps.src.dao.EmailCorecDao;
import br.gov.bcb.sisaps.src.dao.ParticipanteAgendaCorecDao;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAgendaAlteracaoCorec;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class AgendaCorecMediator {

    private static final String ENVIADO = " (enviado)";
    private static final String PREVISTO = " (previsto)";
    @Autowired
    private AgendaCorecDao agendaCorecDao;
    @Autowired
    private ParticipanteAgendaCorecDao participanteAgendaCorecDao;
    @Autowired
    private EmailCorecDao emailCorecDao;

    public static AgendaCorecMediator get() {
        return SpringUtils.get().getBean(AgendaCorecMediator.class);
    }

    public AgendaCorec buscarAgendaCorec(Integer pk) {
        return agendaCorecDao.buscarAgendaCorec(pk);
    }

    public AgendaCorec buscarAgendaCorecPorCiclo(Integer pk) {
        AgendaCorec agenda = agendaCorecDao.buscarAgendaCorecPorCiclo(pk);
        if (agenda != null) {
            if (agenda.getCiclo() != null) {
                Hibernate.initialize(agenda.getCiclo());
                Hibernate.initialize(agenda.getCiclo().getEntidadeSupervisionavel());
            }
            if (agenda.getParticipantes() != null) {
                Hibernate.initialize(agenda.getParticipantes());
            }

            if (agenda.getObservacoes() != null) {
                Hibernate.initialize(agenda.getObservacoes());
            }
        }
        return agenda;
    }

    @Transactional
    public String salvar(AgendaCorec agenda, List<ObservacaoAgendaCorec> observacoesExcluidas, Date dataPrevisaoCorec) {

        new RegraAgendaAlteracaoCorec(agenda, dataPrevisaoCorec).validar();

        if (!dataPrevisaoCorec.equals(agenda.getCiclo().getDataPrevisaoCorec())) {

            agenda.getCiclo().setDataPrevisaoCorec(dataPrevisaoCorec);
            agenda.setDataEnvioApresentacao(null);
            agenda.setDataEnvioDisponibilidade(null);
            CicloMediator.get().alterar(agenda.getCiclo());
            removerEmailDisponibilidadeParticipantes(agenda);
        }

        if (agenda.getObservacoes() != null) {
            for (ObservacaoAgendaCorec observacaoAgendaCorec : agenda.getObservacoes()) {
                if (observacaoAgendaCorec.getPk() == null) {
                    agenda.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
                    agenda.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
                } else {
                    observacaoAgendaCorec.setAlterarDataUltimaAtualizacao(false);
                }
            }
        }

        if (!Util.isNuloOuVazio(observacoesExcluidas)) {
            agenda.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
            agenda.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
        }

        if (agenda.getPk() == null) {
            agendaCorecDao.save(agenda);
        } else {
            agendaCorecDao.update(agenda);
        }

        ObservacaoAgendaCorecMediator.get().excluir(observacoesExcluidas);

        return "Agenda do comitê salva com sucesso.";
    }

    @Transactional
    public void removerEmailDisponibilidadeParticipantes(AgendaCorec agenda) {
        List<EmailCorec> emailParticipantes =
                EmailCorecMediator.get().buscarEmailParticipantePorTipo(agenda.getPk(),
                        TipoEmailCorecEnum.DISPONIBILIDADE);
        if (!Util.isNuloOuVazio(emailParticipantes)) {
            EmailCorecMediator.get().excluirEmailParticipantes(emailParticipantes);
        }
    }

    public boolean comiteARealizar(Ciclo ciclo) {
        return EstadoCicloEnum.EM_ANDAMENTO.equals(ciclo.getEstadoCiclo().getEstado())
                || EstadoCicloEnum.COREC.equals(ciclo.getEstadoCiclo().getEstado());
    }

    public String getDataEmailApresentacao(AgendaCorec agenda) {
        return getDataEmailPorTipo(agenda.getCiclo(), agenda.getDataEnvioApresentacao(),
                TipoEmailCorecEnum.APRESENTACAO);

    }

    public String getDataEmailDisponibilidade(AgendaCorec agenda) {
        return getDataEmailPorTipo(agenda.getCiclo(), agenda.getDataEnvioDisponibilidade(),
                TipoEmailCorecEnum.DISPONIBILIDADE);

    }

    public String getDataEmailPorTipo(Ciclo ciclo, DateTime data, TipoEmailCorecEnum tipo) {
        if (comiteARealizar(ciclo)) {
            if (data != null) {
                return Util.formatarData(data) + ENVIADO;
            } else {
                DateTime date = new DateTime(ciclo.getDataPrevisaoCorec());
                ParametroEmail parametro = ParametroEmailMediator.get().buscarPorTipo(tipo);
                if (parametro.getPrazo() != null) {
                    date = date.minusDays(parametro.getPrazo());
                    return Util.formatar(new LocalDate(date)) + PREVISTO;
                }
                return "";
            }

        } else {
            return Util.formatarData(data);
        }
    }

    public boolean dataVencida(Ciclo ciclo, DateTime data, TipoEmailCorecEnum tipo) {
        if (comiteARealizar(ciclo)) {
            if (data == null) {
                DateTime date = new DateTime(ciclo.getDataPrevisaoCorec());
                ParametroEmail parametro = ParametroEmailMediator.get().buscarPorTipo(tipo);
                date = date.minusDays(parametro.getPrazo());
                int result = date.compareTo(DataUtil.getDateTimeAtual());
                if (result == 0 || result == -1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    };

    public void excluirDataAssinaturaParticipantes(Integer pkCiclo) {
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(pkCiclo);
        if (agenda != null) {
            List<ParticipanteAgendaCorec> listaParticipantes =
                    ParticipanteAgendaCorecMediator.get().buscarParticipanteAgendaCorec(agenda.getPk());
            for (ParticipanteAgendaCorec participanteAgendaCorec : listaParticipantes) {
                if (participanteAgendaCorec.getAssinatura() != null) {
                    participanteAgendaCorec.setAssinatura(null);
                    participanteAgendaCorecDao.update(participanteAgendaCorec);
                }

                EmailCorec emailParticipante =
                        EmailCorecMediator.get().retornarDataSolicitacaoAssinatura(agenda.getPk(),
                                participanteAgendaCorec.getMatricula());
                if (emailParticipante != null) {
                    emailParticipante.setUltimaAtualizacao(null);
                    emailCorecDao.delete(emailParticipante);
                }

            }
            participanteAgendaCorecDao.flush();
            emailCorecDao.flush();
        }
    }

    public void ataNaoAssinadaParticipante(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        if (agenda != null) {
            for (ParticipanteAgendaCorec participanteAgendaCorec : agenda.getParticipantes()) {
                if (participanteAgendaCorec.getAssinatura() == null) {
                    SisapsUtil.adicionarErro(erros, new ErrorMessage(
                            "Todos os participantes efetivos do comitê devem assinar a Ata."));
                    break;
                }
            }
        }
    }

    @Transactional
    public void save(AgendaCorec agendaCorec) {
        agendaCorecDao.save(agendaCorec);
    }
    
    @Transactional
    public void saveOrUpdate(AgendaCorec agendaCorec) {
        agendaCorecDao.saveOrUpdate(agendaCorec);
    }

}
