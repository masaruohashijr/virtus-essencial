package br.gov.bcb.sisaps.web.page.componentes.scroll;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.model.PropertyModel;

public class HiddenFieldScroll extends HiddenField<String> {

    public static final String EVENTO = "onclick";
    public static final String AJUSTA_SCROLL = "getScrollXY();";
    private String scroll;
    private final String campoNoFormulario;

    public HiddenFieldScroll(String id, String campoNoFormulario) {
        super(id);
        setOutputMarkupId(true);
        this.campoNoFormulario = campoNoFormulario;
        addComponentes();
    }

    private void addComponentes() {
        setModel(new PropertyModel<String>(this, "scroll"));
    }

    public boolean temScroll() {
        // só deve fazer o scroll automático caso não haja errors.
        return scroll != null && getSession().getFeedbackMessages().isEmpty();
    }

    public String getScroll() {
        return scroll;
    }

    public void setScroll(String scroll) {
        this.scroll = scroll;
    }

    public String getLoadScript() {
        int x = 0;
        int y = 0;
        if (temScroll()) {
            x = Integer.valueOf(scroll.substring(0, scroll.indexOf(',')));
            y = Integer.valueOf(scroll.substring(scroll.indexOf(',') + 1));
            return "window.scrollTo(" + x + "," + y + ");";
        } else {
            return "";
        }
    }

    public String getTimerScript() {
        StringBuffer script = new StringBuffer(700); //NOPMD

        script.append("function getScrollXY() {"); //NOPMD
        script.append("    var scrOfX = 0, scrOfY = 0;");
        script.append("    if( typeof( window.pageYOffset ) == 'number' ) {");
        script.append("        scrOfY = window.pageYOffset;");
        script.append("        scrOfX = window.pageXOffset;");
        script.append("    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {");
        script.append("        scrOfY = document.body.scrollTop;");
        script.append("        scrOfX = document.body.scrollLeft;");
        script.append("    } else if( document.documentElement && ");
        script.append("                 ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ");
        script.append("             ) {");
        script.append("        scrOfY = document.documentElement.scrollTop;");
        script.append("        scrOfX = document.documentElement.scrollLeft;");
        script.append("    }");
        script.append("    " + campoNoFormulario + ".value = scrOfX+\",\"+scrOfY;"); //NOPMD
        script.append('}');

        return script.toString();
    }

    public static void incluirAjusteScroll(ComponentTag tag) {
        String old = tag.getAttribute(EVENTO);
        tag.put(EVENTO, HiddenFieldScroll.AJUSTA_SCROLL + (old == null ? "" : old));
    }
}
