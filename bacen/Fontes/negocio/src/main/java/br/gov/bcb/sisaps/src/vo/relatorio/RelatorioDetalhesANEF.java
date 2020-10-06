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
import java.util.List;

public class RelatorioDetalhesANEF implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nomeConglomerado;
    private String notaCalculada;
    private String notaRefinada;
    private String notaAjustada;
    private boolean possuiCorec;
    private String justificativaNotaAjustada;
    private String rodape;
    

    private List<RelatorioSinteseANEFVO> sinteses;

    public String getNomeConglomerado() {
        return nomeConglomerado;
    }

    public void setNomeConglomerado(String nomeConglomerado) {
        this.nomeConglomerado = nomeConglomerado;
    }

    public String getRodape() {
        return rodape;
    }

    public void setRodape(String rodape) {
        this.rodape = rodape;
    }

    public List<RelatorioSinteseANEFVO> getSinteses() {
        return sinteses;
    }

    public void setSinteses(List<RelatorioSinteseANEFVO> sinteses) {
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

    public String getJustificativaNotaAjustada() {
        return justificativaNotaAjustada;
    }

    public void setJustificativaNotaAjustada(String justificativaNotaAjustada) {
        this.justificativaNotaAjustada = justificativaNotaAjustada;
    }

    public boolean isPossuiCorec() {
        return possuiCorec;
    }

    public void setPossuiCorec(boolean possuiCorec) {
        this.possuiCorec = possuiCorec;
    }

    
    
    
}