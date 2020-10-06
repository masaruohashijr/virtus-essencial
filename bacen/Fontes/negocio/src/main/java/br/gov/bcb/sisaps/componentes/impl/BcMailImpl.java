/*
 * Sistema: TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.componentes.impl;

import org.springframework.stereotype.Component;

import br.gov.bcb.sisaps.adaptadores.bcmail.IBcMail;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.util.EmailUtil;

@Component(IBcMail.NOME)
public class BcMailImpl implements IBcMail {
    @Override
    public void enviarEmail(Email email) {
        EmailUtil.enviarEmail(email.getRemetente(), email.getDestinatarios(), email.getAssunto(), email.getCorpo(),
                email.getAnexos());
    }
}