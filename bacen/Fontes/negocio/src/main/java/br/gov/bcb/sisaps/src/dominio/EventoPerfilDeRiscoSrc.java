package br.gov.bcb.sisaps.src.dominio;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Entity
@Table(schema = "SUP", name = "EPR_EVENTO_PERFIL_DE_RISCO_SRC")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EventoPerfilDeRiscoSrc.CAMPO_ID))})
public class EventoPerfilDeRiscoSrc extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "EPR_ID";
    
    private EventoConsolidado eventoConsolidado;
    private PerfilRisco perfilRisco;
    private List<SubEventoPerfilDeRiscoSrc> subEventos;
    private SimNaoEnum bloqueado;
    
    @Column(name = "EPR_CD_BLOQUEADO", nullable = true, length = 1, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getBloqueado() {
        return bloqueado;
    }

    @JoinColumn(name = "EVC_ID", referencedColumnName = "EVC_ID", nullable = false)
    @OneToOne(targetEntity = EventoConsolidado.class, optional = false)
    public EventoConsolidado getEventoConsolidado() {
        return eventoConsolidado;
    }

    public void setEventoConsolidado(EventoConsolidado eventoConsolidado) {
        this.eventoConsolidado = eventoConsolidado;
    }

    @JoinColumn(name = "EPR_ID_PERFIL", referencedColumnName = "PER_ID", nullable = false)
    @ManyToOne(targetEntity = PerfilRisco.class, optional = false)
    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    @OneToMany(mappedBy = "eventoPerfilDeRiscoSrc", fetch = FetchType.LAZY)
    public List<SubEventoPerfilDeRiscoSrc> getSubEventos() {
        return subEventos;
    }

    public void setSubEventos(List<SubEventoPerfilDeRiscoSrc> subEventos) {
        this.subEventos = subEventos;
    }
    
    public void setBloqueado(SimNaoEnum bloqueado) {
        this.bloqueado = bloqueado;
    }

}
