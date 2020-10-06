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

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "NRQ_NOTAS_RIS_CON_QUALIT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasRiscoControleQualitativa.CAMPO_ID) )})
public class NotasRiscoControleQualitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NRQ_ID";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private NotasGeraisFinal notasGeraisFinal;
    private String nomeRiscoControle;
    private BigDecimal notaResidualFinalVig;
    private String conceitoGeralFinalVig;
    private BigDecimal notaResidualCalc;
    private BigDecimal notaResidualRiscoCalc;
    private BigDecimal notaResidualControleCalc;
    private BigDecimal percentualRiscoControle;
    private DateTime dataSintese;
    private String operadorSintese;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasGeraisFinal.CAMPO_ID, nullable = false)
    public NotasGeraisFinal getNotasGeraisFinal() {
        return notasGeraisFinal;
    }

    public void setNotasGeraisFinal(NotasGeraisFinal notasGeraisFinal) {
        this.notasGeraisFinal = notasGeraisFinal;
    }

    @Column(name = "NRQ_NM_RIS_CON", nullable = false)
    public String getNomeRiscoControle() {
        return nomeRiscoControle;
    }

    public void setNomeRiscoControle(String nomeRiscoControle) {
        this.nomeRiscoControle = nomeRiscoControle;
    }

    @Column(name = "NRQ_VL_NOTA_RESIDUAL_FINAL_VIG", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaResidualFinalVig() {
        return notaResidualFinalVig;
    }

    public void setNotaResidualFinalVig(BigDecimal notaResidualFinalVig) {
        this.notaResidualFinalVig = notaResidualFinalVig;
    }

    @Column(name = "NRQ_DS_CONCEITO_GERAL_FINAL_VIG")
    public String getConceitoGeralFinalVig() {
        return conceitoGeralFinalVig;
    }

    public void setConceitoGeralFinalVig(String conceitoGeralFinalVig) {
        this.conceitoGeralFinalVig = conceitoGeralFinalVig;
    }

    @Column(name = "NRQ_VL_NOTA_RESIDUAL_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaResidualCalc() {
        return notaResidualCalc;
    }

    public void setNotaResidualCalc(BigDecimal notaResidualCalc) {
        this.notaResidualCalc = notaResidualCalc;
    }

    @Column(name = "NRQ_VL_NOTA_RESIDUAL_RISCO_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaResidualRiscoCalc() {
        return notaResidualRiscoCalc;
    }

    public void setNotaResidualRiscoCalc(BigDecimal notaResidualRiscoCalc) {
        this.notaResidualRiscoCalc = notaResidualRiscoCalc;
    }

    @Column(name = "NRQ_VL_NOTA_RESIDUAL_CONTR_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaResidualControleCalc() {
        return notaResidualControleCalc;
    }

    public void setNotaResidualControleCalc(BigDecimal notaResidualControleCalc) {
        this.notaResidualControleCalc = notaResidualControleCalc;
    }

    @Column(name = "NRQ_VL_PERCENTUAL_RISCO", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPercentualRiscoControle() {
        return percentualRiscoControle;
    }

    public void setPercentualRiscoControle(BigDecimal percentualRiscoControle) {
        this.percentualRiscoControle = percentualRiscoControle;
    }

    @Column(name = "NRQ_DH_SINTESE")
    public DateTime getDataSintese() {
        return dataSintese;
    }

    public void setDataSintese(DateTime dataSintese) {
        this.dataSintese = dataSintese;
    }

    @Column(name = "NRQ_CD_OPER_SINTESE")
    public String getOperadorSintese() {
        return operadorSintese;
    }

    public void setOperadorSintese(String operadorSintese) {
        this.operadorSintese = operadorSintese;
    }

}
