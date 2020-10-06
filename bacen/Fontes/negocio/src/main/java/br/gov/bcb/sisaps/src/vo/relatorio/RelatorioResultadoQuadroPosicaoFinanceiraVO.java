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

public class RelatorioResultadoQuadroPosicaoFinanceiraVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String periodo;
    private String lucro;
    private String lucroAjustado;
    private String rspla;
    private String rsplaAjustado;
    private Integer periodoInteger;
    private Boolean exibirLucroAjustado;
    private Boolean exibirRsplaAjustado;

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getLucro() {
        return lucro;
    }

    public void setLucro(String lucro) {
        this.lucro = lucro;
    }

    public String getLucroAjustado() {
        return lucroAjustado;
    }

    public void setLucroAjustado(String lucroAjustado) {
        this.lucroAjustado = lucroAjustado;
    }

    public String getRspla() {
        return rspla;
    }

    public void setRspla(String rspla) {
        this.rspla = rspla;
    }

    public String getRsplaAjustado() {
        return rsplaAjustado;
    }

    public void setRsplaAjustado(String rsplaAjustado) {
        this.rsplaAjustado = rsplaAjustado;
    }

    public Integer getPeriodoInteger() {
        return periodoInteger;
    }

    public void setPeriodoInteger(Integer periodoInteger) {
        this.periodoInteger = periodoInteger;
    }

    public Boolean getExibirLucroAjustado() {
        return exibirLucroAjustado;
    }

    public void setExibirLucroAjustado(Boolean exibirLucroAjustado) {
        this.exibirLucroAjustado = exibirLucroAjustado;
    }

    public Boolean getExibirRsplaAjustado() {
        return exibirRsplaAjustado;
    }

    public void setExibirRsplaAjustado(Boolean exibirRsplaAjustado) {
        this.exibirRsplaAjustado = exibirRsplaAjustado;
    }

}