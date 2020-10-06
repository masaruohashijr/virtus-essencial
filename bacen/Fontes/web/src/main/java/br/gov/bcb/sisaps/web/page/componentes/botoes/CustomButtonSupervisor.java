/*
 * Sistema SIGAS..
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
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
