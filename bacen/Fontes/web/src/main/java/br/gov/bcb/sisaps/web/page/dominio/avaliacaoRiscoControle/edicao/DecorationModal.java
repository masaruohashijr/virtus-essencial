package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class DecorationModal extends PainelModalWindow {

    public DecorationModal(String id) {
        super(id);
        addOrReplace(new AjaxLazyLoadPanel("lazyLoad") {
            @Override
            public Component getLazyLoadComponent(String markupId) {
                Label label = new Label(markupId, "<br />Executando...");
                label.setEscapeModelStrings(false);
                return label;
            }
        });
    }

}
