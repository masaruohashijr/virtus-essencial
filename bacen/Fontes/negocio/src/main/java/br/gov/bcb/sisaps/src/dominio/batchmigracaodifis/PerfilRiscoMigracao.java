package br.gov.bcb.sisaps.src.dominio.batchmigracaodifis;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "PRM_PERFIL_RISCO_MIGRACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = PerfilRiscoMigracao.CAMPO_ID) )})
public class PerfilRiscoMigracao extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "PRM_ID";

    private NotasGeraisFinal notasGeraisFinal;
    private DateTime dataCriacao;
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasGeraisFinal.CAMPO_ID, nullable = false)
    public NotasGeraisFinal getNotasGeraisFinal() {
        return notasGeraisFinal;
    }

    public void setNotasGeraisFinal(NotasGeraisFinal notasGeraisFinal) {
        this.notasGeraisFinal = notasGeraisFinal;
    }

    @Column(name = "PRM_DH_CRIACAO", nullable = false)
    public DateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(DateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Column(name = "PRM_DS_TIPO", nullable = false)
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
