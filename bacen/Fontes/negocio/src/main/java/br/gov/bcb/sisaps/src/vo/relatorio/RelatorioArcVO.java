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

public class RelatorioArcVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nomeConglomerado;
    private String nomeUnidadeAtividade;
    private String nomeArc;
    private String nomeChaveArc;
    private String notaCalculada;
    private String notaAjustada;
    private String justificativaNotaAjustada;
    private String tendencia;
    private String justificativatendencia;
    private String rodape;
    private Boolean anexoArc;

    private List<RelatorioElementoVO> elementos;

    public String getNomeConglomerado() {
        return nomeConglomerado;
    }

    public void setNomeConglomerado(String nomeConglomerado) {
        this.nomeConglomerado = nomeConglomerado;
    }

    public String getNomeUnidadeAtividade() {
        return nomeUnidadeAtividade;
    }

    public void setNomeUnidadeAtividade(String nomeUnidadeAtividade) {
        this.nomeUnidadeAtividade = nomeUnidadeAtividade;
    }

    public String getNomeArc() {
        return nomeArc;
    }

    public void setNomeArc(String nomeArc) {
        this.nomeArc = nomeArc;
    }

    public String getNotaCalculada() {
        return notaCalculada;
    }

    public void setNotaCalculada(String notaCalculada) {
        this.notaCalculada = notaCalculada;
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

    public String getTendencia() {
        return tendencia;
    }

    public void setTendencia(String tendencia) {
        this.tendencia = tendencia;
    }

    public String getJustificativatendencia() {
        return justificativatendencia;
    }

    public void setJustificativatendencia(String justificativatendencia) {
        this.justificativatendencia = justificativatendencia;
    }

    public List<RelatorioElementoVO> getElementos() {
        return elementos;
    }

    public void setElementos(List<RelatorioElementoVO> elementos) {
        this.elementos = elementos;
    }

    public String getRodape() {
        return rodape;
    }

    public void setRodape(String rodape) {
        this.rodape = rodape;
    }

    public Boolean getAnexoArc() {
        return anexoArc;
    }

    public void setAnexoArc(Boolean anexoArc) {
        this.anexoArc = anexoArc;
    }

    public String getNomeChaveArc() {
        return nomeChaveArc;
    }

    public void setNomeChaveArc(String nomeChaveArc) {
        this.nomeChaveArc = nomeChaveArc;
    }
    
    

}