/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistente;

public class EquipeES extends ObjetoPersistente {

    public static final String CAMPO_ID = "EQE_ID";
    private static final long serialVersionUID = 1L;
    private String localizacao;

    @Override
    @Id
    @Column(name = CAMPO_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getPk() {
        return super.getPk();
    }

    @Column(name = "EQE_DS_LOCALIZACAO", nullable = false)
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Transient
    public String getUnidadeDaLocalizacao() {
        return localizacao.split(SisapsUtil.BARRA)[0];
    }
}
