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



public class ParametroGrupoRiscoControleVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String nomeAbreviado;
    private String descricao;
    private String enderecoManual;
    private Short ordem;
    
    public ParametroGrupoRiscoControleVO() {
        super();
    }
    
    public ParametroGrupoRiscoControleVO(String nome, String nomeAbreviado, String descricao, 
            String enderecoManual, Short ordem) {
        this.nome = nome;
        this.nomeAbreviado = nomeAbreviado;
        this.descricao = descricao;
        this.enderecoManual = enderecoManual;
        this.ordem = ordem;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getNomeAbreviado() {
        return nomeAbreviado;
    }
    
    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getEnderecoManual() {
        return enderecoManual;
    }
    
    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }
    
    

}
