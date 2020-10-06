package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

public interface IColunaComponente<T> extends Serializable {

    Component obterComponente(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel);
}