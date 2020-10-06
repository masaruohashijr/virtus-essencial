/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;

@Embeddable
public class EstadoMatriz implements Serializable {

    public static final String PROP_ESTADO = "estado";
    private EstadoMatrizEnum estado;
    
    @Column
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = EstadoMatrizEnum.CLASS_NAME)})
    public EstadoMatrizEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoMatrizEnum estado) {
        this.estado = estado;
    }

}
