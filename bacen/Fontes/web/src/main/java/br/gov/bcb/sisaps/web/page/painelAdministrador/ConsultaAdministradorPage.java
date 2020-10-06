/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelAdministrador;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.GestaoAgendaPage;
import br.gov.bcb.sisaps.web.page.dominio.gestaoes.GestaoES;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.ADMINISTRADOR})
public class ConsultaAdministradorPage extends DefaultPageMenu {

    private static final String PAINEL_DO_ADMINISTRADOR = "Painel do administrador";

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<?> form = new Form<Object>("formulario");
        form.add(new PainelConsultaPerfilDeRiscoAdministrador("painelConsultaPerfilDeRisco"));
        
        form.add(new PainelAlterarParametros("painelAlterarParametros"));
        form.add(new PainelAlterarLocalizacaoTemporaria("painelAlterarLocalizacao"));
        form.addOrReplace(new BttGestaoAgenda());
        form.addOrReplace(new BttGestaoES());
        add(form);
    }

    private class BttGestaoES extends AjaxSubmitLinkSisAps {

        public BttGestaoES() {
            super("bttGestaoES");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            getPaginaAtual().avancarParaNovaPagina(new GestaoES());
        }
    }
    
    private class BttGestaoAgenda extends AjaxSubmitLinkSisAps {

        public BttGestaoAgenda() {
            super("bttGestaoAgenda");
        }

        @Override
        public void executeSubmit(AjaxRequestTarget target) {
            getPaginaAtual().avancarParaNovaPagina(new GestaoAgendaPage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return PAINEL_DO_ADMINISTRADOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0501";
    }
}
