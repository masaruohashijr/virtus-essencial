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

public class RelatorioSlide03VO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RelatorioSubAtividadeVO> atual;

    private List<RelatorioSubAtividadeVO> anterior;

    public List<RelatorioSubAtividadeVO> getAtual() {
        return atual;
    }

    public void setAtual(List<RelatorioSubAtividadeVO> atual) {
        this.atual = atual;
    }

    public List<RelatorioSubAtividadeVO> getAnterior() {
        return anterior;
    }

    public void setAnterior(List<RelatorioSubAtividadeVO> anterior) {
        this.anterior = anterior;
    }

}