package br.gov.bcb.sisaps.web.page.componentes.modal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;

public abstract class AjaxSubmitLinkModalWindow extends AjaxSubmitLink {
    private static final String FEEDBACKMODALWINDOW = "feedbackmodalwindow";
    private boolean isTabela;

    public AjaxSubmitLinkModalWindow(String id) {
        this(id, false);
    }

    public AjaxSubmitLinkModalWindow(String id, boolean isTabela) {
        super(id);
        this.isTabela = isTabela;
        setOutputMarkupId(true);
        setMarkupId(id);
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
        } catch (StaleObjectStateException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (ConstraintViolationException e) {
            ExceptionUtils.tratarErroConstraint(e, getPage());
        }

        if (isTabela) {
            target.add(getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent()
                    .getParent().getParent().getParent().getParent().getParent().get(FEEDBACKMODALWINDOW));
        } else {
            target.add(getParent().getParent().get(FEEDBACKMODALWINDOW));
        }
    }

    /**
     * Método acionado na execução do submit.
     */
    public abstract void executeSubmit(AjaxRequestTarget target);

}
