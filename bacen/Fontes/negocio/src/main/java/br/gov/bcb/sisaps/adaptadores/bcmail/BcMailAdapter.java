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
package br.gov.bcb.sisaps.adaptadores.bcmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;

/**
 * Adapter para acesso ao componente LOCAL BcMail.
 */
@Component
public class BcMailAdapter {

    @Autowired
    private IBcMail bcMail;

    public static BcMailAdapter get() {
        return SpringUtils.get().getBean(BcMailAdapter.class);
    }

    public void enviarEmail(Email email) {
        bcMail.enviarEmail(email);
    }
}