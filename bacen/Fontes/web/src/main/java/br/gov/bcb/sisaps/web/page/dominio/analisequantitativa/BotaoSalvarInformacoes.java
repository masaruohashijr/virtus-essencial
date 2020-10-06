package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;

public abstract class BotaoSalvarInformacoes extends AjaxSubmitLink {
    private static final String MASK_HIDE = "Mask.hide('";
    private static final String FECHA_PARENTESES = "');";

    public BotaoSalvarInformacoes() {
        super("bttSalvarInformacoes");
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        executarSubmissao(target);
    }

    protected abstract void executarSubmissao(AjaxRequestTarget target);

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
                return "Mask.show('" + BotaoSalvarInformacoes.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getSuccessHandler(Component component) {
                return MASK_HIDE + BotaoSalvarInformacoes.this.getMarkupId() + FECHA_PARENTESES;
            }

            @Override
            public CharSequence getFailureHandler(Component component) {
                return MASK_HIDE + BotaoSalvarInformacoes.this.getMarkupId() + FECHA_PARENTESES;
            }

        });
    }

}
