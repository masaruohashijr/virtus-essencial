package br.gov.bcb.sisaps.web.page.componentes.botoes;

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
import org.hibernate.ObjectNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;

public abstract class AjaxSubmitLinkSisAps extends AjaxSubmitLink {

    protected static final String MASK_HIDE = "Mask.hide('";
    protected static final String FECHA_PARENTESES = "');";
    protected final boolean isIndicator;
    
    public AjaxSubmitLinkSisAps(String id) {
        this(id, false);
    }
    
    public AjaxSubmitLinkSisAps(String id, boolean isIndicator) {
        super(id);
        this.isIndicator = isIndicator;
        setOutputMarkupId(true);
        setMarkupId(id);
    }
    
    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (isIndicator) {
            response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                    "res/indicator.css")));
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                    "res/indicator.js")));
        }
    }
    
    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        getPage().getFeedbackMessages().clear();
        try {
            executeSubmit(target);
        } catch (InvalidStateException e) {
            ExceptionUtils.tratarInvalidStateException(e, getPage());
        } catch (NegocioException e) {
            ExceptionUtils.tratarNegocioException(e, getPage());
        } catch (HibernateOptimisticLockingFailureException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (ObjectNotFoundException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (StaleObjectStateException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (ConstraintViolationException e) {
            ExceptionUtils.tratarErroConstraint(e, getPage());
        }
        target.add(getPage().get("feedback"));
        Component scriptErro = getPage().get("scriptErro");
        if (scriptErro != null) {
            target.add(scriptErro);
        }
    }
    
    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        if (isIndicator) {
            attributes.getAjaxCallListeners().add(new AjaxCallListener() {
                @Override
                public CharSequence getPrecondition(Component component) {
                    return "Mask.show('" + AjaxSubmitLinkSisAps.this.getMarkupId() + FECHA_PARENTESES;
                }

                @Override
                public CharSequence getSuccessHandler(Component component) {
                    return MASK_HIDE + AjaxSubmitLinkSisAps.this.getMarkupId() + FECHA_PARENTESES;
                }

                @Override
                public CharSequence getFailureHandler(Component component) {
                    return MASK_HIDE + AjaxSubmitLinkSisAps.this.getMarkupId() + FECHA_PARENTESES;
                }

            });
        }
    }

    /**
     * Método acionado na execução do submit.
     */
    public abstract void executeSubmit(AjaxRequestTarget target);

}
