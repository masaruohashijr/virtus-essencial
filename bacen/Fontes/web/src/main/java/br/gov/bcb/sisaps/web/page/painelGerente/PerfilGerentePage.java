/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelGerente;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.GERENTE})
public class PerfilGerentePage extends DefaultPageMenu {

    private static final String MENU = "Painel do gerente";
    private PainelCiclosEquipeGerente painelCicloPanel;
    private final Form<?> form = new Form<Object>("formulario");

    public PerfilGerentePage() {
        painelCicloPanel = new PainelCiclosEquipeGerente("consultaCicloPanel");
        form.addOrReplace(painelCicloPanel);
        form.addOrReplace(new PainelPendenciasGerente("pendenciasGerente"));
        add(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return MENU;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0901";
    }
   
}
