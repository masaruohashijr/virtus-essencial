/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;

public class LinhaMatrizVO extends ObjetoPersistenteVO {

    private static final String FILHO = "&nbsp;&nbsp;&nbsp;";
    private static final long serialVersionUID = 1L;
    private Integer sequencial;
    private String nome;
    private TipoLinhaMatrizVOEnum tipo;
    private TipoUnidadeAtividadeEnum atividade;
    private boolean filho;
    private Integer pkMatriz;

    public LinhaMatrizVO() {
        super();
    }

    public LinhaMatrizVO(Integer pk) {
        this.pk = pk;
    }
 

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoLinhaMatrizVOEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoLinhaMatrizVOEnum tipo) {
        this.tipo = tipo;
    }

    public TipoUnidadeAtividadeEnum getAtividade() {
        return atividade;
    }

    public void setAtividade(TipoUnidadeAtividadeEnum atividade) {
        this.atividade = atividade;
    }
    
    public String getNomeFormatadoVigente() {
        StringBuffer nomeFormatado = new StringBuffer();
        if (filho) {
            nomeFormatado.append(FILHO);
        }
        nomeFormatado.append(nome);
        return nomeFormatado.toString();

    }
    
    public String getNomeFormatadoVigenteRelatorio() {
        StringBuffer nomeFormatado = new StringBuffer();
        if (filho) {
            nomeFormatado.append("    ");
        }
        nomeFormatado.append(nome);
        return nomeFormatado.toString();
    }

    public String grupoRisco() {
        return "";
    }

    public boolean isFilho() {
        return filho;
    }

    public void setFilho(boolean filho) {
        this.filho = filho;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public Integer getPkMatriz() {
        return pkMatriz;
    }

    public void setPkMatriz(Integer pkMatriz) {
        this.pkMatriz = pkMatriz;
    }
    
    

}
