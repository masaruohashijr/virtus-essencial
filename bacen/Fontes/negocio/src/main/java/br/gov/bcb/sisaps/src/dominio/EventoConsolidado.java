package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.TipoEventoConsolidadoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;


@Entity
@Table(schema = "SUP", name = "EVC_EVENTO_CONSOLIDADO")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EventoConsolidado.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "EVC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "EVC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class EventoConsolidado extends ObjetoPersistenteAuditavelSisAps<Integer> {

    public static final String CAMPO_ID = "EVC_ID";
    
    private Consolidado consolidado;
    private TipoEventoConsolidadoEnum tipo;
    private String departamento;

    @JoinColumn(name = "CON_ID", referencedColumnName = "CON_ID", nullable = false)
    @ManyToOne(targetEntity = Consolidado.class, optional = false)
    public Consolidado getConsolidado() {
        return consolidado;
    }

    @Column(name = "EVC_CD_TIPO", nullable = false, length = 2, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = TipoEventoConsolidadoEnum.CLASS_NAME)})
    public TipoEventoConsolidadoEnum getTipo() {
        return tipo;
    }

    @Column(name = "EVC_DS_DEPARTAMENTO", length = 10, nullable = false)
    public String getDepartamento() {
        return departamento;
    }

    public void setConsolidado(Consolidado consolidado) {
        this.consolidado = consolidado;
    }

    public void setTipo(TipoEventoConsolidadoEnum tipo) {
        this.tipo = tipo;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    
    
}
