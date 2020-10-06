package br.gov.bcb.sisaps.web.page.componentes.botoes;

import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;

public abstract class BotaoConfirmacao extends AjaxSubmitLinkSisAps {
    private static final long serialVersionUID = 1L;
    private String text;

    public BotaoConfirmacao(String id, String text) {
        super(id);
        this.text = text;
    }
    
    public BotaoConfirmacao(String id, String text, boolean isIndicator) {
        super(id, isIndicator);
        this.text = text;
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        AjaxCallListener ajaxCallListener = new AjaxCallListener();
        if (isIndicator) {
            ajaxCallListener.onPrecondition("var r = confirm('" + text + "'); if (r == false) { " 
                    + MASK_HIDE + getMarkupId() + FECHA_PARENTESES + " return r; }");
        } else {
            ajaxCallListener.onPrecondition("return confirm('" + text + "');");
        }
        attributes.getAjaxCallListeners().add(ajaxCallListener);
    }

}
