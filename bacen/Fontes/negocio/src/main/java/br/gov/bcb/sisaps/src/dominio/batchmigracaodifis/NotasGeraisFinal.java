package br.gov.bcb.sisaps.src.dominio.batchmigracaodifis;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "NGF_NOTAS_GERAIS_FINAL", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasGeraisFinal.CAMPO_ID))})
public class NotasGeraisFinal extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NGF_ID";
    private static final String DEFINICAO_DATE = "date";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private String cnpj;
    private String estadoCiclo;
    private String metodologia;
    private DateTime dataVersao;
    private Date dataInicioCiclo;
    private Date dataPrevisaoCorec;
    
    private String perspectivaFinalVig;
    private String perspectivaCorec;
    private String perspectivaSuperv;
    
    private String grauPreocFinalVig;
    private String grauPreocCorec;
    private String grauPreocSuperv;
    
    private BigDecimal notaMatrizCalc;
    private String notaQltFinalVig;
    private BigDecimal notaQltCalculada;
    private String notaQltRefinada;
    private String notaQltAjustadaCorec;
    private String notaQltAjustadaSuperv;
    
    private String notaQntFinalVigente;
    private BigDecimal notaQntCalculada;
    private String notaQntRefinada;
    private String notaQntAjustadaCorec;
    private String notaQntAjustadaSuperv;
    
    private BigDecimal pesoAnef;
    private BigDecimal pesoRiscoControle;
    private BigDecimal grauPreocFinalCalc;
    private String grauPreocFinalRef;
    private BigDecimal pesoMatriz;
    private BigDecimal pesoGovernanca;
    private BigDecimal notaFinalGovernanca;

    @Column(name = "NGF_CD_CNPJ", nullable = false)
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Column(name = "NGF_DS_ESTADO_CICLO", nullable = false)
    public String getEstadoCiclo() {
        return estadoCiclo;
    }

    public void setEstadoCiclo(String estadoCiclo) {
        this.estadoCiclo = estadoCiclo;
    }

    @Column(name = "NGF_DS_METODOLOGIA", nullable = false)
    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    @Column(name = "NGF_DH_VERSAO")
    public DateTime getDataVersao() {
        return dataVersao;
    }

    public void setDataVersao(DateTime dataVersao) {
        this.dataVersao = dataVersao;
    }

    @Column(name = "NGF_DT_INICIO_CICLO", columnDefinition = DEFINICAO_DATE)
    public Date getDataInicioCiclo() {
        return dataInicioCiclo;
    }

    public void setDataInicioCiclo(Date dataInicioCiclo) {
        this.dataInicioCiclo = dataInicioCiclo;
    }

    @Column(name = "NGF_DT_PREV_COREC", columnDefinition = DEFINICAO_DATE)
    public Date getDataPrevisaoCorec() {
        return dataPrevisaoCorec;
    }

    public void setDataPrevisaoCorec(Date dataPrevisaoCorec) {
        this.dataPrevisaoCorec = dataPrevisaoCorec;
    }

    @Column(name = "NGF_DS_GRAU_PREOC_FINAL_VIG")
    public String getGrauPreocFinalVig() {
        return grauPreocFinalVig;
    }

    public void setGrauPreocFinalVig(String grauPreocFinalVig) {
        this.grauPreocFinalVig = grauPreocFinalVig;
    }

    @Column(name = "NGF_DS_GRAU_PREOC_COREC")
    public String getGrauPreocCorec() {
        return grauPreocCorec;
    }

    public void setGrauPreocCorec(String grauPreocCorec) {
        this.grauPreocCorec = grauPreocCorec;
    }

    @Column(name = "NGF_DS_GRAU_PREOC_SUPERV")
    public String getGrauPreocSuperv() {
        return grauPreocSuperv;
    }

    public void setGrauPreocSuperv(String grauPreocSuperv) {
        this.grauPreocSuperv = grauPreocSuperv;
    }

    @Column(name = "NGF_DS_PERSP_FINAL_VIG")
    public String getPerspectivaFinalVig() {
        return perspectivaFinalVig;
    }

    public void setPerspectivaFinalVig(String perspectivaFinalVig) {
        this.perspectivaFinalVig = perspectivaFinalVig;
    }

    @Column(name = "NGF_DS_PERSP_COREC")
    public String getPerspectivaCorec() {
        return perspectivaCorec;
    }

    public void setPerspectivaCorec(String perspectivaCorec) {
        this.perspectivaCorec = perspectivaCorec;
    }

    @Column(name = "NGF_DS_PERSP_SUPERV")
    public String getPerspectivaSuperv() {
        return perspectivaSuperv;
    }

    public void setPerspectivaSuperv(String perspectivaSuperv) {
        this.perspectivaSuperv = perspectivaSuperv;
    }
    
    @Column(name = "NGF_VL_NOTA_MATRIZ_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaMatrizCalc() {
        return notaMatrizCalc;
    }

    public void setNotaMatrizCalc(BigDecimal notaMatrizCalc) {
        this.notaMatrizCalc = notaMatrizCalc;
    }

    @Column(name = "NGF_DS_NOTA_QLT_FINAL_VIG")
    public String getNotaQltFinalVig() {
        return notaQltFinalVig;
    }

    public void setNotaQltFinalVig(String notaQltFinalVig) {
        this.notaQltFinalVig = notaQltFinalVig;
    }

    @Column(name = "NGF_VL_NOTA_QLT_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaQltCalculada() {
        return notaQltCalculada;
    }

    public void setNotaQltCalculada(BigDecimal notaQltCalculada) {
        this.notaQltCalculada = notaQltCalculada;
    }

    @Column(name = "NGF_DS_NOTA_QLT_REFINADA")
    public String getNotaQltRefinada() {
        return notaQltRefinada;
    }

    public void setNotaQltRefinada(String notaQltRefinada) {
        this.notaQltRefinada = notaQltRefinada;
    }

    @Column(name = "NGF_DS_NOTA_QLT_AJUSTADA_COREC")
    public String getNotaQltAjustadaCorec() {
        return notaQltAjustadaCorec;
    }

    public void setNotaQltAjustadaCorec(String notaQltAjustadaCorec) {
        this.notaQltAjustadaCorec = notaQltAjustadaCorec;
    }

    @Column(name = "NGF_DS_NOTA_QLT_AJUSTADA_SUPERV")
    public String getNotaQltAjustadaSuperv() {
        return notaQltAjustadaSuperv;
    }

    public void setNotaQltAjustadaSuperv(String notaQltAjustadaSuperv) {
        this.notaQltAjustadaSuperv = notaQltAjustadaSuperv;
    }

    @Column(name = "NGF_DS_NOTA_QNT_FINAL_VIG")
    public String getNotaQntFinalVigente() {
        return notaQntFinalVigente;
    }

    public void setNotaQntFinalVigente(String notaQntFinalVigente) {
        this.notaQntFinalVigente = notaQntFinalVigente;
    }

    @Column(name = "NGF_VL_NOTA_QNT_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaQntCalculada() {
        return notaQntCalculada;
    }

    public void setNotaQntCalculada(BigDecimal notaQntCalculada) {
        this.notaQntCalculada = notaQntCalculada;
    }

    @Column(name = "NGF_DS_NOTA_QNT_REFINADA")
    public String getNotaQntRefinada() {
        return notaQntRefinada;
    }

    public void setNotaQntRefinada(String notaQntRefinada) {
        this.notaQntRefinada = notaQntRefinada;
    }

    @Column(name = "NGF_DS_NOTA_QNT_AJUSTADA_COREC")
    public String getNotaQntAjustadaCorec() {
        return notaQntAjustadaCorec;
    }

    public void setNotaQntAjustadaCorec(String notaQntAjustadaCorec) {
        this.notaQntAjustadaCorec = notaQntAjustadaCorec;
    }

    @Column(name = "NGF_DS_NOTA_QNT_AJUSTADA_SUPERV")
    public String getNotaQntAjustadaSuperv() {
        return notaQntAjustadaSuperv;
    }

    public void setNotaQntAjustadaSuperv(String notaQntAjustadaSuperv) {
        this.notaQntAjustadaSuperv = notaQntAjustadaSuperv;
    }

    @Column(name = "NGF_VL_PESO_ANEF", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPesoAnef() {
        return pesoAnef;
    }

    public void setPesoAnef(BigDecimal pesoAnef) {
        this.pesoAnef = pesoAnef;
    }

    @Column(name = "NGF_VL_PESO_RIS_CON", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPesoRiscoControle() {
        return pesoRiscoControle;
    }

    public void setPesoRiscoControle(BigDecimal pesoRiscoControle) {
        this.pesoRiscoControle = pesoRiscoControle;
    }

    @Column(name = "NGF_VL_GRAU_PREOC_FINAL_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getGrauPreocFinalCalc() {
        return grauPreocFinalCalc;
    }

    public void setGrauPreocFinalCalc(BigDecimal grauPreocFinalCalc) {
        this.grauPreocFinalCalc = grauPreocFinalCalc;
    }

    @Column(name = "NGF_DS_GRAU_PREOC_FINAL_REF")
    public String getGrauPreocFinalRef() {
        return grauPreocFinalRef;
    }

    public void setGrauPreocFinalRef(String grauPreocFinalRef) {
        this.grauPreocFinalRef = grauPreocFinalRef;
    }

    @Column(name = "NGF_VL_PESO_MATRIZ", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPesoMatriz() {
        return pesoMatriz;
    }

    public void setPesoMatriz(BigDecimal pesoMatriz) {
        this.pesoMatriz = pesoMatriz;
    }

    @Column(name = "NGF_VL_PESO_GOV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPesoGovernanca() {
        return pesoGovernanca;
    }

    public void setPesoGovernanca(BigDecimal pesoGovernanca) {
        this.pesoGovernanca = pesoGovernanca;
    }

    @Column(name = "NGF_VL_NOTA_FINAL_GOV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaFinalGovernanca() {
        return notaFinalGovernanca;
    }

    public void setNotaFinalGovernanca(BigDecimal notaFinalGovernanca) {
        this.notaFinalGovernanca = notaFinalGovernanca;
    }
    
    
    
}
