/*
 * Sistema: TBC
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
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