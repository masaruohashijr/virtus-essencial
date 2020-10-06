package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelJustificativaAqt extends Panel {

    private AnaliseQuantitativaAQT aqt;

    public PainelJustificativaAqt(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        AvaliacaoAQT avaliacaoANEF = aqt.getAvaliacaoANEF();
        addJustificativa(avaliacaoANEF);
    }

    private void addJustificativa(AvaliacaoAQT avaliacaoANEF) {
        Label justifi =
                new LabelLinhas("idJustificativa", avaliacaoANEF == null ? ""
                        : avaliacaoANEF.getJustificativa() == null ? "" : avaliacaoANEF.getJustificativa()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(true);
                    }
                };
        justifi.setEscapeModelStrings(false);
        addOrReplace(justifi);

    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public void setAqt(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

}
