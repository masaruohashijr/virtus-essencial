package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelQuadroPosicaoFinanceiraComum extends Panel {

    protected QuadroPosicaoFinanceiraVO novoQuadroVO;
    protected PerfilRisco perfilRiscoAtual;
    protected Label labelInfo;
    protected Boolean informacoesNaoSalvas = Boolean.FALSE;

    public PainelQuadroPosicaoFinanceiraComum(String id, QuadroPosicaoFinanceiraVO novoQuadroVO,
            PerfilRisco perfilRiscoAtual) {
        super(id);
        setMarkupId(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.novoQuadroVO = novoQuadroVO;
        this.perfilRiscoAtual = perfilRiscoAtual;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    public Label getLabelInfo() {
        return labelInfo;
    }

    public Boolean isInformacoesNaoSalvas() {
        return informacoesNaoSalvas;
    }

    public void setInformacoesNaoSalvas(Boolean informacoesNaoSalvas) {
        this.informacoesNaoSalvas = informacoesNaoSalvas;
    }
}
