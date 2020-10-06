package br.gov.bcb.sisaps.web.page.componentes.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public abstract class AjaxLinkIndicator<T> extends AjaxLink<T> {

    private static final String MASK_HIDE = "Mask.hide('";
    private static final String FECHA_PARENTESES = "');";

    public AjaxLinkIndicator(String id, IModel<T> model) {
        super(id, model);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
    }

    public AjaxLinkIndicator(String id) {
        this(id, null);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                "res/indicator.css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                "res/indicator.js")));
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        attributes.getAjaxCallListeners().add(new AjaxCallListener() {
            @Override
            public CharSequence getPrecondition(Component component) {
                return "Mask.show('" + AjaxLinkIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getSuccessHandler(Component component) {
                return MASK_HIDE + AjaxLinkIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getFailureHandler(Component component) {
                return MASK_HIDE + AjaxLinkIndicator.this.getMarkupId() + FECHA_PARENTESES;
            }

        });
    }

}
