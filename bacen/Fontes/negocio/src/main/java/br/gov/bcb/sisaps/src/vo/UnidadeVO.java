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

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;

public class UnidadeVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private String nome;
    private TipoUnidadeAtividadeEnum tipo;
    private MatrizVO matriz;



    public UnidadeVO() {
        super();
    }

    public UnidadeVO(Integer pk) {
        this.pk = pk;
    }

    @Override
    public Integer getPk() {
        return pk;
    }

    @Override
    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoUnidadeAtividadeEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoUnidadeAtividadeEnum tipo) {
        this.tipo = tipo;
    }


    public MatrizVO getMatriz() {
        return matriz;
    }

    public void setMatriz(MatrizVO matriz) {
        this.matriz = matriz;
    }


}
