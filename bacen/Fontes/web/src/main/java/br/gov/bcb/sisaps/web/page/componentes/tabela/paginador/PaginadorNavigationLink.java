package br.gov.bcb.sisaps.web.page.componentes.tabela.paginador;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationLink;

public class PaginadorNavigationLink<T> extends PagingNavigationLink<T> {

    public PaginadorNavigationLink(String id, IPageable pageable, long pageNumber) {
        super(id, pageable, pageNumber);
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
