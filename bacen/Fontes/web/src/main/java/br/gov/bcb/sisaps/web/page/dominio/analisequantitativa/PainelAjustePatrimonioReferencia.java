package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.configureTabela;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAjustePatrimonioReferencia extends PainelQuadroPosicaoFinanceiraComum {

    private static final String AJUSTE_PATRIMONIO_REFERENCIA = "ajustePatrimonioReferencia";

    public PainelAjustePatrimonioReferencia(String id, QuadroPosicaoFinanceiraVO novoQuadroVO,
            PerfilRisco perfilRiscoAtual) {
        super(id, novoQuadroVO, perfilRiscoAtual);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        new BuildTabelaPatrimonioReferencia(AJUSTE_PATRIMONIO_REFERENCIA).criarTabela(this, novoQuadroVO,
                configureTabela("Patrimônios de referência", AJUSTE_PATRIMONIO_REFERENCIA), true);
        labelInfo = new LabelInformacoesNaoSalvas("labelInfoNaoSalvasPatrimonio") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }

        };
        addOrReplace(labelInfo);
        BotaoSalvarInformacoes botaoSalvarInformacoes = new BotaoSalvarInformacoes() {
            @Override
            protected void executarSubmissao(AjaxRequestTarget target) {
                QuadroPosicaoFinanceiraMediator.get().salvarAjustePatrimonios(novoQuadroVO);
                setInformacoesNaoSalvas(Boolean.FALSE);
                GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                        (GerenciarQuadroPosicaoFinanceira) getPage();
                gerenciarQuadroPosicaoFinanceira.lancarInfoAjax(
                        "Os ajustes de patrimônios de referência foram salvos com sucesso.",
                        PainelAjustePatrimonioReferencia.this, target);
            }
        };
        botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + "Patrimonios");
        addOrReplace(botaoSalvarInformacoes);
        setVisibilityAllowed(CollectionUtils.isNotEmpty(novoQuadroVO.getPatrimonios()));
    }
}
