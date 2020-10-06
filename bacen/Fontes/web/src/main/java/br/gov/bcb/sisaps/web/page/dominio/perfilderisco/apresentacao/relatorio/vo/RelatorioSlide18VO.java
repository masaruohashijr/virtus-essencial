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
package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioSlide18VO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RelatorioSub18VO> listaNegocioAtual;

    private List<RelatorioSub18VO> listaCorporativoAtual;

    private List<RelatorioSub18VO> listaNegocioAnterior;

    private List<RelatorioSub18VO> listaCorporativoAnterior;

    private boolean possuiAnterior;

    public List<RelatorioSub18VO> getListaNegocioAtual() {
        return listaNegocioAtual;
    }

    public void setListaNegocioAtual(List<RelatorioSub18VO> listaNegocioAtual) {
        this.listaNegocioAtual = listaNegocioAtual;
    }

    public List<RelatorioSub18VO> getListaCorporativoAtual() {
        return listaCorporativoAtual;
    }

    public void setListaCorporativoAtual(List<RelatorioSub18VO> listaCorporativoAtual) {
        this.listaCorporativoAtual = listaCorporativoAtual;
    }

    public List<RelatorioSub18VO> getListaNegocioAnterior() {
        return listaNegocioAnterior;
    }

    public void setListaNegocioAnterior(List<RelatorioSub18VO> listaNegocioAnterior) {
        this.listaNegocioAnterior = listaNegocioAnterior;
    }

    public List<RelatorioSub18VO> getListaCorporativoAnterior() {
        return listaCorporativoAnterior;
    }

    public void setListaCorporativoAnterior(List<RelatorioSub18VO> listaCorporativoAnterior) {
        this.listaCorporativoAnterior = listaCorporativoAnterior;
    }

    public boolean isPossuiAnterior() {
        return possuiAnterior;
    }

    public void setPossuiAnterior(boolean possuiAnterior) {
        this.possuiAnterior = possuiAnterior;
    }

}