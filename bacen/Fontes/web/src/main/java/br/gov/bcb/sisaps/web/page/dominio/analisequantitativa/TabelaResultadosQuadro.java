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
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class TabelaResultadosQuadro extends Panel {
    
    private static final String FUNDO_PADRAO_A_CLARO3_ALTURA_LINHA = "fundoPadraoAClaro3 alturaLinha";
    private List<String> periodos;
    private Map<OutraInformacaoVO, Label> mapLabel = new HashMap<OutraInformacaoVO, Label>();
    private Map<OutraInformacaoVO, ColunaFragmentValorAjusteAjustado<OutraInformacaoVO>> mapFragmento = 
            new HashMap<OutraInformacaoVO, ColunaFragmentValorAjusteAjustado<OutraInformacaoVO>>();

    private final QuadroPosicaoFinanceiraVO novoQuadroVO;

    private final boolean isEdicao;
    private final boolean isApresentacao;

    public TabelaResultadosQuadro(String id, QuadroPosicaoFinanceiraVO novoQuadroVO, 
            boolean isEdicao, boolean isApresentacao) {
        super(id);
        this.novoQuadroVO = novoQuadroVO;
        this.isEdicao = isEdicao;
        this.isApresentacao = isApresentacao;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer container = new WebMarkupContainer("tabelaResultadosQuadro");
        container.setMarkupId(getId());
        addOrReplace(container);
        setPeriodos();
        addPeriodos(container);
        addResultados(container);
    }
    
    private void setPeriodos() {
        periodos = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(novoQuadroVO.getNomesResultados())) {
            List<OutraInformacaoVO> resultados = novoQuadroVO.getResultadosPorNome().get(
                    novoQuadroVO.getNomesResultados().get(0));
            for (OutraInformacaoVO resultado : resultados) {
                periodos.add(resultado.getPeriodoFormatado());
            }
        }
    }

    private void addPeriodos(WebMarkupContainer container) {
        container.addOrReplace(new ListView<String>("periodos", periodos) {
            @Override
            protected void populateItem(ListItem<String> item) {
                WebMarkupContainer linhaPeriodo = new WebMarkupContainer("linhaPeriodo");
                if (isApresentacao) {
                    linhaPeriodo.add(new AttributeAppender(ConstantesWeb.STYLE, "height: 35px"));
                } else {
                    linhaPeriodo.add(new AttributeAppender(ConstantesWeb.STYLE, "height: 25px"));
                }
                item.add(linhaPeriodo);
                linhaPeriodo.add(new Label("periodo", item.getModelObject()));
            }
        });
    }
    
    private void addResultados(WebMarkupContainer container) {
        container.addOrReplace(new ListView<String>("resultadosPorNome", novoQuadroVO.getNomesResultados()) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(criarTabela(item.getModelObject()));
            }
        });
    }
    
    private Component criarTabela(String nomeResultado) {
        List<OutraInformacaoVO> resultados = novoQuadroVO.getResultadosPorNome().get(nomeResultado);
        
        Configuracao configuracao = configureTabela(null, "resultado_" + nomeResultado);
        configuracao.setExibirTitulo(false);
        configuracao.setCssPar(Model.of(FUNDO_PADRAO_A_CLARO3_ALTURA_LINHA));
        configuracao.setCssImpar(Model.of(FUNDO_PADRAO_A_CLARO3_ALTURA_LINHA));
        if (isApresentacao) {
            configuracao.setStyleLinhas(Model.of("height: 35px;"));
        }
        List<Coluna<OutraInformacaoVO>> colunas = obterColunas(nomeResultado, resultados);
        ProviderGenericoList<OutraInformacaoVO> provider = criarProvider(resultados);
        Tabela<OutraInformacaoVO> tabela = 
                new Tabela<OutraInformacaoVO>("tabelaResultados", configuracao, colunas, provider, false);
        return tabela;
    }

    private List<Coluna<OutraInformacaoVO>> obterColunas(String nomeResultado, List<OutraInformacaoVO> resultados) {
        TipoEdicaoInformacaoEnum tipoEdicao = resultados.get(0).getTipoEdicaoInformacaoEnum();
        String larguraColuna = "width: 33%;";
        if (tipoEdicao.equals(TipoEdicaoInformacaoEnum.AJUSTADO)) {
            larguraColuna = "width: 50%;";
        }
        List<Coluna<OutraInformacaoVO>> colunas = new LinkedList<Coluna<OutraInformacaoVO>>();
        colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(larguraColuna))
                .setCabecalho(nomeResultado)
                .setPropriedadeTela(property(getPropertyObject(OutraInformacaoVO.class).getValorFormatado()))
                .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getValor())));
        
        if (isEdicao) {
            colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(larguraColuna))
                    .setCabecalho(tipoEdicao.getDescricao())
                    .setComponente(new ColunaEdicao())
                    .setPropriedade(property(getPropertyObject(OutraInformacaoVO.class).getValorEditadoFormatado())));
        }
        
        boolean temValorAjustado = novoQuadroVO.existeValorEditado(TipoInformacaoEnum.RESULTADO, nomeResultado);
        
        if ((isEdicao && tipoEdicao.equals(TipoEdicaoInformacaoEnum.AJUSTE)) || (!isEdicao && temValorAjustado)) {
            colunas.add(new Coluna<OutraInformacaoVO>().setEstiloCabecalho(getFormatoCabecalho(larguraColuna))
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
    
    private ProviderGenericoList<OutraInformacaoVO> criarProvider(final List<OutraInformacaoVO> resultados) {
        return new ProviderGenericoList<OutraInformacaoVO>("", SortOrder.ASCENDING, obterModel(resultados));
    }

    private IModel<List<OutraInformacaoVO>> obterModel(final List<OutraInformacaoVO> resultados) {
        IModel<List<OutraInformacaoVO>> model =
                new AbstractReadOnlyModel<List<OutraInformacaoVO>>() {
            @Override
            public List<OutraInformacaoVO> getObject() {
                return resultados;
            }
        };
        return model;
    }
    
    private class ColunaEdicao implements IColunaComponente<OutraInformacaoVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<OutraInformacaoVO>> cellItem, String componentId,
                final IModel<OutraInformacaoVO> rowModel) {
            ColunaFragmentEdicao colunaFragment =
                    new ColunaFragmentEdicao(componentId, "fragmentInput", TabelaResultadosQuadro.this, rowModel,
                            property(getPropertyObject(OutraInformacaoVO.class).getValorEditado()));
            mapFragmento.put(rowModel.getObject(), colunaFragment);
            return mapFragmento.get(rowModel.getObject());
        }
    }
    
    private boolean isPeriodoAtual(OutraInformacaoVO resultado) {
        return resultado.getPeriodoFormatado().equals(periodos.get(periodos.size() - 1));
    }
    
    private class ColunaFragmentEdicao extends ColunaFragmentValorAjusteAjustado<OutraInformacaoVO> {
        private static final String INPUT = "input";
        private IModel<OutraInformacaoVO> rowModel;

        public ColunaFragmentEdicao(String id, String markupId, MarkupContainer markupProvider,
                IModel<OutraInformacaoVO> rowModel, String expressao) {
            super(id, markupId, markupProvider, rowModel.getObject(), expressao, isPeriodoAtual(rowModel.getObject()));
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
            textFieldCustomizado.setMarkupId(INPUT 
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
        if (rowModel.getObject().getTipoEdicaoInformacaoEnum().equals(TipoEdicaoInformacaoEnum.AJUSTE)) {
            Label lb = mapLabel.get(rowModel.getObject());
            target.add(lb, lb.getMarkupId());
        }
        painel = pageAtual.getPainelAjusteResultadoNovo();
        
        if (isMostrarInformacoesNaoSalvas) {
            painel.setInformacoesNaoSalvas(Boolean.TRUE);
        } else {
            painel.setInformacoesNaoSalvas(Boolean.FALSE);
        }
        target.add(painel.getLabelInfo());
        target.add(pageAtual.getPainelQuadroPosicaoFinanceira());
    }
    
}
