package br.gov.bcb.sisaps.web.page.componentes.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.PackageResourceReference;

public abstract class AjaxButtonIndicator extends AjaxButton {

    private static final String FECHA_PARENTESES = "');";
    private static final String MASK_HIDE = "Mask.hide('";

    public AjaxButtonIndicator(String id) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxButtonIndicator.class,
                "res/indicator.css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxButtonIndicator.class,
                "res/indicator.js")));
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        attributes.getAjaxCallListeners().add(new AjaxCallListener() {
            @Override
            public CharSequence getPrecondition(Component component) {
                return "Mask.show('" + AjaxButtonIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getSuccessHandler(Component component) {
                return MASK_HIDE + AjaxButtonIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getFailureHandler(Component component) {
                return MASK_HIDE + AjaxButtonIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

        });
    }

}
