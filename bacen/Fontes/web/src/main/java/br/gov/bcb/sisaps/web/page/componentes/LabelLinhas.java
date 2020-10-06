package br.gov.bcb.sisaps.web.page.componentes;

import java.io.Serializable;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class LabelLinhas extends Label implements Serializable {

    public LabelLinhas(String id, IModel<?> model) {
        super(id, model);
        markupid(id);
    }

    public LabelLinhas(String id, Serializable label) {
        this(id, Model.of(label));
    }

    public LabelLinhas(String id, String label) {
        super(id, label);
        markupid(id);
    }

    public LabelLinhas(String id) {
        super(id);
        markupid(id);
    }

    private void markupid(String id) {
        setOutputMarkupId(true);
        setMarkupId(id);
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        super.onComponentTagBody(markupStream, openTag);
    }

}
