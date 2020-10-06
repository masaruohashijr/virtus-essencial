package br.gov.bcb.sisaps.web.page.componentes.tabela.paginador;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationIncrementLink;

public class PaginadorNavigationIncrementLink<T> extends PagingNavigationIncrementLink<T> {

    public PaginadorNavigationIncrementLink(String id, IPageable pageable, int increment) {
        super(id, pageable, increment);
    }

    @Override
    public String getBeforeDisabledLink() {
        return ConstantesPaginador.INICIO_DESABILITADO;
    }

    @Override
    public String getAfterDisabledLink() {
        return ConstantesPaginador.FINAL_DESABILITADOR;
    }
}