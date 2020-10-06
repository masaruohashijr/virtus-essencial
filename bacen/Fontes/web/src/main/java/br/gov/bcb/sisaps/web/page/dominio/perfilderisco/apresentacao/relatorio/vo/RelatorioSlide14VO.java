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
package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioSlide14VO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String notaCalculada;

    private String notaRefinada;

    private String notaAjustada;

    private List<RelatorioSubSinteseVO> sinteses;

    public List<RelatorioSubSinteseVO> getSinteses() {
        return sinteses;
    }

    public void setSinteses(List<RelatorioSubSinteseVO> sinteses) {
        this.sinteses = sinteses;
    }

    public String getNotaCalculada() {
        return notaCalculada;
    }

    public void setNotaCalculada(String notaCalculada) {
        this.notaCalculada = notaCalculada;
    }

    public String getNotaRefinada() {
        return notaRefinada;
    }

    public void setNotaRefinada(String notaRefinada) {
        this.notaRefinada = notaRefinada;
    }

    public String getNotaAjustada() {
        return notaAjustada;
    }

    public void setNotaAjustada(String notaAjustada) {
        this.notaAjustada = notaAjustada;
    }

}