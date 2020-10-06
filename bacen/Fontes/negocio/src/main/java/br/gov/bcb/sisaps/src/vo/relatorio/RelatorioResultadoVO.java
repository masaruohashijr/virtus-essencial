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

public class RelatorioResultadoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String periodo;
    private String titulo;
    private String valorP1;
    private String valorP2;
    private String valorP3;
    private String valorP4;
    
    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getValorP1() {
        return valorP1;
    }
    
    public void setValorP1(String valorP1) {
        this.valorP1 = valorP1;
    }
    
    public String getValorP2() {
        return valorP2;
    }
    
    public void setValorP2(String valorP2) {
        this.valorP2 = valorP2;
    }
    
    public String getValorP3() {
        return valorP3;
    }
    
    public void setValorP3(String valorP3) {
        this.valorP3 = valorP3;
    }
    
    public String getValorP4() {
        return valorP4;
    }
    
    public void setValorP4(String valorP4) {
        this.valorP4 = valorP4;
    }

}