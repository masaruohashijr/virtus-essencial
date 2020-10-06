package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

@SuppressWarnings("serial")
public abstract class ProviderOrdenado<T> extends SortableDataProvider<T, String> {

    public ProviderOrdenado(String propriedade, SortOrder ordem) {
        setSort(propriedade, ordem);
    }

    public Iterator<? extends T> iterator(long first, long count) {
        String propriedade = getSort() == null ? " " : getSort().getProperty();
        return iterator(propriedade, getSortState() == null ? SortOrder.NONE
                : getSortState().getPropertySortOrder(propriedade), first, count);
    }

    public abstract Iterator<? extends T> iterator(String propriedade, SortOrder ordem, long first, long count);
}
