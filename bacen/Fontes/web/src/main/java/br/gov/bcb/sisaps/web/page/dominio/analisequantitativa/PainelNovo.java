package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public class PainelNovo extends Panel {

    private Object object;

    public PainelNovo(String id, Object object) {
        super(id);
        this.object = object;
        setMarkupId(id + object.hashCode());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        WebMarkupContainer wmc = new WebMarkupContainer("accordion") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new AttributeModifier("id", getId() + object.hashCode()));
            }
        };

        AjaxLink<String> ajaxLink = new AjaxLink<String>("") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                // TODO Auto-generated method stub
            }
        };
        ajaxLink.add(new AttributeModifier("data-parent", getId() + object.hashCode()));
        wmc.add(ajaxLink);
        add(wmc);
    }

}
