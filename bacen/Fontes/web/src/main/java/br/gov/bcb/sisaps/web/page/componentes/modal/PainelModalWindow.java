package br.gov.bcb.sisaps.web.page.componentes.modal;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.wicket.comp.BacenFeedbackPanel;

public class PainelModalWindow extends PainelSisAps {

    protected static final String FEEDBACKMODALWINDOW = "feedbackmodalwindow";

    public PainelModalWindow(String id) {
        super(id);
        FeedbackPanel feedBackPanel = new BacenFeedbackPanel(FEEDBACKMODALWINDOW);
        feedBackPanel.setOutputMarkupId(true);
        add(feedBackPanel);
    }

    public FeedbackPanel getFeedbackPanel() {
        return (FeedbackPanel) get(FEEDBACKMODALWINDOW);
    }

}
