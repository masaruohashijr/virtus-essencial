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
import java.util.List;

public class RelatorioSintesesMatrizVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nomeConglomerado;
    private String nomeUnidadeAtividade;
    private String rodape;

    private List<RelatorioSinteseVO> sinteses;

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

    public String getRodape() {
        return rodape;
    }

    public void setRodape(String rodape) {
        this.rodape = rodape;
    }

    public List<RelatorioSinteseVO> getSinteses() {
        return sinteses;
    }

    public void setSinteses(List<RelatorioSinteseVO> sinteses) {
        this.sinteses = sinteses;
    }

}