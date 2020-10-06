package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class LinkScroll extends AjaxLink<Object> {
    private WebMarkupContainer wmc;

    public LinkScroll(String id, WebMarkupContainer wmc) {
        super(id);
        this.wmc = wmc;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        target.focusComponent(wmc);
    }
}
