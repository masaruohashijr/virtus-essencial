package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.configureTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.getFormatoCabecalho;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.OutraInformacaoVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraNumericaBehaviour;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.FormatoColunaBold;

public class TabelaPatrimoniosIndicesQuadro extends Panel {
    
    private static final String WIDTH_15 = "width: 15%;";

    private Map<OutraInformacaoVO, Label> mapLabel = new HashMap<OutraInformacaoVO, Label>();
    private Map<OutraInformacaoVO, ColunaFragmentValorAjusteAjustado<OutraInformacaoVO>> mapFragmento = 
            new HashMap<OutraInformacaoVO, ColunaFragmentValorAjusteAjustado<OutraInformacaoVO>>();

    private final QuadroPosicaoFinanceiraVO novoQuadroVO;

    private final TipoInformacaoEnum tipoInformacao;

    private final boolean isEdicao;

    public TabelaPatrimoniosIndicesQuadro(String id, QuadroPosicaoFinanceiraVO novoQuadroVO, 
            TipoInformacaoEnum tipoInformacao, boolean isEdicao) {
        super(id);
        this.novoQuadroVO = novoQuadroVO;
        this.tipoInformacao = tipoInformacao;
        this.isEdicao = isEdicao;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addTabela();
    }
    
    private void addTabela() {
        String titulo = getTitulo();
        Configuracao configuracao = configureTabela(titulo, getId());
        if (!isEdicao) {
            configuracao.setExibirTitulo(false);
        }
        List<Coluna<OutraInformacaoVO>> colunas = obterColunas();
        ProviderGenericoList<OutraInformacaoVO> provider = criarProvider();
        Tabela<OutraInformacaoVO> tabela = 
                new Tabela<OutraInformacaoVO>("tabela", configuracao, colunas, provider, false);
        addOrReplace(tabela);
    }

    private String getTitulo() {
        String titulo = "";
        switch (tipoInformacao) {
            case PATRIMONIO:
                titulo = "Patrimônios de referência";
                break;
            case INDICE:
                titulo = "Índices";
                break;
            default:
                break;
        }
        return titulo;
    }

    private ProviderGenericoList<OutraInformacaoVO> criarProvider() {
        return new ProviderGenericoList<OutraInformacaoVO>("", SortOrder.ASCENDING, obterModel());
    }

    private IModel<List<OutraInformacaoVO>> obterModel() {
        IModel<List<OutraInformacaoVO>> model =
                new AbstractReadOnlyModel<List<OutraInformacaoVO>>() {
            @Override
            public List<OutraInformacaoVO> getObject() {
                switch (tipoInformacao) {
                    case PATRIMONIO:
                        return novoQuadroVO.getPatrimoniosNovo();
                    case INDICE:
                        return novoQuadroVO.getIndicesNovo();
                    default:
                        break;
                }
                return new ArrayList<OutraInformacaoVO>();
            }
        };
        return model;
    }

    private List<Coluna<OutraInformacaoVO>> obterColunas() {
        TipoEdicaoInformacaoEnum tipoEdicao = obterTipoEdicao();
        String larguraColuna = "width: 55%;";
        
        boolean temValorAjustado = novoQuadroVO.existeValorEditado(tipoInformacao, null);
        
        boolean exibirColunaAjustado = (isEdicao && tipoEdicao.equals(TipoEdicaoInformacaoEnum.AJUSTE)) 
                || (!isEdicao && temValorAjustado);
        
        if (exibirColunaAjustado) {
            larguraColuna = "width: 70%;";
        }
        List<Coluna<OutraInformacaoVO>> colunas = new LinkedList<Coluna<OutraInformacaoVO>>();
        colunas.add(new Coluna<OutraInformacaoVO>()
                .setEstiloCabecalho("background-color:#fff; color:#000; " + larguraColuna)
                .setCabecalho("").setFormatador(new FormatoColunaBold<OutraInformacaoVO>())
                .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getDescricao())));
        colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                .setCabecalho(tipoInformacao.equals(TipoInformacaoEnum.INDICE) ? "Índice" : "Valor")
                .setPropriedadeTela(property(getPropertyObject(OutraInformacaoVO.class).getValorFormatado()))
                .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getValor())));
        if (isEdicao) {
            colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                    .setCabecalho(tipoEdicao.getDescricao())
                    .setComponente(new ColunaEdicao())
                    .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getValorEditadoFormatado())));
        }
        
        if (exibirColunaAjustado) {
            colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                    .setCabecalho(TipoEdicaoInformacaoEnum.AJUSTADO.getDescricao())
                    .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getValorAjustado()))
                    .setComponente(new IColunaComponente<OutraInformacaoVO>() {
                        @Override
                        public Component obterComponente(Item<ICellPopulator<OutraInformacaoVO>> cellItem,
                                String componentId, final IModel<OutraInformacaoVO> rowModel) {
                            return criarComponente(componentId, rowModel);
                        }
                    }));
        }
        return colunas;
    }

    private TipoEdicaoInformacaoEnum obterTipoEdicao() {
        TipoEdicaoInformacaoEnum tipoEdicao = TipoEdicaoInformacaoEnum.AJUSTADO;
        if (tipoInformacao.equals(TipoInformacaoEnum.PATRIMONIO) 
                && CollectionUtils.isNotEmpty(novoQuadroVO.getPatrimoniosNovo())) {
            tipoEdicao = novoQuadroVO.getPatrimoniosNovo().get(0).getTipoEdicaoInformacaoEnum();
        } else if (tipoInformacao.equals(TipoInformacaoEnum.INDICE) 
                && CollectionUtils.isNotEmpty(novoQuadroVO.getIndicesNovo())) {
            tipoEdicao = novoQuadroVO.getIndicesNovo().get(0).getTipoEdicaoInformacaoEnum();
        }
        return tipoEdicao;
    }
    
    private Component criarComponente(String componentId, final IModel<OutraInformacaoVO> rowModel) {
        IModel<String> modelValorAjustado = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return rowModel.getObject().getValorAjustadoFormatado();
            }

        };
        Label labelAjustado = new Label(componentId, modelValorAjustado);
        mapLabel.put(rowModel.getObject(), labelAjustado);
        return mapLabel.get(rowModel.getObject());
    }
    
    private class ColunaEdicao implements IColunaComponente<OutraInformacaoVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<OutraInformacaoVO>> cellItem, String componentId,
                final IModel<OutraInformacaoVO> rowModel) {
            ColunaFragmentEdicao colunaFragment =
                    new ColunaFragmentEdicao(componentId, "fragmentInput", TabelaPatrimoniosIndicesQuadro.this, rowModel,
                            property(getPropertyObject(OutraInformacaoVO.class).getValorEditado()));
            mapFragmento.put(rowModel.getObject(), colunaFragment);
            return mapFragmento.get(rowModel.getObject());
        }
    }
    
    private class ColunaFragmentEdicao extends ColunaFragmentValorAjusteAjustado<OutraInformacaoVO> {
        private IModel<OutraInformacaoVO> rowModel;

        public ColunaFragmentEdicao(String id, String markupId, MarkupContainer markupProvider,
                IModel<OutraInformacaoVO> rowModel, String expressao) {
            super(id, markupId, markupProvider, rowModel.getObject(), expressao);
            this.rowModel = rowModel;
        }

        @Override
        protected void executorOnUpdateExterno(AjaxRequestTarget target) {
            super.executorOnUpdateExterno(target);
            int temAsterisco = 0;
            for (ColunaFragmentValorAjusteAjustado<?> frag : mapFragmento.values()) {
                if (frag.getIsMostrar()) {
                    temAsterisco++;
                }
            }
            prepararTargetAjuste(getTextField(), rowModel, target, temAsterisco > 0);
        }

        @Override
        protected void onAddConfiguracaoTextField(TextField<OutraInformacaoVO> textFieldCustomizado) {
            boolean permiteNegativo = false;
            if (getObjeto().getTipoEdicaoInformacaoEnum().equals(TipoEdicaoInformacaoEnum.AJUSTE)) {
                permiteNegativo = true;
            }
            textFieldCustomizado.setMarkupId("inputAjuste" 
                    + getObjeto().getDescricaoPeriodo().replace(Constantes.ESPACO_EM_BRANCO, Constantes.VAZIO));
            textFieldCustomizado.add(new MascaraNumericaBehaviour(getObjeto().obterFormato(false), permiteNegativo));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <C> IConverter<C> getConverterTextField(Class<C> type) {
            return (IConverter<C>) new FormatarNumericoBigDecimal(getObjeto().obterFormato(true));
        }
    }
    
    private void prepararTargetAjuste(TextField<OutraInformacaoVO> textField, IModel<OutraInformacaoVO> rowModel,
            AjaxRequestTarget target, boolean isMostrarInformacoesNaoSalvas) {
        GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) textField.getPage();
        PainelQuadroPosicaoFinanceiraComum painel = null;
        switch (tipoInformacao) {
            case PATRIMONIO:
                painel = pageAtual.getPainelAjustePatrimonioNovo();
                break;
            case INDICE:
                painel = pageAtual.getPainelAjusteIndicesNovo();
                break;
            default:
                break;
        }
        if (rowModel.getObject().getTipoEdicaoInformacaoEnum().equals(TipoEdicaoInformacaoEnum.AJUSTE)) {
            Label lb = mapLabel.get(rowModel.getObject());
            target.add(lb, lb.getMarkupId());
        }
        if (isMostrarInformacoesNaoSalvas) {
            painel.setInformacoesNaoSalvas(Boolean.TRUE);
        } else {
            painel.setInformacoesNaoSalvas(Boolean.FALSE);
        }
        target.add(painel.getLabelInfo());
        target.add(pageAtual.getPainelQuadroPosicaoFinanceira());
    }

}
