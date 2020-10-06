package br.gov.bcb.sisaps.web.page.componentes.tabela.paginador;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class Paginador extends PagingNavigator {

    public Paginador(String id, final DataGridView<?> grid) {
        super(id, grid);
        final WebMarkupContainer plural = new WebMarkupContainer("plural") {
            @Override
            public boolean isVisible() {
                return grid.getItemCount() > 1;
            }
        };
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "" + grid.getItemCount();
            }
        };
        String total = "total";
        plural.add(new Label(total, model));
        add(plural);
        WebMarkupContainer singular = new WebMarkupContainer("singular") {
            @Override
            public boolean isVisible() {
                return !plural.isVisible();
            }
        };
        singular.add(new Label(total, model));
        add(singular);

        add(new Label("inicio", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "" + ((grid.getCurrentPage() * grid.getItemsPerPage()) + 1);
            }
        }));
        add(new Label("fim", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return "" + Math.min((grid.getCurrentPage() + 1) * grid.getItemsPerPage(), grid.getItemCount());
            }
        }));
    }

    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new PaginadorNavigationLink<Void>(id, pageable, pageNumber);
    }

    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new PaginadorNavigationIncrementLink<Void>(id, pageable, increment);
    }

    @Override
    protected PagingNavigation newNavigation(final String id, final IPageable pageable,
            final IPagingLabelProvider labelProvider) {
        return new PaginadorNavigation(id, pageable, labelProvider);
    }
}
