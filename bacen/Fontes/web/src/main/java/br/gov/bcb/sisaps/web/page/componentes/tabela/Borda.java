package br.gov.bcb.sisaps.web.page.componentes.tabela;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;

@SuppressWarnings("serial")
public class Borda extends OrderByBorder<String> {

    public Borda(String id, String property, ISortStateLocator<String> stateLocator) {
        super(id, property, stateLocator);
    }
}