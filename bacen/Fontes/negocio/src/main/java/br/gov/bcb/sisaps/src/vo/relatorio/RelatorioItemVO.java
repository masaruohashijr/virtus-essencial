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

public class RelatorioItemVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nomeItem;
    private String avaliacaoInspetor;
    private Boolean anexos;
    private String nomeChave;

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getAvaliacaoInspetor() {
        return avaliacaoInspetor;
    }

    public void setAvaliacaoInspetor(String avaliacaoInspetor) {
        this.avaliacaoInspetor = avaliacaoInspetor;
    }

    public Boolean getAnexos() {
        return anexos;
    }

    public void setAnexos(Boolean anexos) {
        this.anexos = anexos;
    }

    public String getNomeChave() {
        return nomeChave;
    }

    public void setNomeChave(String nomeChave) {
        this.nomeChave = nomeChave;
    }
    
    
    

}