package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivelDetalhar;

public class PainelDetalhesAQT extends PainelSisAps {

	private static final String COREC = " (Corec)";
    private AnaliseQuantitativaAQT aqt;
    private boolean possuiCorec;
    private boolean isPerfilRiscoAtual;

    public PainelDetalhesAQT(String id, AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        super(id);
        this.aqt = aqt;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        possuiCorec = !getNotaCorec().isEmpty();
        addOrReplace(new Label("idNomeDoGrupo", new PropertyModel<String>(aqt, "parametroAQT.descricao")));
        novaNotaAQT();
        AvaliacaoAQT ajuste = aqt.getAvaliacaoANEF();
        Label novaNotaAjustada =
                new Label("idNovaNotaAnefAjustada", ajuste == null || ajuste.getJustificativa() == null ? "" : ajuste
                        .getParametroNota().getDescricaoValor());
        addOrReplace(novaNotaAjustada);
        grupo(ajuste);
        addOrReplace(new Label("idNotaCorec", getNotaCorec()).setVisibilityAllowed(possuiCorec));
    }
    
    private String getNotaCorec() {
    	String notaCorecAjustada =
                AnaliseQuantitativaAQTMediator.get().notaAjustadaFinal(aqt, getPerfilPorPagina(), isPerfilRiscoAtual);
        if (notaCorecAjustada.contains(COREC)) {
        	return notaCorecAjustada.replace(COREC, Constantes.ESPACO_EM_BRANCO);
        } else {
        	return "";
        }
    }

    private void novaNotaAQT() {
        addOrReplace(new Label("idNovaNotaAnef", ((ConsultaAQT) getPaginaAtual()).getPainelResumoAnefs()
                .getNotaCalculadaFinalSupervisor()));
    }

    private void grupo(AvaliacaoAQT ajuste) {
        PainelJustificativaAqt painelConsulta = new PainelJustificativaAqt("idPainelNota", aqt);
        GrupoExpansivelDetalhar grupo =
                new GrupoExpansivelDetalhar("GrupoEspansivelNota", "Nota do ANEF ajustada", false,
                        new Component[] {painelConsulta}) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getMarkupIdControle() {
                        return "bttExpandirNotaAjustada";
                    }

                    @Override
                    public String getMarkupIdTitulo() {
                        return "bttExpandir";
                    }

                };

        grupo.setOutputMarkupId(true);
        grupo.setMarkupId(grupo.getMarkupId() + aqt.getPk());

        boolean ajusteDiferenteVazio =
                ajuste != null && (ajuste.getJustificativa() != null && (ajuste.getParametroNota() != null));
        grupo.setVisibilityAllowed(ajusteDiferenteVazio && !possuiCorec);
        addOrReplace(painelConsulta);
        addOrReplace(grupo);
    }

    public void setAqt(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

}
