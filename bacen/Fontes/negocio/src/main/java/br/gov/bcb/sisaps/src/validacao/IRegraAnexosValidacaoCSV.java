/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

// Regra de valida��o de anexo.
public interface IRegraAnexosValidacaoCSV extends Serializable {

    public void validar(InputStream anexo, File file);

}
