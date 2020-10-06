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

import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaEstadoCicloVO extends Consulta<EstadoCicloVO> {

    private EstadoCicloEnum estado;
    private LocalDate dataEfetivoInicio;
    private LocalDate dataEfetivoTermino;
    private CicloVO ciclo;

    public EstadoCicloEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoCicloEnum estado) {
        this.estado = estado;
    }

    public LocalDate getDataEfetivoInicio() {
        return dataEfetivoInicio;
    }

    public void setDataEfetivoInicio(LocalDate dataEfetivoInicio) {
        this.dataEfetivoInicio = dataEfetivoInicio;
    }

    public LocalDate getDataEfetivoTermino() {
        return dataEfetivoTermino;
    }

    public void setDataEfetivoTermino(LocalDate dataEfetivoTermino) {
        this.dataEfetivoTermino = dataEfetivoTermino;
    }

    public CicloVO getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloVO ciclo) {
        this.ciclo = ciclo;
    }

}