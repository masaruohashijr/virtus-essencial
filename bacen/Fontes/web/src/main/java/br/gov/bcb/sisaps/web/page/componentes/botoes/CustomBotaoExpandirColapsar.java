package br.gov.bcb.sisaps.web.page.componentes.botoes;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/***
 * 
 * @author deinf.weslen
 * 
 */

@SuppressWarnings("serial")
public abstract class CustomBotaoExpandirColapsar extends Panel implements Serializable {

    public CustomBotaoExpandirColapsar(String id) {
        super(id);
        addBotoes();
    }

    private void addBotoes() {
        BotaoColapsar botaoColapsar = new BotaoColapsar("colapsar");
        botaoColapsar.setMarkupId(botaoColapsar.getId());
        botaoColapsar.setOutputMarkupId(true);
        addOrReplace(botaoColapsar);

        BotaoExpandir botaoExpandir = new BotaoExpandir("expandir");
        botaoExpandir.setMarkupId(botaoExpandir.getId());
        botaoExpandir.setOutputMarkupId(true);
        addOrReplace(botaoExpandir);
    }

    private class BotaoColapsar extends AjaxButton {
        public BotaoColapsar(String id) {
            super(id);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            expandirColapsarGrupos(target);
        }

    }

    private class BotaoExpandir extends AjaxButton {
        public BotaoExpandir(String id) {
            super(id);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            expandirColapsarGrupos(target);
        }
    }

    public abstract void expandirColapsarGrupos(AjaxRequestTarget target);

}
