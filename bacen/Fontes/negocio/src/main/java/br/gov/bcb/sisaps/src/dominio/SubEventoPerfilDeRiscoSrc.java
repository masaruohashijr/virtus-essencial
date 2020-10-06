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

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;

@Entity
@Table(schema = "SUP", name = "SPR_SUBEVENTO_PERFIL_DE_RISCO")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = SubEventoPerfilDeRiscoSrc.CAMPO_ID))})
public class SubEventoPerfilDeRiscoSrc extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "SPR_ID";
    public static final String SUB_EVENTO = "subEvento";
    
    private EventoPerfilDeRiscoSrc eventoPerfilDeRiscoSrc;
    private TipoSubEventoPerfilRiscoSRC tipo;

    @JoinColumn(name = "EPR_ID", referencedColumnName = "EPR_ID", nullable = false)
    @ManyToOne(targetEntity = EventoPerfilDeRiscoSrc.class, optional = false)
    public EventoPerfilDeRiscoSrc getEventoPerfilDeRiscoSrc() {
        return eventoPerfilDeRiscoSrc;
    }

    public void setEventoPerfilDeRiscoSrc(EventoPerfilDeRiscoSrc eventoPerfilDeRiscoSrc) {
        this.eventoPerfilDeRiscoSrc = eventoPerfilDeRiscoSrc;
    }

    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {
            @Parameter(name = "enumClass", value = TipoSubEventoPerfilRiscoSRC.CLASS_NAME)})
    @Column(name = "SPR_CD_TIPO", nullable = false, length = 2, columnDefinition = "smallint")
    public TipoSubEventoPerfilRiscoSRC getTipo() {
        return tipo;
    }

    public void setTipo(TipoSubEventoPerfilRiscoSRC tipo) {
        this.tipo = tipo;
    }

    
}
