/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.util.geral;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;


public class ValidacaoException extends NegocioException {

    public ValidacaoException(ErrorMessage message) {
        super(message, null);
    }    
    
}