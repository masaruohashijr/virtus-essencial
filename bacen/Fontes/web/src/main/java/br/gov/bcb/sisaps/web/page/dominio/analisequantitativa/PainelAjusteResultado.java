package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.configureTabela;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAjusteResultado extends PainelQuadroPosicaoFinanceiraComum {

    private static final String RESULTADOS = "Resultados";
    private static final String AJUSTES_RESULTADOS = "ajustesResultados";

    public PainelAjusteResultado(String id, QuadroPosicaoFinanceiraVO novoQuadroVO) {
        super(id, novoQuadroVO, null);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        new BuildTabelaResultados(AJUSTES_RESULTADOS).criarTabela(this, novoQuadroVO,
                configureTabela(RESULTADOS, AJUSTES_RESULTADOS), true);
        labelInfo = new LabelInformacoesNaoSalvas("labelInfoNaoSalvasResultados") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }
        };
        addOrReplace(labelInfo);
        BotaoSalvarInformacoes botaoSalvarInformacoes = new BotaoSalvarInformacoes() {
            @Override
            protected void executarSubmissao(AjaxRequestTarget target) {
                QuadroPosicaoFinanceiraMediator.get().salvarAjusteResultado(novoQuadroVO);
                GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                        (GerenciarQuadroPosicaoFinanceira) getPage();
                setInformacoesNaoSalvas(Boolean.FALSE);
                gerenciarQuadroPosicaoFinanceira.lancarInfoAjax("Os ajustes de resultados foram salvos com sucesso.",
                        PainelAjusteResultado.this, target);
            }
        };
        botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + RESULTADOS);
        addOrReplace(botaoSalvarInformacoes);
        setVisibilityAllowed(CollectionUtils.isNotEmpty(novoQuadroVO.getResultados()));
    }

}
