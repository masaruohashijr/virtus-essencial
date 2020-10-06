package br.gov.bcb.sisaps.web.page.componentes;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.web.page.componentes.tabela.ProviderOrdenado;

@SuppressWarnings("serial")
public class ProviderGenericoList<T> extends ProviderOrdenado<T> {

    private IModel<List<T>> modelLista;
    private Comparador comparador = new Comparador();

    public ProviderGenericoList(String propriedade, SortOrder ordem, IModel<List<T>> model) {
        super(propriedade, ordem);
        this.modelLista = model;
    }

    @Override
    public Iterator<? extends T> iterator(final String propriedade, final SortOrder ordem, long first, long count) {
        List<T> lista = modelLista.getObject();
        if (!"".equals(propriedade)) {
            comparador.setPropriedade(propriedade);
            comparador.setOrdem(ordem);
            Collections.sort(lista, comparador);
        }
        List<T> resultado = lista.subList((int) first, (int) (first + count));
        return resultado.iterator();
    }

    @Override
    public long size() {
        return modelLista.getObject().size();
    }

    @Override
    public IModel<T> model(final T object) {
        return new AbstractReadOnlyModel<T>() {

            @Override
            public T getObject() {
                return object;
            }
        };
    }

    private final class Comparador implements Comparator<T>, Serializable {
        private String propriedade;
        private SortOrder ordem;

        public void setPropriedade(String propriedade) {
            this.propriedade = propriedade;
        }

        public void setOrdem(SortOrder ordem) {
            this.ordem = ordem;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public int compare(T o1, T o2) {
            int tmp = 0;
            Object left = new PropertyModel<T>(o1, propriedade).getObject();
            Object right = new PropertyModel<T>(o2, propriedade).getObject();
            if (left instanceof Comparable && right instanceof Comparable) {
                tmp = ((Comparable) left).compareTo((Comparable) right);
            } else {
                tmp = String.valueOf(left).compareTo(String.valueOf(right));
            }
            return ordem == SortOrder.ASCENDING ? tmp : -tmp;
        }
    }
}