package br.gov.bcb.sisaps.src.dominio.batchmigracaodifis;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "NOQ_NOTAS_ELEMENTOS_QUANTITATIVA", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasElementosQuantitativa.CAMPO_ID))})
public class NotasElementosQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NOQ_ID";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private NotasComponentesQuantitativa notasComponentesQuantitativa;
    private String nomeElemento;
    private BigDecimal notaElementoQntFinalVigente;
    private BigDecimal notaElementoQntFinalInspetor;
    private BigDecimal notaElementoQntFinalSupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasComponentesQuantitativa.CAMPO_ID, nullable = false)
    public NotasComponentesQuantitativa getNotasComponentesQuantitativa() {
        return notasComponentesQuantitativa;
    }

    public void setNotasComponentesQuantitativa(NotasComponentesQuantitativa notasComponentesQuantitativa) {
        this.notasComponentesQuantitativa = notasComponentesQuantitativa;
    }

    @Column(name = "NOQ_NM_ELEMENTO", nullable = false)
    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    @Column(name = "NOQ_VL_NOTA_ELE_QNT_FINAL_VIG", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQntFinalVigente() {
        return notaElementoQntFinalVigente;
    }

    public void setNotaElementoQntFinalVigente(BigDecimal notaElementoQntFinalVigente) {
        this.notaElementoQntFinalVigente = notaElementoQntFinalVigente;
    }

    @Column(name = "NOQ_VL_NOTA_ELE_QNT_FINAL_INSP", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQntFinalInspetor() {
        return notaElementoQntFinalInspetor;
    }

    public void setNotaElementoQntFinalInspetor(BigDecimal notaElementoQntFinalInspetor) {
        this.notaElementoQntFinalInspetor = notaElementoQntFinalInspetor;
    }

    @Column(name = "NOQ_VL_NOTA_ELE_QNT_FINAL_SUPERV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQntFinalSupervisor() {
        return notaElementoQntFinalSupervisor;
    }

    public void setNotaElementoQntFinalSupervisor(BigDecimal notaElementoQntFinalSupervisor) {
        this.notaElementoQntFinalSupervisor = notaElementoQntFinalSupervisor;
    }

}
