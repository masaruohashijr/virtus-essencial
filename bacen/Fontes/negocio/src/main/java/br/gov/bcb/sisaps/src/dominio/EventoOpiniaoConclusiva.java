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
@Table(schema = "SUP", name = "EOC_EVENTOS_OPINIAO_CONCLUSIVA")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EventoOpiniaoConclusiva.CAMPO_ID))})
public class EventoOpiniaoConclusiva extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "EOC_ID";
    
    private EventoConsolidado eventoConsolidado;
    private OpiniaoConclusivaConsolidado opiniao;
    private String cnpjCodigoOrigemSrc;

    @JoinColumn(name = "EVC_ID", referencedColumnName = "EVC_ID", nullable = false)
    @OneToOne(targetEntity = EventoConsolidado.class, optional = false)
    public EventoConsolidado getEventoConsolidado() {
        return eventoConsolidado;
    }

    @JoinColumn(name = "OCC_ID", referencedColumnName = "OCC_ID", nullable = false)
    @ManyToOne(targetEntity = OpiniaoConclusivaConsolidado.class, optional = false)
    public OpiniaoConclusivaConsolidado getOpiniao() {
        return opiniao;
    }
    
    @Column(name = "EOC_CD_CNPJ_ORIGEM", length = 8)
    public String getCnpjCodigoOrigemSrc() {
        return cnpjCodigoOrigemSrc;
    }

    public void setCnpjCodigoOrigemSrc(String cnpjCodigoOrigemSrc) {
        this.cnpjCodigoOrigemSrc = cnpjCodigoOrigemSrc;
    }

    public void setEventoConsolidado(EventoConsolidado eventoConsolidado) {
        this.eventoConsolidado = eventoConsolidado;
    }

    public void setOpiniao(OpiniaoConclusivaConsolidado opiniao) {
        this.opiniao = opiniao;
    }
    
}
