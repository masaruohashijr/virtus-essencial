/*
 * Projeto: Rating
 * Rating
 * Arquivo: RelatorioRatingVO.java
 * RelatorioRatingVO
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.vo.relatorio;

import java.io.Serializable;

public class RelatorioIndiceVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricaoIndice;
    private String valor;
    private String valorAjustado;
    private Boolean exibirIndiceAjustado;

    public String getDescricaoIndice() {
        return descricaoIndice;
    }

    public void setDescricaoIndice(String descricaoIndice) {
        this.descricaoIndice = descricaoIndice;
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

    public Boolean getExibirIndiceAjustado() {
        return exibirIndiceAjustado;
    }

    public void setExibirIndiceAjustado(Boolean exibirIndiceAjustado) {
        this.exibirIndiceAjustado = exibirIndiceAjustado;
    }

}