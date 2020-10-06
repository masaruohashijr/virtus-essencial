package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.buildTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.getFormatoCabecalho;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import br.gov.bcb.sisaps.src.vo.analisequantitativa.PatrimonioVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.FormatoColunaBold;

public class BuildTabelaPatrimonioReferencia implements Serializable {
    private static final String WIDTH_15 = "width: 15%;";
    private static final String AJUSTADO = "Ajustado";
    private Map<PatrimonioVO, Label> mapLabel = new HashMap<PatrimonioVO, Label>();
    private Map<PatrimonioVO, ColunaFragmentValorAjusteAjustado<PatrimonioVO>> mapFragmentoAjuste = //
            new HashMap<PatrimonioVO, ColunaFragmentValorAjusteAjustado<PatrimonioVO>>();
    private WebMarkupContainer container;
    private String id;

    public BuildTabelaPatrimonioReferencia(String id) {
        this.id = id;
    }

    protected void criarTabela(final WebMarkupContainer container, final QuadroPosicaoFinanceiraVO vo,
            Configuracao cfg, boolean editable) {

        this.container = container;
        List<Coluna<PatrimonioVO>> colunas = new LinkedList<Coluna<PatrimonioVO>>();
        colunas.add(new Coluna<PatrimonioVO>().setEstiloCabecalho("background-color:#fff; color:#000; width: 55%;")
                .setCabecalho("").setFormatador(new FormatoColunaBold<PatrimonioVO>())
                .setPropriedade(property(getPropertyObject(PatrimonioVO.class).getDescricaoPatrimonio())));

        colunas.add(new Coluna<PatrimonioVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho("Valor")
                .setPropriedadeTela(property(getPropertyObject(PatrimonioVO.class).getValorPatrimonioFormatado()))
                .setPropriedade(property(getPropertyObject(PatrimonioVO.class).getValorPatrimonio())));

        if (editable) {
            buildColunaAjuste(colunas);
        }

        boolean temValorAjustado = false;
        for (PatrimonioVO patrimonioVO : vo.getPatrimonios()) {
            if (patrimonioVO.getValorPatrimonioAjustado() != null) {
                temValorAjustado = true;
                break;
            }
        }
        if (editable || temValorAjustado) {
            buildColunaAjustado(colunas);
        }

        container.addOrReplace(buildTabela(id, new LoadableDetachableModel<List<PatrimonioVO>>() {
            @Override
            protected List<PatrimonioVO> load() {
                return vo.getPatrimonios() == null ? new ArrayList<PatrimonioVO>() : vo.getPatrimonios();
            }
        }, cfg, colunas, property(getPropertyObject(PatrimonioVO.class).getValorPatrimonio()), SortOrder.DESCENDING));
    }

    private void buildColunaAjustado(List<Coluna<PatrimonioVO>> colunas) {
        Coluna<PatrimonioVO> colunaAjustado =
                new Coluna<PatrimonioVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho(AJUSTADO)
                        .setComponente(new IColunaComponente<PatrimonioVO>() {
                            @Override
                            public Component obterComponente(Item<ICellPopulator<PatrimonioVO>> cellItem,
                                    String componentId, final IModel<PatrimonioVO> rowModel) {
                                return criarComponente(componentId, rowModel);
                            }
                        });
        colunaAjustado.setPropriedade(property(getPropertyObject(PatrimonioVO.class).getValorPatrimonioAjustado()));
        colunas.add(colunaAjustado);
    }
    
    private Component criarComponente(String componentId, final IModel<PatrimonioVO> rowModel) {
        Label labelAjustado = new Label(componentId, new Model<PatrimonioVO>()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setDefaultModel(new PropertyModel<PatrimonioVO>(rowModel.getObject(),
                        property(getPropertyObject(PatrimonioVO.class)
                                .getValorPatrimonioAjustado())));
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new FormatarNumerico();
            }
        };
        mapLabel.put(rowModel.getObject(), labelAjustado);
        return mapLabel.get(rowModel.getObject());
    }

    private void buildColunaAjuste(List<Coluna<PatrimonioVO>> colunas) {
        colunas.add(new Coluna<PatrimonioVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho("Ajuste")
                .setComponente(new ColunaAjustePatrimonio())
                .setPropriedade(property(getPropertyObject(PatrimonioVO.class).getAjustePatrimonio())));

    }

    private void prepararTargetAjuste(TextField<PatrimonioVO> textField, final IModel<PatrimonioVO> rowModel,
            AjaxRequestTarget target, boolean isMostrarInformacoesNaoSalvas) {
        Label lb = mapLabel.get(rowModel.getObject());
        GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) textField.getPage();
        PainelAjustePatrimonioReferencia painelaAjustePatrimonio = pageAtual.getPainelAjustePatrimonioReferencia();
        if (textField.getValue() != null && textField.getValue().isEmpty()) {
            lb.setDefaultModelObject(null);
        } else {
            lb.setDefaultModel(textField.getModel());
        }

        if (isMostrarInformacoesNaoSalvas) {
            painelaAjustePatrimonio.setInformacoesNaoSalvas(Boolean.TRUE);
        } else {
            painelaAjustePatrimonio.setInformacoesNaoSalvas(Boolean.FALSE);
        }
        target.add(pageAtual.getPainelQuadroPosicaoFinanceira());
        target.add(painelaAjustePatrimonio.getLabelInfo());
        target.add(lb, lb.getMarkupId());
    }

    private class ColunaAjustePatrimonio implements IColunaComponente<PatrimonioVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<PatrimonioVO>> cellItem, String componentId,
                final IModel<PatrimonioVO> rowModel) {
            ColunaFragmentAjuste colunaFragmentAjuste =
                    new ColunaFragmentAjuste(componentId, "fragmentInput", container, rowModel,
                            property(getPropertyObject(PatrimonioVO.class).getAjustePatrimonio()));
            mapFragmentoAjuste.put(rowModel.getObject(), colunaFragmentAjuste);
            return mapFragmentoAjuste.get(rowModel.getObject());
        }
    }

    private class ColunaFragmentAjuste extends ColunaFragmentValorAjusteAjustado<PatrimonioVO> {
        private IModel<PatrimonioVO> rowModel;

        public ColunaFragmentAjuste(String id, String markupId, MarkupContainer markupProvider,
                IModel<PatrimonioVO> rowModel, String expressao) {
            super(id, markupId, markupProvider, rowModel.getObject(), expressao);
            this.rowModel = rowModel;
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
        }

        @Override
        protected void executorOnUpdateExterno(AjaxRequestTarget target) {
            super.executorOnUpdateExterno(target);
            int temAsterisco = 0;
            for (ColunaFragmentValorAjusteAjustado<?> frag : mapFragmentoAjuste.values()) {
                if (frag.getIsMostrar()) {
                    temAsterisco++;
                }
            }
            prepararTargetAjuste(getTextField(), rowModel, target, temAsterisco > 0);
        }

        @Override
        protected void onAddConfiguracaoTextField(TextField<PatrimonioVO> textFieldCustomizado) {
            textFieldCustomizado.setMarkupId("inputAjuste" + getObjeto().getDescricaoPatrimonio().replace(" ", ""));
            textFieldCustomizado.add(new AttributeModifier("onkeyup", RETURN_SO_NUMEROS_THIS), new AttributeModifier(
                    "onmousemove", RETURN_SO_NUMEROS_THIS));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <C> IConverter<C> getConverterTextField(Class<C> type) {
            return (IConverter<C>) new FormatarNumerico();
        }
    }

}
