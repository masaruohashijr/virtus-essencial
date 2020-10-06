package crt2.dominio.analisequantitativa.analisarconcluiranef;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AvaliacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AnalisarConcluirANEF extends ConfiguracaoTestesNegocio {

    private Integer anef;
    private Integer nota;
    private Integer notae;
    private String justificativa;
    private Integer elemento;
    private String analise;

    public String mensagemEsperadaAoSalvar() {
        try {
            erro = null;
            AnaliseQuantitativaAQT anefBase = AnaliseQuantitativaAQTMediator.get().buscar(anef);
            if (nota != 1000) {
                AvaliacaoAQT avaliacaoAQT =
                        AvaliacaoAQTMediator.get().buscarNotaAvaliacaoANEF(anefBase, PerfisNotificacaoEnum.SUPERVISOR);
                if (avaliacaoAQT == null) {
                    avaliacaoAQT = new AvaliacaoAQT();
                    avaliacaoAQT.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
                }
                avaliacaoAQT.setJustificativa(justificativa);
                ParametroNotaAQT paramenNotaAQT = ParametroNotaAQTMediator.get().buscarPorPK(nota);
                avaliacaoAQT.setParametroNota(paramenNotaAQT);
               
                if (anefBase.getAvaliacoesAnef().isEmpty()) {
                    anefBase.getAvaliacoesAnef().add(avaliacaoAQT);
                }
                AvaliacaoAQTMediator.get().salvarAterarAvalicaoSupervisor(anefBase, avaliacaoAQT);
            }

            ParametroNotaAQT paramenNotaElementoAQT = ParametroNotaAQTMediator.get().buscarPorPK(notae);
            if (elemento != null) {
                for (ElementoAQT ele : anefBase.getElementos()) {
                    if (ele.getPk().equals(elemento)) {
                        ele.setJustificativaSupervisor(analise);
                        ele.setParametroNotaSupervisor(paramenNotaElementoAQT);
                        ElementoAQTMediator.get().salvarNovaNotaElementoAQTSupervisor(anefBase, ele, true, true);
                        break;
                    }
                }
            }

            AnaliseQuantitativaAQTMediator.get().concluirAnaliseANEFSupervisor(anefBase, perfilUsuario());
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();
    }

    public Integer getAnef() {
        return anef;
    }

    public void setAnef(Integer anef) {
        this.anef = anef;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }
    
    public Integer getNotae() {
        return notae;
    }

    public void setNotae(Integer notae) {
        this.notae = notae;
    }

	public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Integer getElemento() {
        return elemento;
    }

    public void setElemento(Integer elemento) {
        this.elemento = elemento;
    }

    public String getAnalise() {
        return analise;
    }

    public void setAnalise(String analise) {
        this.analise = analise;
    }

}
