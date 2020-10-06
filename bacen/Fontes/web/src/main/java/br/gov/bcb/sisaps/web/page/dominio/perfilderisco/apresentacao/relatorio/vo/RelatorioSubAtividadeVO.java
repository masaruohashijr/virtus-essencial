/*
RelatorioSlideTextoVO.java * Projeto: Rating
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

public class RelatorioSubAtividadeVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ano;
    private String dataBaseFormatada;
    private String descricao;
    private String situacao;

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getDataBaseFormatada() {
        return dataBaseFormatada;
    }

    public void setDataBaseFormatada(String dataBaseFormatada) {
        this.dataBaseFormatada = dataBaseFormatada;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

}