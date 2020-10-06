package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelJustificativaArc extends Panel {

    private AvaliacaoRiscoControle avaliacao;

    public PainelJustificativaArc(String id, AvaliacaoRiscoControle avaliacao) {
        super(id);
        this.avaliacao = avaliacao;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        AvaliacaoARC avaliacaoArc = avaliacao.getAvaliacaoARC();
        addJustificativa(avaliacaoArc);
    }

    private void addJustificativa(AvaliacaoARC avaliacaoArc) {
        Label justifi =
                new LabelLinhas("idJustificativa", avaliacaoArc == null ? ""
                        : avaliacaoArc.getJustificativa() == null ? "" : avaliacaoArc.getJustificativa()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(true);
                    }
                };
        justifi.setEscapeModelStrings(false);
        addOrReplace(justifi);

    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

}
