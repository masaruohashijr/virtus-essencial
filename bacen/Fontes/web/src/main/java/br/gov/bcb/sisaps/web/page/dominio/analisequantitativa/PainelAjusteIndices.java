package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.configureTabela;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAjusteIndices extends PainelQuadroPosicaoFinanceiraComum {

    private static final String AJUSTES_INDICES = "ajustesIndices";

    public PainelAjusteIndices(String id, QuadroPosicaoFinanceiraVO novoQuadroVO, PerfilRisco perfilRiscoAtual) {
        super(id, novoQuadroVO, perfilRiscoAtual);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        new BuildTabelaIndices(AJUSTES_INDICES).criarTabela(this, novoQuadroVO,
                configureTabela("Índices", AJUSTES_INDICES), true);
        labelInfo = new LabelInformacoesNaoSalvas("labelInfoNaoSalvasIndices") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }
        };
        addOrReplace(labelInfo);
        BotaoSalvarInformacoes botaoSalvarInformacoes = new BotaoSalvarInformacoes() {
            @Override
            protected void executarSubmissao(AjaxRequestTarget target) {
                QuadroPosicaoFinanceiraMediator.get().salvarAjusteIndices(novoQuadroVO);
                GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                        (GerenciarQuadroPosicaoFinanceira) getPage();
                setInformacoesNaoSalvas(Boolean.FALSE);
                gerenciarQuadroPosicaoFinanceira.lancarInfoAjax("Os ajustes de índices foram salvos com sucesso.",
                        PainelAjusteIndices.this, target);

            }
        };
        botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + "Indices");
        addOrReplace(botaoSalvarInformacoes);
        setVisibilityAllowed(CollectionUtils.isNotEmpty(novoQuadroVO.getIndices()));
    }
}
