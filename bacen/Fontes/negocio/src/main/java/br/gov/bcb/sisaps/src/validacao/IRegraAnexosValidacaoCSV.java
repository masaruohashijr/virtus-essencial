/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

// Regra de validação de anexo.
public interface IRegraAnexosValidacaoCSV extends Serializable {

    public void validar(InputStream anexo, File file);

}
