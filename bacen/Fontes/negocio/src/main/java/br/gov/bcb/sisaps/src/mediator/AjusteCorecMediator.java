package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AjusteCorecDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;

@Service
@Transactional(readOnly = true)
public class AjusteCorecMediator {

    @Autowired
    private AjusteCorecDAO ajusteCorecDao;

    @Autowired
    private NotaMatrizMediator notaMatrizMediator;

    @Autowired
    private NotaAjustadaAEFMediator notaAjustadaAEFMediator;

    public static AjusteCorecMediator get() {
        return SpringUtils.get().getBean(AjusteCorecMediator.class);
    }

    public AjusteCorec buscarPorCiclo(Integer pkCiclo) {
        return ajusteCorecDao.buscarPorCiclo(pkCiclo);
    }

    public AjusteCorec buscarPorCiclo(Ciclo ciclo) {
        AjusteCorec ajuste = ajusteCorecDao.buscarPorCiclo(ciclo.getPk());
        if (ajuste == null) {
            ajuste = new AjusteCorec();
            ajuste.setCiclo(ciclo);
        }
        return ajuste;
    }

    public String salvarAjusteCorec(AjusteCorec ajusteCorec) {
        ajusteCorecDao.saveOrUpdate(ajusteCorec);
        ajusteCorecDao.flush();
        return "Ajustes salvos com sucesso.";
    }

    public ParametroNotaAQT notaAjustadaCorecAEF(PerfilRisco perfilRisco, Ciclo ciclo, PerfilAcessoEnum perfilMenu) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        AjusteCorec notaCorecCicloAnterior = notaMatrizMediator.notaCicloAnterior(ciclo);
        AjusteCorec notaCorecCicloAtual = notaMatrizMediator.notaCicloAtual(ciclo);
        NotaAjustadaAEF notaAjustadaSupervisor = notaAjustadaAEFMediator.buscarNotaAjustadaAEF(ciclo, perfilRisco);
        boolean diferenteSupervisorEGerente =
                !PerfilAcessoEnum.SUPERVISOR.equals(perfilMenu) && !PerfilAcessoEnum.GERENTE.equals(perfilMenu);
        if (cicloAndamentoCorecDiferenteSupervisor(ciclo, diferenteSupervisorEGerente)) {
            if (notaAjustadaSupervisor == null && possuiNotaAjustadaQuantitativa(notaCorecCicloAnterior)) {
                return notaCorecCicloAnterior.getNotaQuantitativa();
            }
        } else if (perfilRiscoAtual.getPk().equals(perfilRisco.getPk())) {
            if (possuiNotaAjustadaQuantitativa(notaCorecCicloAtual)) {
                return notaCorecCicloAtual.getNotaQuantitativa();
            }

            if (notaAjustadaSupervisor == null && possuiNotaAjustadaQuantitativa(notaCorecCicloAnterior)) {
                return notaCorecCicloAnterior.getNotaQuantitativa();
            }
        }
        return null;
    }

    private boolean cicloAndamentoCorecDiferenteSupervisor(Ciclo ciclo, boolean diferenteSupervisorEGerente) {
        return (EstadoCicloEnum.COREC.equals(ciclo.getEstadoCiclo().getEstado()) && diferenteSupervisorEGerente)
                || EstadoCicloEnum.EM_ANDAMENTO.equals(ciclo.getEstadoCiclo().getEstado());
    }

    public ParametroNota notaAjustadaCorecMatriz(PerfilRisco perfilRisco, Ciclo ciclo, PerfilAcessoEnum perfilMenu,
            boolean isPerfilRiscoAtual) {

        AjusteCorec notaCorecCicloAnterior = notaMatrizMediator.notaCicloAnterior(ciclo);
        AjusteCorec notaCorecCicloAtual = notaMatrizMediator.notaCicloAtual(ciclo);
        NotaMatriz notaAjustadaSupervisor = notaMatrizMediator.buscarPorPerfilRisco(perfilRisco.getPk());
        boolean diferenteSupervisorEGerente =
                !PerfilAcessoEnum.SUPERVISOR.equals(perfilMenu) && !PerfilAcessoEnum.GERENTE.equals(perfilMenu);
        if (cicloAndamentoCorecDiferenteSupervisor(ciclo, diferenteSupervisorEGerente)) {
            if (notaAjustadaSupervisor == null && possuiNotaAjustadaQualitativa(notaCorecCicloAnterior)) {
                return notaCorecCicloAnterior.getNotaQualitativa();
            }
        } else {
            if (possuiNotaAjustadaQualitativa(notaCorecCicloAtual) && isPerfilRiscoAtual) {
                return notaCorecCicloAtual.getNotaQualitativa();
            }

            if (notaAjustadaSupervisor == null && possuiNotaAjustadaQualitativa(notaCorecCicloAnterior)) {
                return notaCorecCicloAnterior.getNotaQualitativa();
            }
        }
        return null;
    }

    public boolean possuiNotaAjustadaQuantitativa(AjusteCorec notaCorec) {
        return notaCorec != null && notaCorec.getNotaQuantitativa() != null;
    }

    public boolean possuiNotaAjustadaQualitativa(AjusteCorec notaCorec) {
        return notaCorec != null && notaCorec.getNotaQualitativa() != null;
    }

    public boolean possuiNotaAjustadaES(AjusteCorec notaCorec) {
        return notaCorec != null && (notaCorec.getNotaFinal() != null || notaCorec.getGrauPreocupacao() != null);
    }

    @Transactional
    public void atualizarDadosNovaMetodologia(AjusteCorec ajusteCorec, Metodologia metodologia) {
        if (ajusteCorec.getNotaQualitativa() != null) {
            ParametroNota notaQualitativa = ParametroNotaMediator.get().buscarPorDescricao(metodologia,
                    ajusteCorec.getNotaQualitativa().getDescricaoValor());
            ajusteCorec.setNotaQualitativa(notaQualitativa);
        }
        if (ajusteCorec.getNotaQuantitativa() != null) {
            ParametroNotaAQT notaQuantitativa = ParametroNotaAQTMediator.get().buscarPorDescricao(metodologia,
                    ajusteCorec.getNotaQuantitativa().getDescricaoValor());
            ajusteCorec.setNotaQuantitativa(notaQuantitativa);
        }
        if (ajusteCorec.getGrauPreocupacao() != null) {
            ParametroNota notaFinal = ParametroNotaMediator.get().buscarPorDescricao(metodologia,
                    ajusteCorec.getGrauPreocupacao().getDescricao());
            ajusteCorec.setNotaFinal(notaFinal);
            ajusteCorec.setGrauPreocupacao(null);
        }
        ajusteCorec.setAlterarDataUltimaAtualizacao(false);
        ajusteCorecDao.update(ajusteCorec);
    }

    @Transactional(readOnly = true)
    public String notaAjustadaCorecES(PerfilRisco perfilRisco, Ciclo ciclo, PerfilAcessoEnum perfilMenu) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        AjusteCorec notaCorecCicloAnterior = notaMatrizMediator.notaCicloAnterior(ciclo);
        AjusteCorec notaCorecCicloAtual = notaMatrizMediator.notaCicloAtual(ciclo);
        boolean diferenteSupervisorEGerente =
                !PerfilAcessoEnum.SUPERVISOR.equals(perfilMenu) && !PerfilAcessoEnum.GERENTE.equals(perfilMenu);
        if (cicloAndamentoCorecDiferenteSupervisor(ciclo, diferenteSupervisorEGerente)) {
            if (GrauPreocupacaoESMediator.get().exibirCorecAnteriorNotaFinalES(perfilRisco)) {
                return notaCorecCicloAnterior.getDescricaoNotaFinal();
            }
        } else if (perfilRiscoAtual.getPk().equals(perfilRisco.getPk())) {
            if (possuiNotaAjustadaES(notaCorecCicloAtual)) {
                return notaCorecCicloAtual.getDescricaoNotaFinal();
            }

            if (GrauPreocupacaoESMediator.get().exibirCorecAnteriorNotaFinalES(perfilRisco)) {
                return notaCorecCicloAnterior.getDescricaoNotaFinal();
            }
        }
        return null;
    }
}
