/*
 * Sistema APS.
 * InfraException.java
 * Copyright (c) Banco Central do Brasil.
 * 
 * Este software é confidencial e propriedade do Banco Central do Brasil. Não é permitida sua
 * distribuição ou divulgação do seu conteúdo sem expressa autorização do Banco Central. Este
 * arquivo contém informações proprietárias.
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
