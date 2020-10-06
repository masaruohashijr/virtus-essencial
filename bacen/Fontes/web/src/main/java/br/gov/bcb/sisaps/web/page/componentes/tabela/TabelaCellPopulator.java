package br.gov.bcb.sisaps.web.page.componentes.tabela;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class TabelaCellPopulator<T> implements ICellPopulator<T> {
    private final Coluna<T> coluna;

    public TabelaCellPopulator(Coluna<T> coluna) {
        this.coluna = coluna;
    }

    public void detach() {
        // não precisa implementar
    }

    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        IColunaComponente<T> componente = coluna.getComponente();
        if (componente == null) {
            String propriedade = coluna.getPropriedade();
            if (propriedade == null) {
                throw new IllegalArgumentException("Ajuste a propriedade do objeto para a coluna:" + coluna);
            } else {
                cellItem.add(componentePadrao(componentId, rowModel, propriedade));

            }
        } else {
            cellItem.add(componente.obterComponente(cellItem, componentId, rowModel));
        }
        IColunaFormatador<T> formatador = coluna.getFormatador();
        if (formatador != null) {
            String css = formatador.obterCss(rowModel.getObject());
            if (css != null) {
                cellItem.add(new AttributeAppender(ConstantesWeb.CLASS, Model.of(css), ConstantesWeb.SEPARATOR_CLASS));
            }
            String style = formatador.obterStyle(rowModel.getObject());
            if (style != null) {
                cellItem.add(new AttributeAppender(ConstantesWeb.STYLE, Model.of(style), ConstantesWeb.SEPARATOR_STYLE));
            }
        }

    }

    protected Label componentePadrao(String componentId, IModel<T> rowModel, String propriedade) {
        String propriedadeTela = coluna.getPropriedadeTela() == null ? propriedade : coluna.getPropriedadeTela();
        Label label = new Label(componentId, new PropertyModel<T>(rowModel.getObject(), propriedadeTela));
        label.setEscapeModelStrings(!coluna.isEscapeModelStrings());
        return label;
    }
}