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
@Table(name = "NAQ_NOTAS_ARCS_QUALITATIVA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasArcsQualitativa.CAMPO_ID) )})
public class NotasArcsQualitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NAQ_ID";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private NotasGeraisFinal notasGeraisFinal;
    private NotasRiscoControleQualitativa notasRiscoControleQualitativa;
    private String nomeUnidade;
    private String nomeAtividade;
    private String tipoArc;
    private BigDecimal notaArcFinalVigente;
    private BigDecimal notaArcCalculada;
    private BigDecimal notaArcAjustadaSuperv;
    private BigDecimal notaArcAjustadaCorec;
    private BigDecimal pesoAtividade;
    private String tendenciaFinalVigente;
    private String tendenciaSupervisor;
    private String tendenciaInspetor;
    private DateTime dataConclusaoArc;
    private DateTime dataPreenchidoArc;
    private DateTime dataAnaliseArc;
    private String operadorConclusaoArc;
    private String operadorPreenchidoArc;
    private String operadorAnaliseArc;
    private String estadoArc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasGeraisFinal.CAMPO_ID, nullable = false)
    public NotasGeraisFinal getNotasGeraisFinal() {
        return notasGeraisFinal;
    }

    public void setNotasGeraisFinal(NotasGeraisFinal notasGeraisFinal) {
        this.notasGeraisFinal = notasGeraisFinal;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasRiscoControleQualitativa.CAMPO_ID, nullable = false)
    public NotasRiscoControleQualitativa getNotasRiscoControleQualitativa() {
        return notasRiscoControleQualitativa;
    }

    public void setNotasRiscoControleQualitativa(NotasRiscoControleQualitativa notasRiscoControleQualitativa) {
        this.notasRiscoControleQualitativa = notasRiscoControleQualitativa;
    }

    @Column(name = "NAQ_NM_UNIDADE")
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    @Column(name = "NAQ_NM_ATIVIDADE")
    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }

    @Column(name = "NAQ_DS_TIPO_ARC", nullable = false)
    public String getTipoArc() {
        return tipoArc;
    }

    public void setTipoArc(String tipoArc) {
        this.tipoArc = tipoArc;
    }

    @Column(name = "NAQ_VL_NOTA_ARC_FINAL_VIG", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaArcFinalVigente() {
        return notaArcFinalVigente;
    }

    public void setNotaArcFinalVigente(BigDecimal notaArcFinalVigente) {
        this.notaArcFinalVigente = notaArcFinalVigente;
    }

    @Column(name = "NAQ_VL_NOTA_ARC_CALC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaArcCalculada() {
        return notaArcCalculada;
    }

    public void setNotaArcCalculada(BigDecimal notaArcCalculada) {
        this.notaArcCalculada = notaArcCalculada;
    }

    @Column(name = "NAQ_VL_NOTA_ARC_AJUSTADA_SUPERV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaArcAjustadaSuperv() {
        return notaArcAjustadaSuperv;
    }

    public void setNotaArcAjustadaSuperv(BigDecimal notaArcAjustadaSuperv) {
        this.notaArcAjustadaSuperv = notaArcAjustadaSuperv;
    }

    @Column(name = "NAQ_VL_NOTA_ARC_AJUSTADA_COREC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaArcAjustadaCorec() {
        return notaArcAjustadaCorec;
    }

    public void setNotaArcAjustadaCorec(BigDecimal notaArcAjustadaCorec) {
        this.notaArcAjustadaCorec = notaArcAjustadaCorec;
    }

    @Column(name = "NAQ_VL_PESO", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPesoAtividade() {
        return pesoAtividade;
    }

    public void setPesoAtividade(BigDecimal pesoAtividade) {
        this.pesoAtividade = pesoAtividade;
    }

    @Column(name = "NAQ_DS_TENDENCIA_FINAL_VIG")
    public String getTendenciaFinalVigente() {
        return tendenciaFinalVigente;
    }

    public void setTendenciaFinalVigente(String tendenciaFinalVigente) {
        this.tendenciaFinalVigente = tendenciaFinalVigente;
    }

    @Column(name = "NAQ_DS_TENDENCIA_SUPERVISOR")
    public String getTendenciaSupervisor() {
        return tendenciaSupervisor;
    }

    public void setTendenciaSupervisor(String tendenciaSupervisor) {
        this.tendenciaSupervisor = tendenciaSupervisor;
    }

    @Column(name = "NAQ_DS_TENDENCIA_INSPETOR")
    public String getTendenciaInspetor() {
        return tendenciaInspetor;
    }

    public void setTendenciaInspetor(String tendenciaInspetor) {
        this.tendenciaInspetor = tendenciaInspetor;
    }

    @Column(name = "NAQ_DH_CONCLUSAO_ARC")
    public DateTime getDataConclusaoArc() {
        return dataConclusaoArc;
    }

    public void setDataConclusaoArc(DateTime dataConclusaoArc) {
        this.dataConclusaoArc = dataConclusaoArc;
    }

    @Column(name = "NAQ_DH_PREENCHIDO_ARC")
    public DateTime getDataPreenchidoArc() {
        return dataPreenchidoArc;
    }

    public void setDataPreenchidoArc(DateTime dataPreenchidoArc) {
        this.dataPreenchidoArc = dataPreenchidoArc;
    }

    @Column(name = "NAQ_DH_ANALISE_ARC")
    public DateTime getDataAnaliseArc() {
        return dataAnaliseArc;
    }

    public void setDataAnaliseArc(DateTime dataAnaliseArc) {
        this.dataAnaliseArc = dataAnaliseArc;
    }

    @Column(name = "NAQ_CD_OPER_CONCLUSAO_ARC")
    public String getOperadorConclusaoArc() {
        return operadorConclusaoArc;
    }

    public void setOperadorConclusaoArc(String operadorConclusaoArc) {
        this.operadorConclusaoArc = operadorConclusaoArc;
    }

    @Column(name = "NAQ_CD_OPER_PREENCHIDO_ARC")
    public String getOperadorPreenchidoArc() {
        return operadorPreenchidoArc;
    }

    public void setOperadorPreenchidoArc(String operadorPreenchidoArc) {
        this.operadorPreenchidoArc = operadorPreenchidoArc;
    }

    @Column(name = "NAQ_CD_OPER_ANALISE_ARC")
    public String getOperadorAnaliseArc() {
        return operadorAnaliseArc;
    }

    public void setOperadorAnaliseArc(String operadorAnaliseArc) {
        this.operadorAnaliseArc = operadorAnaliseArc;
    }

    @Column(name = "NAQ_DS_ESTADO_ARC")
    public String getEstadoArc() {
        return estadoArc;
    }

    public void setEstadoArc(String estadoArc) {
        this.estadoArc = estadoArc;
    }

}
