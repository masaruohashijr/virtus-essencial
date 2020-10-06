package br.gov.bcb.sisaps.web.page.componentes.botoes;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.link.Link;

import br.gov.bcb.sisaps.integracao.seguranca.Perfis;

@AuthorizeAction(action = Action.RENDER, roles = {Perfis.RATING_TRANSACAO_SUPERVISOR})
public abstract class LinkAdmin<T> extends Link<T> {
    private static final long serialVersionUID = 1L;

    public LinkAdmin(String id) {
        super(id);
    }
}