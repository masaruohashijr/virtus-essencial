package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.buildTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.configureTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.getFormatoCabecalho;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.ContaQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.LayoutContaAnaliseQuantitativaMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxCheckIndicator;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaFormatador;

public class BuildTabelaConta implements Serializable {
    private static final String ESPACEMENTO = " ";
    private static final String FORMATO_CHECK = "vertical-align:bottom; padding:0px, 0px, 0px, 0px;";
    private static final String BARRA_N = "\n";
    private static final String CONTAS = "contas";
    private static final String PONTO = ".";
    private static final String VAZIO = "";
    private static final String COLOR_000 = "color: #000;";
    private static final String COLOR_RED = "color:red";
    private static final String COLOR_BLACK = "color:#000";
    private static final String FONT_WEIGHT_BOLD_COLOR_RED = "font-weight: bold; color: red;";
    private static final String FONT_WEIGHT_BOLD = "font-weight: bold;";
    private static final String COLOR_666666 = "color:#666666";
    private static final String STYLE = "style";
    private static final String WIDTH_15 = "width: 15%;";
    private static final String WIDTH_3 = "width: 3%;";

    private Map<ContaQuadroPosicaoFinanceira, MultiLineLabel> mapMultiLineLabelDescricao =
            new HashMap<ContaQuadroPosicaoFinanceira, MultiLineLabel>();
    private Map<ContaQuadroPosicaoFinanceira, LabelValor> mapLabelValor =
            new HashMap<ContaQuadroPosicaoFinanceira, LabelValor>();
    private Map<ContaQuadroPosicaoFinanceira, Label> mapLabelAjustado =
            new HashMap<ContaQuadroPosicaoFinanceira, Label>();
    private Map<ContaQuadroPosicaoFinanceira, ColunaFragmentValorAjusteAjustado<ContaQuadroPosicaoFinanceira>> mapFragmentoAjuste = //
            new HashMap<ContaQuadroPosicaoFinanceira, ColunaFragmentValorAjusteAjustado<ContaQuadroPosicaoFinanceira>>();
    private Map<ContaQuadroPosicaoFinanceira, Label> mapLabelEscore =
            new HashMap<ContaQuadroPosicaoFinanceira, Label>();

    private Map<ContaQuadroPosicaoFinanceira, AjaxCheckBox> mapAjaxCheck =
            new HashMap<ContaQuadroPosicaoFinanceira, AjaxCheckBox>();
    private boolean limparOsPais = true;
    private QuadroPosicaoFinanceiraVO vo;
    private WebMarkupContainer container;
    private List<ContaQuadroPosicaoFinanceira> listaContas = new LinkedList<ContaQuadroPosicaoFinanceira>();
    private ContaQuadroPosicaoFinanceira contaRaiz;
    private Integer valorTotalAjustadoContaPai;
    private String escoreFormatado;

    private Map<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>> mapaContasNivelSuperior =
            new HashMap<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>>();
    private Map<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>> mapaContasFilhasNivel1 =
            new HashMap<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>>();
    private Map<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>> mapaContasFilhasNivel2 =
            new HashMap<ContaQuadroPosicaoFinanceira, List<ContaQuadroPosicaoFinanceira>>();
    private Map<ContaQuadroPosicaoFinanceira, SimNaoEnum> mapaIsContaFilhaSubNivel =
            new HashMap<ContaQuadroPosicaoFinanceira, SimNaoEnum>();

    protected void criarTabela(WebMarkupContainer container, boolean editable,
            final List<ContaQuadroPosicaoFinanceira> listaContas, QuadroPosicaoFinanceiraVO vo) {
        this.container = container;
        configurarHierarquiaLista(listaContas, editable);
        this.listaContas = listaContas;
        this.vo = vo;
        Configuracao cfg = configureTabela(null, CONTAS);
        cfg.setExibirTitulo(false);
        container.addOrReplace(buildTabela(CONTAS, new LoadableDetachableModel<List<ContaQuadroPosicaoFinanceira>>() {
            @Override
            protected List<ContaQuadroPosicaoFinanceira> load() {
                return listaContas;
            }
        }, cfg, buildColunas(container, editable, listaContas),
                property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getLayoutContaAnaliseQuantitativa()
                        .getSequencial())));
    }

    private List<Coluna<ContaQuadroPosicaoFinanceira>> buildColunas(WebMarkupContainer container,
            final boolean editable, List<ContaQuadroPosicaoFinanceira> contas) {
        List<Coluna<ContaQuadroPosicaoFinanceira>> colunas = new LinkedList<Coluna<ContaQuadroPosicaoFinanceira>>();
        String formato = getFormatoCabecalho(WIDTH_15);
        if (editable) {
            formato = buildColunaCheck(container, colunas);
        }
        buildColunaDescricao(colunas, editable);
        buildColunaValor(colunas, formato, editable);
        if (editable) {
            buildColunaAjuste(container, colunas, formato);
        }
        buildColunaAjustado(colunas, formato, contas, editable);
        buildColunaEscore(colunas, formato, editable);
        return colunas;
    }

    private void buildColunaValor(List<Coluna<ContaQuadroPosicaoFinanceira>> colunas, String formato,
            final boolean editable) {
        colunas.add(new Coluna<ContaQuadroPosicaoFinanceira>().setEstiloCabecalho(formato).setCabecalho("Valor")
                .setFormatador(new IColunaFormatador<ContaQuadroPosicaoFinanceira>() {
                    @Override
                    public String obterCss(ContaQuadroPosicaoFinanceira obj) {
                        return null;
                    }

                    @Override
                    public String obterStyle(ContaQuadroPosicaoFinanceira obj) {
                        return ajustarEstiloValor(editable, obj);
                    }
                }).setComponente(colunaValor()));
    }

    private String ajustarEstiloValor(final boolean editable, ContaQuadroPosicaoFinanceira obj) {
        String estilo = null;
        if (!obj.isContaRaiz()
                && SimNaoEnum.NAO.equals(obj.getLayoutContaAnaliseQuantitativa().getNegrito())
                && SimNaoEnum.NAO.equals(obj.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa()
                        .getDiversos())) {
            if (editable && (obj.getExibir() == null || SimNaoEnum.NAO.equals(obj.getExibir()))) {
                estilo = COLOR_666666;
            } else if (SimNaoEnum.SIM.equals(obj.getExibir()) && editable) {
                estilo = COLOR_BLACK;
            }
        }
        return estilo;
    }

    private IColunaComponente<ContaQuadroPosicaoFinanceira> colunaValor() {
        return new IColunaComponente<ContaQuadroPosicaoFinanceira>() {
            @Override
            public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                    String componentId, IModel<ContaQuadroPosicaoFinanceira> rowModel) {
                final ContaQuadroPosicaoFinanceira conta = rowModel.getObject();
                LabelValor labelValor =
                        new LabelValor(componentId, rowModel, new PropertyModel<ContaQuadroPosicaoFinanceira>(conta,
                                property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getValorFormatado())));
                mapLabelValor.put(conta, labelValor);
                if (conta.isContaRaiz()) {
                    mapLabelValor.get(conta).add(
                            new AttributeModifier(STYLE, conta.isContaRaiz() ? FONT_WEIGHT_BOLD
                                    + (conta.getAjustado() == null && conta.getValorAjuste() == null
                                            && vo.isAjustadoAtivoDiferentePassivo() ? COLOR_RED : COLOR_BLACK) : null));
                }
                return mapLabelValor.get(conta);
            }
        };
    }

    private class LabelValor extends Label {
        private IModel<ContaQuadroPosicaoFinanceira> modelObject;

        public LabelValor(String id, IModel<ContaQuadroPosicaoFinanceira> modelObject,
                IModel<ContaQuadroPosicaoFinanceira> modelObjectValor) {
            super(id, modelObjectValor);
            this.modelObject = modelObject;
        }

        public IModel<ContaQuadroPosicaoFinanceira> getModelObject() {
            return modelObject;
        }
    }

    private void buildColunaEscore(List<Coluna<ContaQuadroPosicaoFinanceira>> colunas, String formato,
            final boolean editable) {
        colunas.add(new Coluna<ContaQuadroPosicaoFinanceira>().setFormatador(formatarContaRaiz(false))
                .setPropriedade(property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getEscore()))
                .setComponente(new IColunaComponente<ContaQuadroPosicaoFinanceira>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                            String componentId, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
                        final ContaQuadroPosicaoFinanceira conta = rowModel.getObject();
                        Label labelEscore =
                                new Label(componentId, new PropertyModel<ContaQuadroPosicaoFinanceira>(conta,
                                        property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getEscore()))) {
                                    @Override
                                    protected void onConfigure() {
                                        super.onConfigure();
                                        configurarColunaEscore(conta, editable, this);
                                    }
                                };
                        mapLabelEscore.put(conta, labelEscore);
                        return mapLabelEscore.get(conta);
                    }
                }).setCabecalho("%").setEstiloCabecalho(formato));
    }

    private void configurarColunaEscore(ContaQuadroPosicaoFinanceira conta, boolean editable, Component componente) {
        componente.setOutputMarkupId(true);
        componente.setOutputMarkupPlaceholderTag(true);
        componente.setVisibilityAllowed((SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa()
                .getObrigatorio())) || isContaEmExibicao(conta) || conta.getAjustado() != null);
        GerenciarQuadroPosicaoFinanceira paginaAtual = null;
        if (container.getPage() instanceof GerenciarQuadroPosicaoFinanceira) {
            paginaAtual = (GerenciarQuadroPosicaoFinanceira) container.getPage();
        }
        if (paginaAtual != null && Boolean.TRUE.equals(paginaAtual.getAjusteRealizado())) {
            componente.setDefaultModel(new PropertyModel<ContaQuadroPosicaoFinanceira>(conta,
                    property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getEscore())));
        } else {
            if (valorTotalAjustadoContaPai == null && !conta.isLinhaEmBranco()) {
                if (limparOsPais && editable) {
                    conta.setValorTotalAjustado(getContaPai().getValor());
                    componente.setDefaultModel(new PropertyModel<ContaQuadroPosicaoFinanceira>(conta,
                            property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getEscore())));
                }
            } else {
                setEscoreFormatado(conta.formatarEscore(valorTotalAjustadoContaPai));
                conta.setValorTotalAjustado(valorTotalAjustadoContaPai);
                componente.setDefaultModel(new PropertyModel<ContaQuadroPosicaoFinanceira>(BuildTabelaConta.this,
                        property(getPropertyObject(BuildTabelaConta.class).getEscoreFormatado())));
            }
        }
    }

    private void buildColunaAjustado(List<Coluna<ContaQuadroPosicaoFinanceira>> colunas, String formato,
            List<ContaQuadroPosicaoFinanceira> listaVO, final boolean editable) {
        boolean temValorAjustado = false;
        for (ContaQuadroPosicaoFinanceira vo : listaVO) {
            if (vo.getAjustado() != null) {
                temValorAjustado = true;
                break;
            }
        }
        if (editable || temValorAjustado) {
            colunas.add(new Coluna<ContaQuadroPosicaoFinanceira>()
                    .setFormatador(formatarContaRaiz(vo.isAjustadoAtivoDiferentePassivo()))
                    .setPropriedadeTela(property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getAjustado()))
                    .setComponente(new IColunaComponente<ContaQuadroPosicaoFinanceira>() {
                        @Override
                        public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                                String componentId, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
                            return criarLabelAjustado(componentId, rowModel);
                        }
                    }).setEstiloCabecalho(formato).setCabecalho("Ajustado"));
        }
    }

    private Component criarLabelAjustado(String componentId, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
        final ContaQuadroPosicaoFinanceira conta = rowModel.getObject();
        Label labelAjustado = new Label(componentId, new Model<ContaQuadroPosicaoFinanceira>()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true).setDefaultModel(
                        new PropertyModel<ContaQuadroPosicaoFinanceira>(conta, property(getPropertyObject(
                                ContaQuadroPosicaoFinanceira.class).getAjustado())));
            }

            @Override
            @SuppressWarnings("unchecked")
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new FormatarNumerico();
            }
        };
        mapLabelAjustado.put(conta, labelAjustado);
        return mapLabelAjustado.get(conta);
    }

    private void buildColunaDescricao(List<Coluna<ContaQuadroPosicaoFinanceira>> colunas, final boolean editable) {
        colunas.add(new Coluna<ContaQuadroPosicaoFinanceira>()
                .setEstiloCabecalho("background-color:#fff; color:#000; width: 45%;").setCabecalho(VAZIO)
                .setEscapeModelStrings(true).setFormatador(new IColunaFormatador<ContaQuadroPosicaoFinanceira>() {
                    @Override
                    public String obterStyle(ContaQuadroPosicaoFinanceira conta) {
                        return ajustarEstiloDescricao(editable, conta);
                    }

                    @Override
                    public String obterCss(ContaQuadroPosicaoFinanceira obj) {
                        return null;
                    }
                }).setComponente(new IColunaComponente<ContaQuadroPosicaoFinanceira>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                            String componentId, IModel<ContaQuadroPosicaoFinanceira> rowModel) {
                        MultiLineLabel multiLineLabel =
                                new MultiLineLabel(componentId, new PropertyModel<ContaQuadroPosicaoFinanceira>(
                                        rowModel.getObject(),
                                        "layoutContaAnaliseQuantitativa.contaAnaliseQuantitativa.descricao")) {
                                    @Override
                                    protected void onConfigure() {
                                        super.onConfigure();
                                    }
                                };

                        mapMultiLineLabelDescricao.put(rowModel.getObject(), multiLineLabel);

                        return mapMultiLineLabelDescricao.get(rowModel.getObject());
                    }
                }));
    }

    private String ajustarEstiloDescricao(final boolean editable, ContaQuadroPosicaoFinanceira conta) {
        String estilo = null;
        if (SimNaoEnum.SIM
                .equals(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa().getDiversos())) {
            estilo = null;
        } else if (SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa().getNegrito())) {
            estilo = FONT_WEIGHT_BOLD;
        } else if (SimNaoEnum.SIM.equals(mapaIsContaFilhaSubNivel.get(conta))) {
            if (editable) {
                estilo = "padding-left: 30px;font-style: italic;";
                if (conta.getExibir() == null || SimNaoEnum.NAO.equals(conta.getExibir())) {
                    estilo = estilo.concat(COLOR_666666);
                }
            }
        } else if (isColorCinza(editable, conta)) {
            estilo = COLOR_666666;
        } else if (SimNaoEnum.SIM.equals(conta.getExibir()) && editable) {
            estilo = null;
        }
        return estilo;
    }

    private boolean isColorCinza(final boolean editable, ContaQuadroPosicaoFinanceira conta) {
        return editable
                && conta.getExibir() == null
                && SimNaoEnum.NAO.equals(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa()
                        .getDiversos()) || (SimNaoEnum.NAO.equals(conta.getExibir()));
    }

    private String buildColunaCheck(WebMarkupContainer container, List<Coluna<ContaQuadroPosicaoFinanceira>> colunas) {
        colunas.add(new Coluna<ContaQuadroPosicaoFinanceira>().setFormatador(formatador())
                .setComponente(new CheckContaSelecao(container)).setEstiloCabecalho(getFormatoCabecalho(WIDTH_3))
                .setPropriedade(property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getExibir())));
        return getFormatoCabecalho(WIDTH_15);
    }

    private IColunaFormatador<ContaQuadroPosicaoFinanceira> formatador() {
        IColunaFormatador<ContaQuadroPosicaoFinanceira> formatador =
                new IColunaFormatador<ContaQuadroPosicaoFinanceira>() {
                    @Override
                    public String obterCss(ContaQuadroPosicaoFinanceira obj) {
                        return Constantes.CENTRALIZADO + ESPACEMENTO + Constantes.FUNDO_PADRAO_A_CLARO3;
                    }

                    @Override
                    public String obterStyle(ContaQuadroPosicaoFinanceira obj) {
                        return FORMATO_CHECK + WIDTH_3;
                    }
                };
        return formatador;
    }

    private void buildColunaAjuste(final WebMarkupContainer container,
            List<Coluna<ContaQuadroPosicaoFinanceira>> colunas, String formato) {
        final String propriedadeAjuste =
                property(getPropertyObject(ContaQuadroPosicaoFinanceira.class).getValorAjuste());
        Coluna<ContaQuadroPosicaoFinanceira> colunaAjusteConta =
                new Coluna<ContaQuadroPosicaoFinanceira>().setEstiloCabecalho(formato).setCabecalho("Ajuste")
                        .setPropriedade(propriedadeAjuste).setFormatador(formatarContaRaiz(false));
        colunaAjusteConta.setComponente(new IColunaComponente<ContaQuadroPosicaoFinanceira>() {
            @Override
            public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                    String componentId, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
                ContaQuadroPosicaoFinanceira conta = rowModel.getObject();
                ColunaComponenteAjuste colunaFragment =
                        new ColunaComponenteAjuste(componentId, container, conta, propriedadeAjuste);
                mapFragmentoAjuste.put(conta, colunaFragment);
                return mapFragmentoAjuste.get(conta);
            }
        });
        colunas.add(colunaAjusteConta);
    }

    private IColunaFormatador<ContaQuadroPosicaoFinanceira> formatarContaRaiz(final boolean isAjustado) {
        return new IColunaFormatador<ContaQuadroPosicaoFinanceira>() {
            @Override
            public String obterCss(ContaQuadroPosicaoFinanceira obj) {
                return Constantes.ESQUERDA + ESPACEMENTO + Constantes.FUNDO_PADRAO_A_CLARO3;
            }

            @Override
            public String obterStyle(ContaQuadroPosicaoFinanceira obj) {
                return obj.isContaRaiz() ? FONT_WEIGHT_BOLD + ((isAjustado) ? COLOR_RED : VAZIO) : null;
            }
        };
    }

    private class ColunaComponenteAjuste extends ColunaFragmentValorAjusteAjustado<ContaQuadroPosicaoFinanceira> {
        private ContaQuadroPosicaoFinanceira conta;

        public ColunaComponenteAjuste(String componentId, MarkupContainer container,
                ContaQuadroPosicaoFinanceira conta, String propriedadeAjuste) {
            super(componentId, "fragmentInput", container, conta, propriedadeAjuste, SimNaoEnum.SIM.equals(conta
                    .getLayoutContaAnaliseQuantitativa().getEditarAjuste())
                    && (isContaEmExibicao(conta) || SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa()
                            .getContaAnaliseQuantitativa().getDiversos())));
            this.conta = conta;
        }

        @Override
        protected void executorOnUpdateExterno(AjaxRequestTarget target) {
            super.executorOnUpdateExterno(target);
            prepararTargetAjuste(ColunaComponenteAjuste.this, getTextField(), conta, target);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <C> IConverter<C> getConverterTextField(Class<C> type) {
            return (IConverter<C>) new FormatarNumerico();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <C> IConverter<C> getConverterLabel(Class<C> type) {
            return (IConverter<C>) new FormatarNumerico();
        }

        @Override
        protected void onAddConfiguracaoTextField(TextField<ContaQuadroPosicaoFinanceira> textFieldCustomizado) {
            textFieldCustomizado.setMarkupId("inputContaLayout" + getObjeto().getPk());
            textFieldCustomizado.add(new AttributeModifier("onkeyup", RETURN_SO_NUMEROS_THIS), new AttributeModifier(
                    "onmousemove", RETURN_SO_NUMEROS_THIS));
        }

        @Override
        protected void onAddConfiguracaoLabel(Label lb) {
            lb.setDefaultModel(new PropertyModel<ContaQuadroPosicaoFinanceira>(conta, property(getPropertyObject(
                    ContaQuadroPosicaoFinanceira.class).getValorAjuste())));

        }
    }

    private void prepararTargetAjuste(ColunaComponenteAjuste colunaComponenteAjuste,
            TextField<ContaQuadroPosicaoFinanceira> textField, final ContaQuadroPosicaoFinanceira conta,
            AjaxRequestTarget target) {
        Label lbAjustado = mapLabelAjustado.get(conta);
        Label lbEscore = mapLabelEscore.get(conta);
        GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) textField.getPage();
        verificarAjuste(conta, target);
        if (textField.getValue() != null && textField.getValue().isEmpty()) {
            lbAjustado.setDefaultModelObject(null);
            colunaComponenteAjuste.setIsMostrar(Boolean.TRUE);
        } else {
            lbEscore.setDefaultModel(new PropertyModel<ContaQuadroPosicaoFinanceira>(conta, property(getPropertyObject(
                    ContaQuadroPosicaoFinanceira.class).getEscore())));
            lbAjustado.setDefaultModel(textField.getModel());
        }
        if (contaRaiz != null) {
            verificarValoresAjustadoPassivoAtivo(conta, target);
        }
        target.add(lbAjustado, lbAjustado.getMarkupId());
        target.add(lbEscore, lbEscore.getMarkupId());

        for (Label label : mapLabelEscore.values()) {
            target.add(label, label.getMarkupId());
        }
        ajustarTargetComponentes(conta, target, pageAtual);
    }

    private void verificarValoresAjustadoPassivoAtivo(ContaQuadroPosicaoFinanceira conta, AjaxRequestTarget target) {
        GerenciarQuadroPosicaoFinanceira page = (GerenciarQuadroPosicaoFinanceira) container.getPage();
        TipoConta tipoConta =
                contaRaiz.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa().getTipoConta();
        Label labelExternoAjustado = null;
        Label labelExternoValor = null;
        boolean isMapaPreenchido = false;
        if (TipoConta.ATIVO.equals(tipoConta)) {
            BuildTabelaConta tabelaContaPassivo = page.getPainelSelecaoContaPassivo().getTabelaConta();
            labelExternoAjustado = tabelaContaPassivo.getMapLabelAjustado().get(tabelaContaPassivo.getContaPai());
            labelExternoValor = tabelaContaPassivo.getMapLabelValor().get(tabelaContaPassivo.getContaPai());
            isMapaPreenchido =
                    !tabelaContaPassivo.getMapLabelAjustado().isEmpty()
                            && !tabelaContaPassivo.getMapLabelValor().isEmpty();
        } else {
            BuildTabelaConta tabelaContaAtivo = page.getPainelSelecaoContaAtivo().getTabelaConta();
            labelExternoAjustado = tabelaContaAtivo.getMapLabelAjustado().get(tabelaContaAtivo.getContaPai());
            labelExternoValor = tabelaContaAtivo.getMapLabelValor().get(tabelaContaAtivo.getContaPai());
            isMapaPreenchido =
                    !tabelaContaAtivo.getMapLabelAjustado().isEmpty() && !tabelaContaAtivo.getMapLabelValor().isEmpty();
        }
        if (isMapaPreenchido && conta.getQuadroPosicaoFinanceira().getVersaoPerfilRisco() == null) {
            montarCoresAtivoPassivo(target, labelExternoAjustado, labelExternoValor);
        }
    }

    //CHECKSTYLE:OFF Funcionalidade de alta complexidade
    private void montarCoresAtivoPassivo(AjaxRequestTarget target, Label labelExternoAjustado, Label labelExternoValor) {
        Integer valorExternoAjustado = obterValorExternoAjustado(labelExternoAjustado);
        boolean isAjustadoDiferenteNull = contaRaiz.getAjustado() != null;
        boolean isAjustadoNull = contaRaiz.getAjustado() == null;
        boolean isAjustadoDiferenteZero = valorExternoAjustado != 0;
        boolean isAjustadoIgualZero = isAjustadoNull ? false : contaRaiz.getAjustado() == 0;
        boolean cond1 =
                isAjustadoDiferenteNull && isAjustadoDiferenteZero
                        && contaRaiz.getAjustado().intValue() == valorExternoAjustado.intValue();
        boolean cond2 = cond1 || (isAjustadoNull || isAjustadoIgualZero) && valorExternoAjustado == 0;
        boolean cond3 = cond2 || isAjustadoNull && valorExternoAjustado == 0;
        if (cond3) {
            montarAtivoPassivo(false, FONT_WEIGHT_BOLD + COLOR_000, FONT_WEIGHT_BOLD + COLOR_000, FONT_WEIGHT_BOLD
                    + COLOR_000, FONT_WEIGHT_BOLD + COLOR_000, labelExternoValor, labelExternoAjustado);
        } else if (isAjustadoDiferenteNull && isAjustadoDiferenteZero
                && contaRaiz.getAjustado().intValue() != valorExternoAjustado.intValue()) {
            montarAtivoPassivo(true, FONT_WEIGHT_BOLD + COLOR_000, FONT_WEIGHT_BOLD_COLOR_RED, FONT_WEIGHT_BOLD
                    + COLOR_000, FONT_WEIGHT_BOLD_COLOR_RED, labelExternoValor, labelExternoAjustado);
        } else if (isAjustadoNull && isAjustadoDiferenteZero) {
            montarAtivoPassivo(true, FONT_WEIGHT_BOLD_COLOR_RED, FONT_WEIGHT_BOLD + COLOR_000, FONT_WEIGHT_BOLD
                    + COLOR_000, FONT_WEIGHT_BOLD_COLOR_RED, labelExternoValor, labelExternoAjustado);
        } else if (isAjustadoDiferenteNull && valorExternoAjustado == 0) {
            montarAtivoPassivo(true, FONT_WEIGHT_BOLD + COLOR_000, FONT_WEIGHT_BOLD_COLOR_RED,
                    FONT_WEIGHT_BOLD_COLOR_RED, FONT_WEIGHT_BOLD + COLOR_000, labelExternoValor, labelExternoAjustado);
        }
        target.add(mapLabelValor.get(contaRaiz), mapLabelValor.get(contaRaiz).getMarkupId());
        target.add(mapLabelAjustado.get(contaRaiz), mapLabelAjustado.get(contaRaiz).getMarkupId());
        target.add(labelExternoValor, labelExternoValor.getMarkupId());
        target.add(labelExternoAjustado, labelExternoAjustado.getMarkupId());
    }

    //CHECKSTYLE:ON
    private Integer obterValorExternoAjustado(Label labelExternoAjustado) {
        Integer valorExternoAjustado = 0;
        if (labelExternoAjustado.getDefaultModelObject() != null) {
            if (StringUtils.isNotBlank(labelExternoAjustado.getDefaultModelObject().toString())) {
                String valorLabel = labelExternoAjustado.getDefaultModelObject().toString();
                valorLabel = valorLabel.replace(PONTO, VAZIO);
                valorLabel = valorLabel.replace("-", VAZIO);
                valorExternoAjustado = Integer.valueOf(valorLabel);
            }
            valorExternoAjustado =
                    StringUtils.isBlank(labelExternoAjustado.getDefaultModelObject().toString()) ? 0 : Integer
                            .valueOf(labelExternoAjustado.getDefaultModelObject().toString().replace(PONTO, VAZIO));
        }
        return valorExternoAjustado;
    }

    private void montarAtivoPassivo(boolean isAjusteAtivoPassivoDiferente, String styleLabelValor,
            String styleLabelAjustado, String styleLabelValorExterno, String styleLabelAjustadoExterno,
            Label labelExternoValor, Label labelExternoAjustado) {
        vo.setAjustadoAtivoDiferentePassivo(isAjusteAtivoPassivoDiferente);
        mapLabelValor.get(contaRaiz).add(AttributeModifier.replace(STYLE, styleLabelValor));
        mapLabelAjustado.get(contaRaiz).add(AttributeModifier.replace(STYLE, styleLabelAjustado));
        labelExternoValor.add(AttributeModifier.replace(STYLE, styleLabelValorExterno));
        labelExternoAjustado.add(AttributeModifier.replace(STYLE, styleLabelAjustadoExterno));

    }

    private void verificarAjuste(ContaQuadroPosicaoFinanceira conta, AjaxRequestTarget target) {
        if (SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa().getEditarAjuste())
                || SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa()
                        .getDiversos())) {
            ajustarCalculoAjusteContasSuperiores(conta, target);
        }
    }

    private void ajustarCalculoAjusteContasSuperiores(ContaQuadroPosicaoFinanceira contaResgistroAtual,
            AjaxRequestTarget target) {
        List<ContaQuadroPosicaoFinanceira> contasNivelSuperior = mapaContasNivelSuperior.get(contaResgistroAtual);
        Collections.reverse(contasNivelSuperior);
        limparOsPais = true;
        for (ContaQuadroPosicaoFinanceira contaNivelSuperior : contasNivelSuperior) {
            List<ContaQuadroPosicaoFinanceira> contasFilhasNivel1 = mapaContasFilhasNivel1.get(contaNivelSuperior);
            Integer somatorio = montarSomatorio(contasFilhasNivel1);
            int temAjustesContasFilhas = temAjustesContasFilhas(contasFilhasNivel1);

            if (temAjustesContasFilhas > 0) {
                QuadroPosicaoFinanceiraMediator.get().setAjusteRealizado(Boolean.FALSE);
            }
            contaNivelSuperior.setValorAjuste(limparOsPais ? null
                    : (temAjustesContasFilhas > 0 && somatorio == 0) ? somatorio : (temAjustesContasFilhas == 0 ? null
                            : somatorio));

            if (contaNivelSuperior.isContaRaiz()) {
                this.contaRaiz = contaNivelSuperior;
                valorTotalAjustadoContaPai = contaNivelSuperior.getAjustado();
            }

            if (target != null) {
                ajustarComponentesNivelSuperior(target, contaNivelSuperior);
            }
        }
    }

    private int temAjustesContasFilhas(List<ContaQuadroPosicaoFinanceira> contasFilhasNivel1) {
        int temAjustesContasFilhas = 0;
        for (ContaQuadroPosicaoFinanceira contaFilha : contasFilhasNivel1) {
            if (mapFragmentoAjuste.get(contaFilha).getObjeto().getValorAjuste() != null) {
                temAjustesContasFilhas++;
                break;
            }
            List<ContaQuadroPosicaoFinanceira> contasFilhasSubNivel = mapaContasFilhasNivel2.get(contaFilha);
            for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira : contasFilhasSubNivel) {
                if (mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getObjeto().getValorAjuste() != null) {
                    temAjustesContasFilhas++;
                    break;
                }
            }
        }
        return temAjustesContasFilhas;
    }

    private void ajustarComponentesNivelSuperior(AjaxRequestTarget target,
            ContaQuadroPosicaoFinanceira contaNivelSuperior) {
        Label labelAjuste = mapFragmentoAjuste.get(contaNivelSuperior).getLabelAjuste();
        Label labelAjustado = mapLabelAjustado.get(contaNivelSuperior);
        Label labelEscore = mapLabelEscore.get(contaNivelSuperior);
        labelAjuste.setDefaultModelObject(contaNivelSuperior.getValorAjuste());
        labelAjustado.setDefaultModelObject(contaNivelSuperior.getAjustado());
        target.add(labelAjuste, labelAjuste.getMarkupId());
        target.add(labelAjustado, labelAjustado.getMarkupId());
        target.add(labelEscore, labelEscore.getMarkupId());
    }

    private void calculoDiversos(ContaQuadroPosicaoFinanceira contaResgistroAtual, AjaxRequestTarget target) {
        ContaQuadroPosicaoFinanceira contaQuadroDiversos = null;
        for (LabelValor lbValor : mapLabelValor.values()) {
            IModel<ContaQuadroPosicaoFinanceira> modelObject = lbValor.getModelObject();
            if (modelObject != null
                    && !modelObject.getObject().isLinhaEmBranco()
                    && SimNaoEnum.SIM.equals(modelObject.getObject().getLayoutContaAnaliseQuantitativa()
                            .getContaAnaliseQuantitativa().getDiversos())) {
                contaQuadroDiversos = modelObject.getObject();
                if (contaQuadroDiversos != null) {
                    ContaAnaliseQuantitativa contaNivelSuperiorResgistroAtual =
                            contaResgistroAtual.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai();

                    ContaAnaliseQuantitativa contaNivelSuperiorDiversos =
                            contaQuadroDiversos.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai();

                    boolean ehParente = contaNivelSuperiorResgistroAtual.getPk() == contaNivelSuperiorDiversos.getPk();

                    boolean ehParenteFolha =
                            mapaIsContaFilhaSubNivel.get(contaResgistroAtual).booleanValue()
                                    && ehParenteFolhaMesmoPaiDiversos(contaResgistroAtual, contaQuadroDiversos);

                    if (contaQuadroDiversos != null && contaResgistroAtual.getValor() != null
                            && (ehParente || ehParenteFolha)) {
                        if (SimNaoEnum.SIM.equals(contaResgistroAtual.getExibir())) {
                            contaQuadroDiversos.setValor(contaQuadroDiversos.getValor()
                                    - contaResgistroAtual.getValor());
                        } else {
                            contaQuadroDiversos.setValor(contaQuadroDiversos.getValor()
                                    + contaResgistroAtual.getValor());
                        }
                        target.add(mapLabelValor.get(contaQuadroDiversos));
                        target.add(mapLabelAjustado.get(contaQuadroDiversos));
                        target.add(mapLabelEscore.get(contaQuadroDiversos));
                    }
                }
            }
        }
    }

    private boolean ehParenteFolhaMesmoPaiDiversos(ContaQuadroPosicaoFinanceira contaResgistroAtual,
            ContaQuadroPosicaoFinanceira contaQuadroDiversos) {
        boolean ehParenteFolhaMesmoPaiDiversos = false;
        for (ContaQuadroPosicaoFinanceira cta : mapaContasNivelSuperior.get(contaResgistroAtual)) {
            if (cta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa() != null
                    && contaQuadroDiversos.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativaPai() != null) {
                if (cta.getLayoutContaAnaliseQuantitativa()
                        .getContaAnaliseQuantitativa()
                        .getPk()
                        .equals(contaQuadroDiversos.getLayoutContaAnaliseQuantitativa()
                                .getContaAnaliseQuantitativaPai().getPk())) {
                    ehParenteFolhaMesmoPaiDiversos = true;
                }
            }
        }
        return ehParenteFolhaMesmoPaiDiversos;
    }

    private Integer montarSomatorio(List<ContaQuadroPosicaoFinanceira> contas) {
        Integer somatorio = 0;
        for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira : contas) {
            List<ContaQuadroPosicaoFinanceira> contasFilhas = mapaContasFilhasNivel2.get(contaQuadroPosicaoFinanceira);
            if (SimNaoEnum.SIM.equals(mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getObjeto()
                    .getLayoutContaAnaliseQuantitativa().getEditarAjuste())
                    && SimNaoEnum.SIM.equals(mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getObjeto()
                            .getExibir())) {
                if (StringUtils.isNotBlank(mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getTextField()
                        .getValue())) {
                    String valor = mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getTextField().getValue();
                    if (StringUtils.isNotBlank(valor)) {
                        valor = valor.replace(PONTO, "");
                    }
                    somatorio = somatorio + Integer.valueOf(valor);
                    limparOsPais = false;
                }
            }
            if (CollectionUtils.isNotEmpty(contasFilhas)) {
                somatorio = somatorio + montarSomatorio(contasFilhas);
            }
        }
        return somatorio;
    }

    private void ajustarTargetComponentes(ContaQuadroPosicaoFinanceira conta, AjaxRequestTarget target,
            GerenciarQuadroPosicaoFinanceira pageAtual) {
        PainelBaseAtivoPassivo painelBaseAtivoPassivo = null;
        if (TipoConta.ATIVO.equals(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa()
                .getTipoConta())) {
            painelBaseAtivoPassivo = pageAtual.getPainelSelecaoContaAtivo();
        } else {
            painelBaseAtivoPassivo = pageAtual.getPainelSelecaoContaPassivo();
        }
        painelBaseAtivoPassivo.setInformacoesNaoSalvas(Boolean.TRUE);
        PainelQuadroPosicaoFinanceira painelQuadroPosicaoFinanceira = pageAtual.getPainelQuadroPosicaoFinanceira();
        target.add(painelQuadroPosicaoFinanceira);
        target.add(pageAtual.getBotaoConcluirNovoQuadro());
        target.add(painelBaseAtivoPassivo.getLabelInformacoes());
    }

    private class CheckContaSelecao implements IColunaComponente<ContaQuadroPosicaoFinanceira> {
        private WebMarkupContainer container;

        public CheckContaSelecao(WebMarkupContainer container) {
            this.container = container;
        }

        @Override
        public Component obterComponente(Item<ICellPopulator<ContaQuadroPosicaoFinanceira>> cellItem,
                String componentId, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
            Fragment fragmentCheck = new Fragment(componentId, "fragmentCheck", container, rowModel);
            AjaxCheckBoxContaSelecao ajaxCheckBoxContaSelecao = new AjaxCheckBoxContaSelecao("check", rowModel);
            ajaxCheckBoxContaSelecao.setMarkupId("idContaLayout" + rowModel.getObject().getPk());
            ajaxCheckBoxContaSelecao.setOutputMarkupId(true);
            ajaxCheckBoxContaSelecao.setOutputMarkupPlaceholderTag(true);
            mapAjaxCheck.put(rowModel.getObject(), ajaxCheckBoxContaSelecao);
            fragmentCheck.addOrReplace(ajaxCheckBoxContaSelecao);
            return fragmentCheck;
        }
    }

    private class AjaxCheckBoxContaSelecao extends AjaxCheckIndicator {
        private IModel<ContaQuadroPosicaoFinanceira> rowModel;

        public AjaxCheckBoxContaSelecao(String id, final IModel<ContaQuadroPosicaoFinanceira> rowModel) {
            super(id, new IModel<Boolean>() {
                @Override
                public void detach() {
                    //Não se aplica
                }

                @Override
                public Boolean getObject() {
                    return rowModel.getObject().getExibir() == null ? null : rowModel.getObject().getExibir()
                            .booleanValue();
                }

                @Override
                public void setObject(Boolean object) {
                    rowModel.getObject().setExibir(SimNaoEnum.getTipo(object));

                }
            });
            this.rowModel = rowModel;
        }

        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) getPage();
            ContaQuadroPosicaoFinanceira conta = rowModel.getObject();
            ColunaFragmentValorAjusteAjustado<?> colunaComponenteTextField = mapFragmentoAjuste.get(conta);
            Label labelAjustado = mapLabelAjustado.get(conta);
            Label labelEscore = mapLabelEscore.get(conta);
            Label labelValor = mapLabelValor.get(conta);
            MultiLineLabel labelDescricao = mapMultiLineLabelDescricao.get(conta);

            LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa = conta.getLayoutContaAnaliseQuantitativa();
            if (Boolean.FALSE.equals(getModelObject())) {
                tratarEventoCheckOff(target, conta, colunaComponenteTextField, labelAjustado,
                        layoutContaAnaliseQuantitativa);
                labelValor.add(AttributeAppender.append(STYLE, COLOR_666666));
                labelDescricao.add(AttributeAppender.append(STYLE, COLOR_666666));
                vo.getListaContasExcluidas().add(rowModel.getObject());
            } else {
                tratarEventoCheckOn(target, conta, colunaComponenteTextField, layoutContaAnaliseQuantitativa);
                labelValor.add(AttributeAppender.replace(STYLE, COLOR_BLACK));
                labelDescricao.add(AttributeAppender.replace(STYLE, COLOR_BLACK));
                vo.getListaContasExcluidas().remove(rowModel.getObject());
            }
            if (colunaComponenteTextField != null) {
                colunaComponenteTextField.setIsMostrar(Boolean.TRUE);
                target.add(colunaComponenteTextField.getTextField(), colunaComponenteTextField.getTextField()
                        .getMarkupId());
                target.add(colunaComponenteTextField.getLbAsteriscoEdicao(), colunaComponenteTextField
                        .getLbAsteriscoEdicao().getMarkupId());
            }
            ajustarCalculoAjusteContasSuperiores(conta, target);
            calculoDiversos(conta, target);
            target.add(labelValor, labelValor.getMarkupId());
            target.add(labelDescricao, labelDescricao.getMarkupId());
            target.add(labelAjustado, labelAjustado.getMarkupId());
            target.add(labelEscore, labelEscore.getMarkupId());
            if (contaRaiz != null) {
                verificarValoresAjustadoPassivoAtivo(conta, target);
            }
            ajustarTargetComponentes(conta, target, pageAtual);
        }

        private void tratarEventoCheckOn(AjaxRequestTarget target, ContaQuadroPosicaoFinanceira obj,
                ColunaFragmentValorAjusteAjustado<?> colunaComponenteTextField,
                LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa) {
            if (colunaComponenteTextField != null) {
                colunaComponenteTextField.setIsCampoVisible(Boolean.TRUE);
            }
            if (!checkSubNivelHabilitado(obj, false, target)) {
                ContaQuadroPosicaoFinanceira contaRootSubNivel =
                        ContaQuadroPosicaoFinanceiraMediator.get().buscaContaNivelSuperior(
                                layoutContaAnaliseQuantitativa.getRoot(), obj);
                mapAjaxCheck.get(contaRootSubNivel).setEnabled(false);
                target.add(mapAjaxCheck.get(contaRootSubNivel), mapAjaxCheck.get(contaRootSubNivel).getMarkupId());
            }
        }

        private void tratarEventoCheckOff(AjaxRequestTarget target, ContaQuadroPosicaoFinanceira contaResgistroAtual,
                ColunaFragmentValorAjusteAjustado<?> colunaComponenteTextField, Label labelAjustado,
                LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa) {
            boolean existeAlgumSelecionado = false;
            if (colunaComponenteTextField != null) {
                colunaComponenteTextField.setIsCampoVisible(Boolean.FALSE);
            }
            ContaQuadroPosicaoFinanceira contaRootSubNivel =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscaContaNivelSuperior(
                            layoutContaAnaliseQuantitativa.getRoot(), contaResgistroAtual);
            List<ContaQuadroPosicaoFinanceira> contasParente =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            contaRootSubNivel.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                            contaResgistroAtual.getQuadroPosicaoFinanceira());
            if (!checkSubNivelHabilitado(contaResgistroAtual, true, target)) {
                for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceiraFilha : contasParente) {
                    if (Boolean.TRUE.equals(mapAjaxCheck.get(contaQuadroPosicaoFinanceiraFilha).getModelObject())) {
                        existeAlgumSelecionado = true;
                        break;
                    }
                }
            }
            if (!existeAlgumSelecionado) {
                mapAjaxCheck.get(contaRootSubNivel).setEnabled(true);
                target.add(mapAjaxCheck.get(contaRootSubNivel), mapAjaxCheck.get(contaRootSubNivel).getMarkupId());
            }
            if (colunaComponenteTextField != null) {
                colunaComponenteTextField.getTextField().setDefaultModelObject(null);
            }
            labelAjustado.setDefaultModelObject(null);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setOutputMarkupId(true);
            setOutputMarkupPlaceholderTag(true);
            ContaQuadroPosicaoFinanceira conta = rowModel.getObject();

            boolean ehParenteFolha = false;
            if (mapaIsContaFilhaSubNivel.get(conta) != null) {
                ehParenteFolha = mapaIsContaFilhaSubNivel.get(conta).booleanValue();
            }

            if (SimNaoEnum.SIM.equals(conta.getExibir())) {
                if (ehParenteFolha) {
                    ContaAnaliseQuantitativa contaPai = conta.getLayoutContaAnaliseQuantitativa().getRoot();
                    if (contaPai != null) {
                        ContaQuadroPosicaoFinanceira contaRootSubNivel =
                                ContaQuadroPosicaoFinanceiraMediator.get().buscaContaNivelSuperior(contaPai, conta);
                        if (mapAjaxCheck.get(contaRootSubNivel) != null) {
                            mapAjaxCheck.get(contaRootSubNivel).setEnabled(false);
                        }
                    }
                }
                checkSubNivelHabilitado(rowModel.getObject(), false, null);
            }
            setVisibilityAllowed(SimNaoEnum.NAO.equals(rowModel.getObject().getLayoutContaAnaliseQuantitativa()
                    .getObrigatorio()));
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled();
        }
    }

    private boolean checkSubNivelHabilitado(ContaQuadroPosicaoFinanceira conta, boolean isEnabled,
            AjaxRequestTarget target) {
        boolean isRootSubNivel = false;
        if (SimNaoEnum.SIM.equals(conta.getLayoutContaAnaliseQuantitativa().getSubNivel())) {
            isRootSubNivel = true;
            List<ContaQuadroPosicaoFinanceira> contas =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                            conta.getQuadroPosicaoFinanceira());
            for (ContaQuadroPosicaoFinanceira contaQuadroPosicaoFinanceira : contas) {
                mapAjaxCheck.get(contaQuadroPosicaoFinanceira).setEnabled(isEnabled);
                if (isEnabled) {
                    contaQuadroPosicaoFinanceira.setExibir(SimNaoEnum.NAO);
                    mapLabelValor.get(contaQuadroPosicaoFinanceira).add(AttributeModifier.remove(STYLE));
                    mapMultiLineLabelDescricao.get(contaQuadroPosicaoFinanceira).add(AttributeModifier.remove(STYLE));
                } else {
                    if (Boolean.TRUE.equals(mapAjaxCheck.get(contaQuadroPosicaoFinanceira).getModelObject())) {
                        mapAjaxCheck.get(contaQuadroPosicaoFinanceira).setModelObject(Boolean.FALSE);
                    }
                    mapLabelValor.get(contaQuadroPosicaoFinanceira).add(AttributeModifier.append(STYLE, COLOR_666666));
                    mapMultiLineLabelDescricao.get(contaQuadroPosicaoFinanceira).add(
                            AttributeModifier.append(STYLE, COLOR_666666));
                }
                if (target != null) {
                    target.add(mapAjaxCheck.get(contaQuadroPosicaoFinanceira),
                            mapAjaxCheck.get(contaQuadroPosicaoFinanceira).getMarkupId());
                    target.add(mapLabelValor.get(contaQuadroPosicaoFinanceira),
                            mapLabelValor.get(contaQuadroPosicaoFinanceira).getMarkupId());
                    target.add(mapMultiLineLabelDescricao.get(contaQuadroPosicaoFinanceira), mapMultiLineLabelDescricao
                            .get(contaQuadroPosicaoFinanceira).getMarkupId());
                    TextField<ContaQuadroPosicaoFinanceira> textField =
                            mapFragmentoAjuste.get(contaQuadroPosicaoFinanceira).getTextField();
                    target.add(textField, textField.getMarkupId());
                }
            }
        }
        return isRootSubNivel;
    }

    private boolean isContaEmExibicao(ContaQuadroPosicaoFinanceira conta) {
        return SimNaoEnum.SIM.equals(conta.getExibir());
    }

    public Map<ContaQuadroPosicaoFinanceira, Label> getMapLabelAjustado() {
        return mapLabelAjustado;
    }

    public ContaQuadroPosicaoFinanceira getContaPai() {
        if (CollectionUtils.isNotEmpty(listaContas)) {
            return listaContas.get(0);
        }
        return null;
    }

    public Map<ContaQuadroPosicaoFinanceira, LabelValor> getMapLabelValor() {
        return mapLabelValor;
    }

    private void configurarHierarquiaLista(final List<ContaQuadroPosicaoFinanceira> listaContas, boolean editable) {
        for (ContaQuadroPosicaoFinanceira conta : new LinkedList<ContaQuadroPosicaoFinanceira>(listaContas)) {
            if (editable) {
                criarHierarquiaInicial(conta);
            }
            verificarCriacaoLinhaEmBranco(listaContas, editable, conta);
            mapaIsContaFilhaSubNivel.put(conta, isFilhoDeSubNivel(conta, editable));
        }
    }

    private void criarHierarquiaInicial(ContaQuadroPosicaoFinanceira conta) {
        List<ContaQuadroPosicaoFinanceira> contasNivelSuperior =
                ContaQuadroPosicaoFinanceiraMediator.get().buscaContasNivelSuperior(
                        new LinkedList<ContaQuadroPosicaoFinanceira>(), conta);
        mapaContasNivelSuperior.put(conta, contasNivelSuperior);
        for (ContaQuadroPosicaoFinanceira contaNivelSuperior : mapaContasNivelSuperior.get(conta)) {
            List<ContaQuadroPosicaoFinanceira> contasFilhasNivel1 =
                    ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                            contaNivelSuperior.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                            contaNivelSuperior.getQuadroPosicaoFinanceira());
            mapaContasFilhasNivel1.put(contaNivelSuperior, contasFilhasNivel1);
            for (ContaQuadroPosicaoFinanceira contaFilhaNivel1 : contasFilhasNivel1) {
                List<ContaQuadroPosicaoFinanceira> contasFilhasNivel2 =
                        ContaQuadroPosicaoFinanceiraMediator.get().buscarContasSubNivelPorPkContaPai(
                                contaFilhaNivel1.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa(),
                                contaFilhaNivel1.getQuadroPosicaoFinanceira());
                mapaContasFilhasNivel2.put(contaFilhaNivel1, contasFilhasNivel2);
            }
        }
    }

    private void verificarCriacaoLinhaEmBranco(final List<ContaQuadroPosicaoFinanceira> listaContas, boolean editable,
            ContaQuadroPosicaoFinanceira conta) {
        LayoutContaAnaliseQuantitativa layout = conta.getLayoutContaAnaliseQuantitativa();
        if (editable && SimNaoEnum.SIM.equals(layout.getSeparacaoDepois())) {
            criarLinhaEmBranco(listaContas, layout, layout.getSequencial());
        } else if (!editable && SimNaoEnum.SIM.equals(layout.getNegrito()) && !conta.isContaRaiz()) {
            criarLinhaEmBranco(listaContas, layout, layout.getSequencial() - 1);
        }
    }

    private void criarLinhaEmBranco(final List<ContaQuadroPosicaoFinanceira> listaContas,
            LayoutContaAnaliseQuantitativa layout, Integer sequencial) {
        ContaAnaliseQuantitativa contaAnaliseQuantitativa = layout.getContaAnaliseQuantitativa();
        ContaQuadroPosicaoFinanceira contaEmBrancoVo = new ContaQuadroPosicaoFinanceira();
        LayoutContaAnaliseQuantitativa layoutContaEmBranco = new LayoutContaAnaliseQuantitativa();
        layoutContaEmBranco.setEditarAjuste(SimNaoEnum.NAO);
        layoutContaEmBranco.setObrigatorio(SimNaoEnum.SIM);
        ContaAnaliseQuantitativa contaAnaliseQuantitativaEmBranco = new ContaAnaliseQuantitativa();
        contaAnaliseQuantitativaEmBranco.setTipoConta(contaAnaliseQuantitativa.getTipoConta());
        contaAnaliseQuantitativaEmBranco.setDiversos(contaAnaliseQuantitativa.getDiversos());
        contaAnaliseQuantitativaEmBranco.setDescricao(BARRA_N);
        layoutContaEmBranco.setContaAnaliseQuantitativa(contaAnaliseQuantitativaEmBranco);
        layoutContaEmBranco.setContaAnaliseQuantitativaPai(layout.getContaAnaliseQuantitativaPai());
        layoutContaEmBranco.setSequencial(sequencial);
        contaEmBrancoVo.setLayoutContaAnaliseQuantitativa(layoutContaEmBranco);
        contaEmBrancoVo.setExibir(SimNaoEnum.NAO);
        contaEmBrancoVo.setEscore(null);
        listaContas.add(contaEmBrancoVo);
    }

    public SimNaoEnum isFilhoDeSubNivel(ContaQuadroPosicaoFinanceira conta, boolean editable) {
        LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa = conta.getLayoutContaAnaliseQuantitativa();
        if (editable && layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai() != null
                && !conta.isLinhaEmBranco()) {
            LayoutContaAnaliseQuantitativa layoutPai =
                    LayoutContaAnaliseQuantitativaMediator.get().obterLayoutPai(
                            layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai().getPk(),
                            layoutContaAnaliseQuantitativa.getLayoutAnaliseQuantitativa().getPk(),
                            layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativa().getTipoConta());
            if (layoutPai != null) {
                return layoutPai.getSubNivel();
            }
        }
        return SimNaoEnum.NAO;
    }

    public String getEscoreFormatado() {
        return escoreFormatado;
    }

    public void setEscoreFormatado(String escoreFormatado) {
        this.escoreFormatado = escoreFormatado;
    }
}
