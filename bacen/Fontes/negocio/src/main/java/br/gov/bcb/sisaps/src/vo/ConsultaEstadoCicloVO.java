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