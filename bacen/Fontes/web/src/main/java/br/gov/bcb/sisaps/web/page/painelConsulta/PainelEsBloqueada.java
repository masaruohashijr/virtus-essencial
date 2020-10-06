package br.gov.bcb.sisaps.web.page.painelConsulta;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.web.page.componentes.modal.PainelModalWindow;

public class PainelEsBloqueada extends PainelModalWindow {

    private Form<?> form = new Form<Object>("form");
    private String msgBloqueado;

    public PainelEsBloqueada(ModalWindow modalWindow, String msgBloqueado) {
        super(modalWindow.getContentId());
        this.msgBloqueado = msgBloqueado;
        this.modalEdicao = modalWindow;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        form.addOrReplace(new Label("msgBloqueado", msgBloqueado));
        addBotaoFechar();
        add(form);
    }

    private void addBotaoFechar() {
        LinkFechar linkFechar = new LinkFechar();
        linkFechar.setOutputMarkupId(true);
        linkFechar.setMarkupId(linkFechar.getId());
        form.addOrReplace(linkFechar);
    }

    protected class LinkFechar extends AjaxLink<Void> {
        public LinkFechar() {
            super("bttVoltarModal");
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            modalEdicao.close(target);
        }

    }

}