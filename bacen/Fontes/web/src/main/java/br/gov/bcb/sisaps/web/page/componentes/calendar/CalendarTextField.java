/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.page.componentes.calendar;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;

import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraDeDataBehaviour;

@SuppressWarnings("serial")
public class CalendarTextField<T> extends TextField<T> {
    private final SimNaoEnum removerImg;
    private String parentId;

    public CalendarTextField(String id, IModel<T> model) {
        this(id, model, SimNaoEnum.NAO);
    }

    public CalendarTextField(String id, IModel<T> model, SimNaoEnum removerImg) {
        super(id, model);
        this.removerImg = removerImg;
        add(new MascaraDeDataBehaviour());
        setMarkupId(getMarkupId());
        setOutputMarkupId(true);
    }

    @Override
    protected void onRender() {
        super.onRender();
        String id = getMarkupId();

        Response response = getResponse();
        if (SimNaoEnum.SIM.equals(removerImg)) {
            JavaScriptUtils.writeJavaScript(response, "$( \"#" + id
                    + "-button\").remove();");
        }
        response.write("\n<img ");
        response.write("  src=\"/img/calen_azul.gif\" ");
        response.write("  id=\"" + id + "-button\" ");
        response.write("  class=\"calendario-trigger\" ");
        response.write("  style=\"cursor: pointer;\" ");
        response.write("  title='Visualizar calendário'");
        response.write("/>");
        
        String script = null;
        script = "\nCalendar.registerDateCalendar(\"" + id + "\",\""
                + id + "-button\",\"" + getParentId() + "\");";
        JavaScriptUtils.writeJavaScript(response, script);

    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
