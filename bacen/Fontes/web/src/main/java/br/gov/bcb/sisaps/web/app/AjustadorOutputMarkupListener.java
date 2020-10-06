package br.gov.bcb.sisaps.web.app;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;

public class AjustadorOutputMarkupListener implements IComponentOnBeforeRenderListener {
    @Override
    public void onBeforeRender(Component component) {
        component.setOutputMarkupId(true);
    }
}