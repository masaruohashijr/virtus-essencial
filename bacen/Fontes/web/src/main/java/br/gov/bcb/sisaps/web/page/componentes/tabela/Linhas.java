package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class Linhas<T> extends DataGridView<T> {
    private Configuracao configuracao;

    public Linhas(String id, List<? extends ICellPopulator<T>> populators, IDataProvider<T> dataProvider,
            Configuracao configuracao) {
        super(id, populators, dataProvider);
        this.configuracao = configuracao;
        if (configuracao.isExibirPaginador()) {
            setItemsPerPage(configuracao.getTamanho().getObject());
        }
    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> model) {
        return new Item<T>(id, index, model) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put(ConstantesWeb.CLASS,
                        ((getIndex() % 2 == 0) ? configuracao.getCssPar() : configuracao.getCssImpar()).getObject());
                tag.put(ConstantesWeb.STYLE, configuracao.getStyleLinhas().getObject());
                super.onComponentTag(tag);
            }
        };
    }
}
