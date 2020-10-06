/*
 * Sistema APS.
 * InfraException.java
 * Copyright (c) Banco Central do Brasil.
 * 
 * Este software � confidencial e propriedade do Banco Central do Brasil. N�o � permitida sua
 * distribui��o ou divulga��o do seu conte�do sem expressa autoriza��o do Banco Central. Este
 * arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.validacao;

public class InfraException extends RuntimeException {

    public InfraException(Throwable cause) {
        super(cause);
    }

    public InfraException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
    
}
