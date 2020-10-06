package br.gov.bcb.sisaps.web.page.componentes.tabela.paginador;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationLink;
import org.apache.wicket.model.Model;

public class PaginadorNavigation extends PagingNavigation {

    public PaginadorNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
        setSeparator(ConstantesPaginador.SEPARADOR);
        setViewSize(ConstantesPaginador.JANELA);
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, long pageIndex) {
        PagingNavigationLink<Void> link = new PagingNavigationLink<Void>(id, pageable, pageIndex) {
            @Override
            public String getBeforeDisabledLink() {
                return ConstantesPaginador.INICIO_DESABILITADO;
            }

            @Override
            public String getAfterDisabledLink() {
                return ConstantesPaginador.FINAL_DESABILITADOR;
            }
        };
        link.setOutputMarkupId(true);
        link.setMarkupId("idPagina" + (pageIndex + 1));
        link.add(new AttributeAppender("class", Model.of(getCssPaginas()), " "));
        return link;
    }

    public String getCssPaginas() {
        return "link_paginador";
    }
}
