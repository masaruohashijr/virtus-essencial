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

public class RelatorioSlideTextoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<RelatorioSubSlideTextoVO> texto;

    public List<RelatorioSubSlideTextoVO> getTexto() {
        return texto;
    }

    public void setTexto(List<RelatorioSubSlideTextoVO> texto) {
        this.texto = texto;
    }

}