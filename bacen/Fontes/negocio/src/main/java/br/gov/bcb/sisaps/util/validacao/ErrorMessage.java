/*
 * Sistema APS.
 * 
 * Copyright (c) Banco Central do Brasil.
 * 
 * Este software é confidencial e propriedade do Banco Central do Brasil. Não é
 * permitida sua distribuição ou divulgação do seu conteúdo sem expressa
 * autorização do Banco Central. Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.validacao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ErrorMessage {

    private String message;
    private Map<String, String> variables;

    public ErrorMessage(String message, Map<String, String> variables) {
        super();
        this.message = message;
        if (variables != null) {
            this.variables = new HashMap<String, String>();
            this.variables.putAll(variables);
        }
    }

    public ErrorMessage(String message, String... parametros) {
        this.message = message;
        if (parametros != null) {
            this.variables = new HashMap<String, String>(parametros.length);

            for (int i = 0; i < parametros.length; i++) {
                variables.put(String.valueOf(i), parametros[i]);
            }
        }
    }

    public ErrorMessage(String message) {
        super();
        this.message = message;
        this.variables = new HashMap<String, String>();
    }

    public static ErrorMessage criarErrorMessage(String message, String... parametros) {
        return new ErrorMessage(message, parametros);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getVariables() {
        if (variables == null) {
            variables = new HashMap<String, String>();
        }
        return variables;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.toString()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean saoIguais = false;

        if (obj == this) {
            saoIguais = true;
        } else if (obj instanceof ErrorMessage) {
            ErrorMessage erro = (ErrorMessage) obj;
            saoIguais = new EqualsBuilder().append(this.toString(), erro.toString()).isEquals();
        }

        return saoIguais;
    }

}
