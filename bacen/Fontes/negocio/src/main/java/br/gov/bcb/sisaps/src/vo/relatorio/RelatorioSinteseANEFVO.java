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

public class RelatorioSinteseANEFVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String titulo;
    private String relevancia;
    private String nota;
    private String justificativaNota;
    private boolean condicao;
    
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getRelevancia() {
        return relevancia;
    }
    public void setRelevancia(String relevancia) {
        this.relevancia = relevancia;
    }
    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
    public String getJustificativaNota() {
        return justificativaNota;
    }
    public void setJustificativaNota(String justificativaNota) {
        this.justificativaNota = justificativaNota;
    }
    public boolean isCondicao() {
        return condicao;
    }
    public void setCondicao(boolean condicao) {
        this.condicao = condicao;
    }
    
    

}