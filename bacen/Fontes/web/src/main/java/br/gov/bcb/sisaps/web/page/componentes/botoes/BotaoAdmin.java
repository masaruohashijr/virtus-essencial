package br.gov.bcb.sisaps.web.page.componentes.botoes;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Button;

import br.gov.bcb.sisaps.web.app.Papeis;

@AuthorizeAction(action = Action.RENDER, roles = {Papeis.ADMIN})
public final class BotaoAdmin extends Button {
    private static final long serialVersionUID = 1L;
    public BotaoAdmin(String id) {
        super(id);
    }
}