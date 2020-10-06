package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAjusteResultadoNovo extends PainelQuadroPosicaoFinanceiraComum {
    

    public PainelAjusteResultadoNovo(String id, QuadroPosicaoFinanceiraVO novoQuadroVO) {
        super(id, novoQuadroVO, null);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addOrReplace(new TabelaResultadosQuadro("ajustesResultados", novoQuadroVO, true, false));
        addLabelInfo();
        addBotaoSalvar();
        setVisibilityAllowed(CollectionUtils.isNotEmpty(novoQuadroVO.getNomesResultados()));
    }

    private void addLabelInfo() {
        labelInfo = new LabelInformacoesNaoSalvas("labelInfoNaoSalvasResultados") {
            @Override
            public boolean isVisibleAllowed() {
                return isInformacoesNaoSalvas();
            }
        };
        addOrReplace(labelInfo);
    }
    
    private void addBotaoSalvar() {
        BotaoSalvarInformacoes botaoSalvarInformacoes = new BotaoSalvarInformacoes() {
            @Override
            protected void executarSubmissao(AjaxRequestTarget target) {
                OutraInformacaoQuadroPosicaoFinanceiraMediator.get().salvarAjustesOutrasInformacoes(
                        novoQuadroVO, TipoInformacaoEnum.RESULTADO);
                setInformacoesNaoSalvas(Boolean.FALSE);
                GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                        (GerenciarQuadroPosicaoFinanceira) getPage();
                gerenciarQuadroPosicaoFinanceira.lancarInfoAjax(
                        "Os ajustes de resultados foram salvos com sucesso.",
                        PainelAjusteResultadoNovo.this, target);
            }
        };
        botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + "Resultados");
        addOrReplace(botaoSalvarInformacoes);
    }

}
