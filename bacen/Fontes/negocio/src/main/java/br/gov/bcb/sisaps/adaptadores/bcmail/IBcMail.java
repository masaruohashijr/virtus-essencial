/*
 * Sistema TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.adaptadores.bcmail;

import br.gov.bcb.sisaps.adaptadores.pessoa.Email;

public interface IBcMail {
	String NOME = "compBcMail";

    void enviarEmail(Email email);
}
