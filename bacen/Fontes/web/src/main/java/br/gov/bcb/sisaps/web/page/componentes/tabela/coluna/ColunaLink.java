package br.gov.bcb.sisaps.web.page.componentes.tabela.coluna;

import org.apache.wicket.markup.html.WebMarkupContainer;

import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class ColunaLink extends PainelSisAps {

    public ColunaLink(String id, WebMarkupContainer link) {
        super(id);
        add(link);
    }

}