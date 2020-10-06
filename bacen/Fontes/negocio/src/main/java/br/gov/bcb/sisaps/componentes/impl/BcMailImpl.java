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