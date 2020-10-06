/*
 * Sistema APS.
 * ObjetoPersistenteAuditavelConcorrente.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
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
