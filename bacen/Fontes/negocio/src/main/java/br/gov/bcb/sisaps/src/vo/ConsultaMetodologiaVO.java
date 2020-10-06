/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo;

import java.util.Date;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMetodologiaEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class ConsultaMetodologiaVO extends Consulta<MetodologiaVO> {

    private String descricao;
    private String titulo;
    private EstadoMetodologiaEnum estado;
    private SimNaoEnum metodologiaDefault;
    private Date dataInclusao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public EstadoMetodologiaEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoMetodologiaEnum estado) {
        this.estado = estado;
    }

    public SimNaoEnum getMetodologiaDefault() {
        return metodologiaDefault;
    }

    public void setMetodologiaDefault(SimNaoEnum metodologiaDefault) {
        this.metodologiaDefault = metodologiaDefault;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

}