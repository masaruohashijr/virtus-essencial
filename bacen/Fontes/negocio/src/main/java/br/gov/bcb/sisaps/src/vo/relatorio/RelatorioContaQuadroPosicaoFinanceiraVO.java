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

public class RelatorioContaQuadroPosicaoFinanceiraVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipoConta;
    private String descricaoConta;
    private String valor;
    private String valorAjustado;
    private String escore;
    private Boolean exibirValorAjustado;
    private Boolean isNegrito;
    private Integer sequencial;

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
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

    public String getEscore() {
        return escore;
    }

    public void setEscore(String escore) {
        this.escore = escore;
    }

    public Boolean getExibirValorAjustado() {
        return exibirValorAjustado;
    }

    public void setExibirValorAjustado(Boolean exibirValorAjustado) {
        this.exibirValorAjustado = exibirValorAjustado;
    }

    public Boolean getIsNegrito() {
        return isNegrito;
    }

    public void setIsNegrito(Boolean isNegrito) {
        this.isNegrito = isNegrito;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

}