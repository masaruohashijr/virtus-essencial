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

public class RelatorioAnaliseQuantitativaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeConglomerado;
    private String rodape;
    private String dataBase;
    private List<RelatorioContaQuadroPosicaoFinanceiraVO> contasQuadroAtivo;
    private List<RelatorioContaQuadroPosicaoFinanceiraVO> contasQuadroPassivo;
    private List<RelatorioPatrimonioVO> patrimonios;
    private List<RelatorioResultadoQuadroPosicaoFinanceiraVO> resultados;
    private List<RelatorioResultadoVO> resultadosNovo;
    private List<RelatorioResultadoVO> periodos;
    private List<RelatorioIndiceVO> indices;
    private Boolean exibirAjustadoContasAtivo;
    private Boolean exibirAJustadoContasPassivo;
    private Boolean exibirAjustadoPatrimonios;
    private Boolean exibirAjustadoResultados;
    private Boolean exibirAjustadoIndices;

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

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public List<RelatorioContaQuadroPosicaoFinanceiraVO> getContasQuadroAtivo() {
        return contasQuadroAtivo;
    }

    public void setContasQuadroAtivo(List<RelatorioContaQuadroPosicaoFinanceiraVO> contasQuadroAtivo) {
        this.contasQuadroAtivo = contasQuadroAtivo;
    }

    public List<RelatorioContaQuadroPosicaoFinanceiraVO> getContasQuadroPassivo() {
        return contasQuadroPassivo;
    }

    public void setContasQuadroPassivo(List<RelatorioContaQuadroPosicaoFinanceiraVO> contasQuadroPassivo) {
        this.contasQuadroPassivo = contasQuadroPassivo;
    }

    public List<RelatorioPatrimonioVO> getPatrimonios() {
        return patrimonios;
    }

    public void setPatrimonios(List<RelatorioPatrimonioVO> patrimonios) {
        this.patrimonios = patrimonios;
    }

    public List<RelatorioResultadoQuadroPosicaoFinanceiraVO> getResultados() {
        return resultados;
    }

    public void setResultados(List<RelatorioResultadoQuadroPosicaoFinanceiraVO> resultados) {
        this.resultados = resultados;
    }

    public List<RelatorioIndiceVO> getIndices() {
        return indices;
    }

    public void setIndices(List<RelatorioIndiceVO> indices) {
        this.indices = indices;
    }

    public Boolean getExibirAjustadoContasAtivo() {
        return exibirAjustadoContasAtivo;
    }

    public void setExibirAjustadoContasAtivo(Boolean exibirAjustadoContasAtivo) {
        this.exibirAjustadoContasAtivo = exibirAjustadoContasAtivo;
    }

    public Boolean getExibirAJustadoContasPassivo() {
        return exibirAJustadoContasPassivo;
    }

    public void setExibirAJustadoContasPassivo(Boolean exibirAJustadoContasPassivo) {
        this.exibirAJustadoContasPassivo = exibirAJustadoContasPassivo;
    }

    public Boolean getExibirAjustadoPatrimonios() {
        return exibirAjustadoPatrimonios;
    }

    public void setExibirAjustadoPatrimonios(Boolean exibirAjustadoPatrimonios) {
        this.exibirAjustadoPatrimonios = exibirAjustadoPatrimonios;
    }

    public Boolean getExibirAjustadoResultados() {
        return exibirAjustadoResultados;
    }

    public void setExibirAjustadoResultados(Boolean exibirAjustadoResultados) {
        this.exibirAjustadoResultados = exibirAjustadoResultados;
    }

    public Boolean getExibirAjustadoIndices() {
        return exibirAjustadoIndices;
    }

    public void setExibirAjustadoIndices(Boolean exibirAjustadoIndices) {
        this.exibirAjustadoIndices = exibirAjustadoIndices;
    }

    public List<RelatorioResultadoVO> getResultadosNovo() {
        return resultadosNovo;
    }

    public void setResultadosNovo(List<RelatorioResultadoVO> dadosSubrelatorioResultadosNovo) {
        this.resultadosNovo = dadosSubrelatorioResultadosNovo;
    }

    public List<RelatorioResultadoVO> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<RelatorioResultadoVO> periodosResultados) {
        this.periodos = periodosResultados;
    }

}