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

public class RelatorioAnefVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nomeConglomerado;
    private String nomeUnidadeAtividade;
    private String nomeAnef;
    private String nomeChaveAnef;
    private String notaCalculada;
    private String notaAjustada;
    private String justificativaNotaAjustada;
    private String rodape;
    private Boolean anexoAnef;

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

    public String getNomeAnef() {
        return nomeAnef;
    }

    public void setNomeAnef(String nomeArc) {
        this.nomeAnef = nomeArc;
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

    public Boolean getAnexoAnef() {
        return anexoAnef;
    }

    public void setAnexoAnef(Boolean anexoArc) {
        this.anexoAnef = anexoArc;
    }

    public String getNomeChaveAnef() {
        return nomeChaveAnef;
    }

    public void setNomeChaveAnef(String nomeChaveArc) {
        this.nomeChaveAnef = nomeChaveArc;
    }

}