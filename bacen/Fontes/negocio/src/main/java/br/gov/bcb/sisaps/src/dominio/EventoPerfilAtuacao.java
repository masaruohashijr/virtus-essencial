package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(schema = "SUP", name = "EPA_EVENTOS_PERFIL_ATUACAO")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EventoPerfilAtuacao.CAMPO_ID))})
public class EventoPerfilAtuacao extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "EPA_ID";
    
    private EventoConsolidado eventoConsolidado;
    private PerfilAtuacaoConsolidado perfil;
    private String cnpjCodigoOrigemSrc;

    @JoinColumn(name = "EVC_ID", referencedColumnName = "EVC_ID", nullable = false)
    @OneToOne(targetEntity = EventoConsolidado.class, optional = false)
    public EventoConsolidado getEventoConsolidado() {
        return eventoConsolidado;
    }

    @JoinColumn(name = "PRT_ID", referencedColumnName = "PRT_ID", nullable = false)
    @ManyToOne(targetEntity = PerfilAtuacaoConsolidado.class, optional = false)
    public PerfilAtuacaoConsolidado getPerfil() {
        return perfil;
    }
    
    @Column(name = "EPA_CD_CNPJ_ORIGEM", length = 8)
    public String getCnpjCodigoOrigemSrc() {
        return cnpjCodigoOrigemSrc;
    }

    public void setCnpjCodigoOrigemSrc(String cnpjCodigoOrigemSrc) {
        this.cnpjCodigoOrigemSrc = cnpjCodigoOrigemSrc;
    }

    public void setEventoConsolidado(EventoConsolidado eventoConsolidado) {
        this.eventoConsolidado = eventoConsolidado;
    }

    public void setPerfil(PerfilAtuacaoConsolidado opiniao) {
        this.perfil = opiniao;
    }
    
}
