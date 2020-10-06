/*
 * Projeto: Rating
 * Rating
 * Arquivo: RelatorioRatingVO.java
 * RelatorioRatingVO
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo.relatorio;

import java.io.Serializable;

public class RelatorioPatrimonioVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricaoPatrimonio;
    private String valor;
    private String valorAjustado;

    public String getDescricaoPatrimonio() {
        return descricaoPatrimonio;
    }

    public void setDescricaoPatrimonio(String descricaoPatrimonio) {
        this.descricaoPatrimonio = descricaoPatrimonio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorAjustado() {
        return valorAjustado;
    }

    public void setValorAjustado(String valorAjustado) {
        this.valorAjustado = valorAjustado;
    }

}