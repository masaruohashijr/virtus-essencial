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

public class RelatorioElementoVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nomeElemento;
    private String notaElemento;
    private String nomeAvaliacaoInspetorItem;
    private String nomeAnaliseSupervisor;
    private String justificativaAnaliseSupervisor;

    private List<RelatorioItemVO> itens;

    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    public String getNotaElemento() {
        return notaElemento;
    }

    public void setNotaElemento(String notaElemento) {
        this.notaElemento = notaElemento;
    }

    public List<RelatorioItemVO> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioItemVO> itens) {
        this.itens = itens;
    }

    public String getNomeAvaliacaoInspetorItem() {
        return nomeAvaliacaoInspetorItem;
    }

    public void setNomeAvaliacaoInspetorItem(String nomeAvaliacaoInspetorItem) {
        this.nomeAvaliacaoInspetorItem = nomeAvaliacaoInspetorItem;
    }

    public String getNomeAnaliseSupervisor() {
        return nomeAnaliseSupervisor;
    }

    public void setNomeAnaliseSupervisor(String analiseSupervisor) {
        this.nomeAnaliseSupervisor = analiseSupervisor;
    }

    public String getJustificativaAnaliseSupervisor() {
        return justificativaAnaliseSupervisor;
    }

    public void setJustificativaAnaliseSupervisor(String justificativaAnaliseSupervisor) {
        this.justificativaAnaliseSupervisor = justificativaAnaliseSupervisor;
    }

}