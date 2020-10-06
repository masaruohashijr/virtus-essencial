package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelAjusteIndicesNovo extends PainelQuadroPosicaoFinanceiraComum {

    public PainelAjusteIndicesNovo(String id, QuadroPosicaoFinanceiraVO novoQuadroVO, PerfilRisco perfilRiscoAtual) {
        super(id, novoQuadroVO, perfilRiscoAtual);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addOrReplace(new TabelaPatrimoniosIndicesQuadro("ajustesIndices", novoQuadroVO, TipoInformacaoEnum.INDICE, true));
        addLabelInfo();
        addBotaoSalvar();
        setVisibilityAllowed(CollectionUtils.isNotEmpty(novoQuadroVO.getIndicesNovo()));
    }

    private void addLabelInfo() {
        labelInfo = new LabelInformacoesNaoSalvas("labelInfoNaoSalvasIndices") {
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
                        novoQuadroVO, TipoInformacaoEnum.INDICE);
                setInformacoesNaoSalvas(Boolean.FALSE);
                GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                        (GerenciarQuadroPosicaoFinanceira) getPage();
                gerenciarQuadroPosicaoFinanceira.lancarInfoAjax("Os ajustes de índices foram salvos com sucesso.",
                        PainelAjusteIndicesNovo.this, target);
                
            }
        };
        botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + "Indices");
        addOrReplace(botaoSalvarInformacoes);
    }
}
