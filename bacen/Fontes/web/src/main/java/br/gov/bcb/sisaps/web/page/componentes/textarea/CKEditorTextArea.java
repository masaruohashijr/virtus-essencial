package br.gov.bcb.sisaps.web.page.componentes.textarea;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;

import br.gov.bcb.sisaps.web.page.DefaultPage;

public class CKEditorTextArea<T> extends TextArea<T> {

    public CKEditorTextArea(String id, IModel<T> model) {
        super(id, model);
        setMarkupId(id);
    }

    @Override
    protected void onRender() {
        super.onRender();
        if (isEnabled() && !DefaultPage.isUsarHtmlUnit()) {
            Response response = getResponse();
            JavaScriptUtils.writeJavaScript(response, obterCKEditor());
        }
    }

    private String obterCKEditor() {
        StringBuilder ckEditor = new StringBuilder();
        ckEditor.append("CKEDITOR.disableAutoInline = true; var " + getMarkupId() 
                + " = CKEDITOR.inline('" + getMarkupId() + "'); ");
        if (!onKeyUpJS().isEmpty()) {
            ckEditor.append(" " + getMarkupId() + ".on('contentDom', function() { var editable_" 
                    + getMarkupId() + " = " + getMarkupId() + ".editable(); " + " editable_" + getMarkupId() 
                    + ".attachListener( editable_" + getMarkupId() + ", 'keyup', function() { " 
                    + onKeyUpJS() + " }); }); ");
            ckEditor.append(getMarkupId() + ".on('change', function() {" + onKeyUpJS() + "});");
        }
        return ckEditor.toString();
    }

    protected String onKeyUpJS() {
        return "";
    }

}
