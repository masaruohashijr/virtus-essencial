package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.buildTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.getFormatoCabecalho;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraNumericaBehaviour;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.FormatoColunaBold;

public class BuildTabelaResultados implements Serializable {
    private static final String UNCHECKED = "unchecked";
    private static final String FRAGMENT_INPUT_AJUSTE = "fragmentInputAjuste";
    private static final String FRAGMENT_INPUT_AJUSTADO = "fragmentInputAjustado";
    private static final String WIDTH_15 = "width: 15%;";
    private static final String AJUSTADO = "Ajustado";
    private Map<ResultadoQuadroPosicaoFinanceira, Label> mapLabelAjustado =
            new HashMap<ResultadoQuadroPosicaoFinanceira, Label>();
    private QuadroPosicaoFinanceiraVO vo;
    private String id;

    public BuildTabelaResultados(String id) {
        this.id = id;
    }

    protected void criarTabela(WebMarkupContainer container, final QuadroPosicaoFinanceiraVO vo,
            Configuracao configuracao, boolean editable) {
        this.vo = vo;
        List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas =
                new LinkedList<Coluna<ResultadoQuadroPosicaoFinanceira>>();
        configurarColunas(container, vo, editable, colunas);
        container.addOrReplace(buildTabela(
                id,
                new LoadableDetachableModel<List<ResultadoQuadroPosicaoFinanceira>>() {
                    @Override
                    protected List<ResultadoQuadroPosicaoFinanceira> load() {
                        return vo.getResultados() == null ? new LinkedList<ResultadoQuadroPosicaoFinanceira>() : vo
                                .getResultados();
                    }
                }, configuracao, colunas, property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                        .getPeriodo()),
                SortOrder.ASCENDING));

    }

    private void configurarColunas(WebMarkupContainer container, final QuadroPosicaoFinanceiraVO vo, boolean editable,
            List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        buildColunaPeriodo(colunas);
        buildColunaLucro(colunas);
        int temLucroAjustado = 0;
        int temRsplaAjustado = 0;
        for (ResultadoQuadroPosicaoFinanceira resultado : vo.getResultados()) {
            if (resultado.getLucroLiquidoAjustado() != null) {
                temLucroAjustado++;
            }
            if (resultado.getRsplaAjustado() != null) {
                temRsplaAjustado++;
            }
        }
        if (editable) {
            buildColunaAjuste(container, colunas);
            buildColunaAjustado(colunas);
        } else {
            if (temLucroAjustado > 0) {
                colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>()
                        .setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho(AJUSTADO)
                        .setComponente(new IColunaComponente<ResultadoQuadroPosicaoFinanceira>() {
                            @Override
                            public Component obterComponente(
                                    Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                                    String componentId, final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
                                return criarLabelLucroAjustado(componentId, rowModel);
                            }
                        })
                        .setPropriedade(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                                        .getLucroLiquidoAjustado())));
            }
        }
        buildColunaRspla(colunas);
        if (editable) {
            buildColunaRplaAjustado(container, colunas);
        } else {
            if (temRsplaAjustado > 0) {
                colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>()
                        .setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho(AJUSTADO)
                        .setPropriedade(
                                property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getRsplaAjustado()))
                        .setPropriedadeTela(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                                        .getRsplaAjustadoFormatado())));
            }
        }
    }

    private void buildColunaLucro(List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>()
                .setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho("Lucro")
                .setComponente(new IColunaComponente<ResultadoQuadroPosicaoFinanceira>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                            String componentId, final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
                        return criarLabelLucro(componentId, rowModel);
                    }
                })
                .setPropriedade(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getLucroLiquido())));
    }

    private Component criarLabelLucro(String componentId,
            final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
        Label labelLucro = new Label(componentId, new Model<ResultadoQuadroPosicaoFinanceira>()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setDefaultModel(new PropertyModel<ResultadoQuadroPosicaoFinanceira>(rowModel
                        .getObject(),
                        property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                                .getLucroLiquido())));
            }

            @SuppressWarnings(UNCHECKED)
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new FormatarNumerico();
            }
        };
        return labelLucro;
    }

    private Component criarLabelLucroAjustado(String componentId,
            final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
        Label labelLucroAjustado =
                new Label(componentId, new Model<ResultadoQuadroPosicaoFinanceira>()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setDefaultModel(new PropertyModel<ResultadoQuadroPosicaoFinanceira>(
                                rowModel.getObject(), property(getPropertyObject(
                                        ResultadoQuadroPosicaoFinanceira.class)
                                        .getLucroLiquidoAjustado())));
                    }

                    @SuppressWarnings(UNCHECKED)
                    @Override
                    public <C> IConverter<C> getConverter(Class<C> type) {
                        return (IConverter<C>) new FormatarNumerico();
                    }
                };
        return labelLucroAjustado;
    }

    private void buildColunaPeriodo(List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>()
                .setEstiloCabecalho(getFormatoCabecalho("width: 25%;"))
                .setCabecalho("Período")
                .setComponente(new ColunaPeriodo())
                .setPropriedadeTela(
                        property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getPeriodoFormatado()))
                .setFormatador(new FormatoColunaBold<ResultadoQuadroPosicaoFinanceira>()));
    }

    private void buildColunaAjuste(final WebMarkupContainer container,
            List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                .setCabecalho("Ajuste").setComponente(new ColunaAjuste(container)));
    }

    private class ColunaAjuste implements IColunaComponente<ResultadoQuadroPosicaoFinanceira> {

        private MarkupContainer container;

        public ColunaAjuste(MarkupContainer container) {
            this.container = container;
        }

        @Override
        public Component obterComponente(Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                String componentId, final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
            FragmentoAjusteAjustado fragmentoAjuste =
                    new FragmentoAjusteAjustado(componentId, FRAGMENT_INPUT_AJUSTE, container, rowModel,
                            property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getAjuste()),
                            "inputAjuste", "asteriscoEdicaoAjuste") {
                        @SuppressWarnings(UNCHECKED)
                        @Override
                        protected <C> IConverter<C> getConverterTextField(Class<C> type) {
                            return (IConverter<C>) new FormatarNumerico();
                        }

                        @Override
                        protected void onAddConfiguracaoTextField(
                                TextField<ResultadoQuadroPosicaoFinanceira> textFieldCustomizado) {
                            textFieldCustomizado.setMarkupId("inputAjusteLucro" + getObjeto().getPeriodo());
                            textFieldCustomizado.add(new AttributeModifier("onkeyup", RETURN_SO_NUMEROS_THIS),
                                    new AttributeModifier("onmousemove", RETURN_SO_NUMEROS_THIS));
                        }
                    };
            fragmentoAjuste.setMarkupId(FRAGMENT_INPUT_AJUSTE + componentId);
            return fragmentoAjuste;
        }
    }

    private class ColunaPeriodo implements IColunaComponente<ResultadoQuadroPosicaoFinanceira> {

        @Override
        public Component obterComponente(Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                String componentId, IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
            String periodoCustomizado = rowModel.getObject().getPeriodoFormatado();
            if (CollectionUtils.isNotEmpty(vo.getResultados())) {
                List<ResultadoQuadroPosicaoFinanceira> resultados = vo.getResultados();
                if (rowModel.getObject().equals(resultados.get(resultados.size() - 1))) {
                    periodoCustomizado = periodoCustomizado.concat("*");
                }
            }
            return new Label(componentId, new Model<String>(periodoCustomizado));
        }
    }

    private void buildColunaRplaAjustado(final WebMarkupContainer container,
            List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        colunas.add(new Coluna<ResultadoQuadroPosicaoFinanceira>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                .setCabecalho(AJUSTADO).setComponente(new ColunaAjustado(container)));
    }

    private void prepararTargetAjuste(TextField<ResultadoQuadroPosicaoFinanceira> textField,
            final IModel<ResultadoQuadroPosicaoFinanceira> rowModel, AjaxRequestTarget target) {
        Label lb = mapLabelAjustado.get(rowModel.getObject());
        GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) textField.getPage();
        PainelAjusteResultado painelaAjusteResultado = pageAtual.getPainelAjusteResultado();

        if (textField.getValue() != null && textField.getValue().isEmpty()) {
            lb.setDefaultModelObject(null);
            painelaAjusteResultado.setInformacoesNaoSalvas(Boolean.FALSE);
        } else {
            lb.setDefaultModel(textField.getModel());
            painelaAjusteResultado.setInformacoesNaoSalvas(Boolean.TRUE);
        }
        target.add(pageAtual.getPainelQuadroPosicaoFinanceira());
        target.add(painelaAjusteResultado.getLabelInfo());
        target.add(lb, lb.getMarkupId());

    }

    private void buildColunaAjustado(List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        Coluna<ResultadoQuadroPosicaoFinanceira> colunaAjustado =
                new Coluna<ResultadoQuadroPosicaoFinanceira>()
                        .setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                        .setCabecalho(AJUSTADO)
                        .setPropriedade(
                                property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                                        .getLucroLiquidoAjustado()))
                        .setComponente(new IColunaComponente<ResultadoQuadroPosicaoFinanceira>() {
                            @Override
                            public Component obterComponente(
                                    Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                                    String componentId, final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
                                return criarLabelAjustado(componentId, rowModel);
                            }
                        });

        colunas.add(colunaAjustado);
    }
    
    private Component criarLabelAjustado(String componentId,
            final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
        Label labelAjustado =
                new Label(componentId, new Model<ResultadoQuadroPosicaoFinanceira>()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setDefaultModel(new PropertyModel<ResultadoQuadroPosicaoFinanceira>(
                        rowModel.getObject(), property(getPropertyObject(
                                ResultadoQuadroPosicaoFinanceira.class)
                                .getLucroLiquidoAjustado())));
            }
            
            @SuppressWarnings(UNCHECKED)
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new FormatarNumerico();
            }
        };
        mapLabelAjustado.put(rowModel.getObject(), labelAjustado);
        return mapLabelAjustado.get(rowModel.getObject());
    }

    private void buildColunaRspla(List<Coluna<ResultadoQuadroPosicaoFinanceira>> colunas) {
        Coluna<ResultadoQuadroPosicaoFinanceira> colunaAjustado =
                new Coluna<ResultadoQuadroPosicaoFinanceira>()
                        .setEstiloCabecalho(getFormatoCabecalho(WIDTH_15))
                        .setCabecalho("RSPLA")
                        .setPropriedadeTela(
                                property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getRsplaFormatado()))
                        .setPropriedade(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getRspla()));
        colunas.add(colunaAjustado);
    }

    private class ColunaAjustado implements IColunaComponente<ResultadoQuadroPosicaoFinanceira> {

        private MarkupContainer container;

        public ColunaAjustado(MarkupContainer container) {
            this.container = container;
        }

        @Override
        public Component obterComponente(Item<ICellPopulator<ResultadoQuadroPosicaoFinanceira>> cellItem,
                String componentId, final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
            FragmentoAjusteAjustado fragmentoAjustado =
                    new FragmentoAjusteAjustado(componentId, FRAGMENT_INPUT_AJUSTADO, container, rowModel,
                            property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getRsplaAjustado()),
                            "inputAjustado", "asteriscoEdicaoAjustado") {

                        @Override
                        protected void onConfigure() {
                            super.onConfigure();
                        }

                        @Override
                        protected void onAddConfiguracaoTextField(TextField<ResultadoQuadroPosicaoFinanceira> textField) {
                            if (textField != null) {
                                textField.setModel(new PropertyModel<ResultadoQuadroPosicaoFinanceira>(rowModel
                                        .getObject(),
                                        property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                                                .getRsplaAjustado())));
                                textField.setMarkupId("inputAjusteRSPLA" + getObjeto().getPeriodo());
                                textField.add(new MascaraNumericaBehaviour("##,#", 5));
                            }
                        }
                    };
            fragmentoAjustado.setMarkupId(FRAGMENT_INPUT_AJUSTADO + componentId);
            return fragmentoAjustado;
        }
    }

    private boolean isPeriodoAtual(final IModel<ResultadoQuadroPosicaoFinanceira> rowModel) {
        return CollectionUtils.isNotEmpty(vo.getResultados())
                && rowModel.getObject().equals(vo.getResultados().get(vo.getResultados().size() - 1));
    }

    private class FragmentoAjusteAjustado extends ColunaFragmentValorAjusteAjustado<ResultadoQuadroPosicaoFinanceira> {
        private IModel<ResultadoQuadroPosicaoFinanceira> rowModel;
        private String idCustomizado;
        private String labelIdCustomizado;

        public FragmentoAjusteAjustado(String componentId, String fragmentId, MarkupContainer container,
                IModel<ResultadoQuadroPosicaoFinanceira> rowModel, String expressao, String idCustomizado,
                String labelIdCustomizado) {
            super(componentId, fragmentId, container, rowModel.getObject(), expressao, isPeriodoAtual(rowModel));
            this.rowModel = rowModel;
            this.idCustomizado = idCustomizado;
            this.labelIdCustomizado = labelIdCustomizado;
        }

        @Override
        protected void executorOnUpdateExterno(AjaxRequestTarget target) {
            prepararTargetAjuste(getTextField(), rowModel, target);
        }

        @Override
        protected String getIdCustomizado() {
            return idCustomizado;
        }

        @Override
        protected String getLabelIdCustomizado() {
            return labelIdCustomizado;
        }
    }
}
