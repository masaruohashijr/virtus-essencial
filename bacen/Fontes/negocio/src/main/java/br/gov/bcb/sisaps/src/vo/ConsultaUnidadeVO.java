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

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class ConsultaUnidadeVO extends Consulta<UnidadeVO> {

    private String nome;
    private TipoUnidadeAtividadeEnum tipo;
    private Matriz matriz;
    private SimNaoEnum excluido;
    private BigDecimal notaCalculada;

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

    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    public SimNaoEnum getExcluido() {
        return excluido;
    }

    public void setExcluido(SimNaoEnum excluido) {
        this.excluido = excluido;
    }

    public BigDecimal getNotaCalculada() {
        return notaCalculada;
    }

    public void setNotaCalculada(BigDecimal notaCalculada) {
        this.notaCalculada = notaCalculada;
    }

}