/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelComite;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.COMITE})
public class PerfilComitePage extends DefaultPageMenu {

    private final Form<?> form = new Form<Object>("formulario");

    public PerfilComitePage() {
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }

    private void addComponentes() {
        form.addOrReplace(new PainelCicloCorecEPosCorec("painelCiclosEstadoCorec", true));
        form.addOrReplace(new PainelCicloCorecEPosCorec("painelCiclosEstadoPosCorec", false));
        add(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Painel do Comit?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0601";
    }
}
