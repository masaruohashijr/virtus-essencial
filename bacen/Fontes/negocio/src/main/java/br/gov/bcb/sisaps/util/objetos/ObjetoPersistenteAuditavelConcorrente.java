/*
 * Sistema APS.
 * ObjetoPersistenteAuditavelConcorrente.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.objetos;

import javax.persistence.Version;

//@MappedSuperclass
public abstract class ObjetoPersistenteAuditavelConcorrente extends ObjetoPersistenteAuditavel {

    public static final String PROP_VERSAO = "versao";

    private static final long serialVersionUID = 1L;

    private Integer versao = 0;

    @Version
    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

}
