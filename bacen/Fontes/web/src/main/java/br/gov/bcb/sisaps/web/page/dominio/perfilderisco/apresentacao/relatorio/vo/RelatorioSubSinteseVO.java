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

public class RelatorioSubSinteseVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nome;
    private String valor;
    private String sintese;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getSintese() {
        return sintese;
    }

    public void setSintese(String sintese) {
        this.sintese = sintese;
    }

}