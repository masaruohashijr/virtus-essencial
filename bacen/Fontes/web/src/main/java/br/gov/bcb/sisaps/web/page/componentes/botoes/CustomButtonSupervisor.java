/*
 * Sistema SIGAS..
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.page.componentes.botoes;

import org.apache.wicket.authorization.Action;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public abstract class CustomButtonSupervisor extends CustomButton {

    public CustomButtonSupervisor(String id) {
        super(id);
        setMarkupId(id);
        setOutputMarkupId(true);
    }

}
