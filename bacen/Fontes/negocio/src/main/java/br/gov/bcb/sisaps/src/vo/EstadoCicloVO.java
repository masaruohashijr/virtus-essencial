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

import javax.persistence.Transient;

import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.util.Util;

public class EstadoCicloVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private EstadoCicloEnum estado;
    private Date dataInicio;
    private Date dataTermino;
    private CicloVO ciclo;

    public EstadoCicloVO() {
        super();
    }

    public EstadoCicloVO(Integer pk, EstadoCicloEnum estado) {
        this.pk = pk;
        this.estado = estado;
    }

    @Override
    public Integer getPk() {
        return pk;
    }

    @Override
    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public EstadoCicloEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoCicloEnum estado) {
        this.estado = estado;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }
    
    @Transient
    public String getDataTerminoFormatada() {
        return Util.formatar(new LocalDate(dataTermino));
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public CicloVO getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloVO ciclo) {
        this.ciclo = ciclo;
    }
    
    
    

}
