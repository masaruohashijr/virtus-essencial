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
@Table(name = "NEQ_NOTAS_ELEMENTOS_QUALITATIVA", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasElementosQualitativa.CAMPO_ID))})
public class NotasElementosQualitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NEQ_ID";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private NotasArcsQualitativa notasArcsQualitativa;
    private String nomeElemento;
    private BigDecimal notaElementoQltFinalVigente;
    private BigDecimal notaElementoQltFinalInspetor;
    private BigDecimal notaElementoQltFinalSupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasArcsQualitativa.CAMPO_ID, nullable = false)
    public NotasArcsQualitativa getNotasArcsQualitativa() {
        return notasArcsQualitativa;
    }

    public void setNotasArcsQualitativa(NotasArcsQualitativa notasArcsQualitativa) {
        this.notasArcsQualitativa = notasArcsQualitativa;
    }

    @Column(name = "NEQ_NM_ELEMENTO", nullable = false)
    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    @Column(name = "NEQ_VL_NOTA_ELE_QLT_FINAL_VIG", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQltFinalVigente() {
        return notaElementoQltFinalVigente;
    }

    public void setNotaElementoQltFinalVigente(BigDecimal notaElementoQltFinalVigente) {
        this.notaElementoQltFinalVigente = notaElementoQltFinalVigente;
    }

    @Column(name = "NEQ_VL_NOTA_ELE_QLT_FINAL_INSP", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQltFinalInspetor() {
        return notaElementoQltFinalInspetor;
    }

    public void setNotaElementoQltFinalInspetor(BigDecimal notaElementoQltFinalInspetor) {
        this.notaElementoQltFinalInspetor = notaElementoQltFinalInspetor;
    }

    @Column(name = "NEQ_VL_NOTA_ELE_QLT_FINAL_SUPERV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaElementoQltFinalSupervisor() {
        return notaElementoQltFinalSupervisor;
    }

    public void setNotaElementoQltFinalSupervisor(BigDecimal notaElementoQltFinalSupervisor) {
        this.notaElementoQltFinalSupervisor = notaElementoQltFinalSupervisor;
    }

}
