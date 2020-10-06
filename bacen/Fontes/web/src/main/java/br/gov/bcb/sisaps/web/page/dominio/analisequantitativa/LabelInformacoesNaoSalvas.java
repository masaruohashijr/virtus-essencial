package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public abstract class LabelInformacoesNaoSalvas extends Label {

    public LabelInformacoesNaoSalvas(String id) {
        super(id, new Model<String>("*Atenção informações não salvas."));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setMarkupId(getId());
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        setVisibilityAllowed(isVisibleAllowed());
    }

    public abstract boolean isVisibleAllowed();

}
