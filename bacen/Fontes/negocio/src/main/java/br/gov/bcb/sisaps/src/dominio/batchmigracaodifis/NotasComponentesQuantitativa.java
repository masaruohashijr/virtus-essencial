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
@Table(name = "NCQ_NOTAS_COMPONENTES_QUANTITATIVA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotasComponentesQuantitativa.CAMPO_ID) )})
public class NotasComponentesQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "NCQ_ID";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private NotasGeraisFinal notasGeraisFinal;
    private String nomeComponente;
    private BigDecimal notaComponenteFinalVig;
    private BigDecimal notaComponenteCalculada;
    private BigDecimal notaComponenteAjustadaSuperv;
    private BigDecimal notaComponenteAjustadaCorec;
    private BigDecimal peso;
    private DateTime dataConclusao;
    private DateTime dataPreenchido;
    private DateTime dataAnalise;
    private String operadorConclusao;
    private String operadorPreenchido;
    private String operadorAnalise;
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = NotasGeraisFinal.CAMPO_ID, nullable = false)
    public NotasGeraisFinal getNotasGeraisFinal() {
        return notasGeraisFinal;
    }

    public void setNotasGeraisFinal(NotasGeraisFinal notasGeraisFinal) {
        this.notasGeraisFinal = notasGeraisFinal;
    }

    @Column(name = "NCQ_NM_COMPONENTE", nullable = false)
    public String getNomeComponente() {
        return nomeComponente;
    }

    public void setNomeComponente(String nomeComponente) {
        this.nomeComponente = nomeComponente;
    }

    @Column(name = "NCQ_VL_NOTA_COMP_FINAL_VIG", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaComponenteFinalVig() {
        return notaComponenteFinalVig;
    }

    public void setNotaComponenteFinalVig(BigDecimal notaComponenteFinalVig) {
        this.notaComponenteFinalVig = notaComponenteFinalVig;
    }

    @Column(name = "NCQ_VL_NOTA_COMP_CALCULADA", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaComponenteCalculada() {
        return notaComponenteCalculada;
    }

    public void setNotaComponenteCalculada(BigDecimal notaComponenteCalculada) {
        this.notaComponenteCalculada = notaComponenteCalculada;
    }

    @Column(name = "NCQ_VL_NOTA_COMP_AJUSTADA_SUPERV", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaComponenteAjustadaSuperv() {
        return notaComponenteAjustadaSuperv;
    }

    public void setNotaComponenteAjustadaSuperv(BigDecimal notaComponenteAjustadaSuperv) {
        this.notaComponenteAjustadaSuperv = notaComponenteAjustadaSuperv;
    }

    @Column(name = "NCQ_VL_NOTA_COMP_AJUSTADA_COREC", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getNotaComponenteAjustadaCorec() {
        return notaComponenteAjustadaCorec;
    }

    public void setNotaComponenteAjustadaCorec(BigDecimal notaComponenteAjustadaCorec) {
        this.notaComponenteAjustadaCorec = notaComponenteAjustadaCorec;
    }

    @Column(name = "NCQ_VL_PESO", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    @Column(name = "NCQ_DH_CONCLUSAO")
    public DateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(DateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    @Column(name = "NCQ_DH_PREENCHIDO")
    public DateTime getDataPreenchido() {
        return dataPreenchido;
    }

    public void setDataPreenchido(DateTime dataPreenchido) {
        this.dataPreenchido = dataPreenchido;
    }

    @Column(name = "NCQ_DH_ANALISE")
    public DateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(DateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    @Column(name = "NCQ_CD_OPER_CONCLUSAO")
    public String getOperadorConclusao() {
        return operadorConclusao;
    }

    public void setOperadorConclusao(String operadorConclusao) {
        this.operadorConclusao = operadorConclusao;
    }

    @Column(name = "NCQ_CD_OPER_PREENCHIDO")
    public String getOperadorPreenchido() {
        return operadorPreenchido;
    }

    public void setOperadorPreenchido(String operadorPreenchido) {
        this.operadorPreenchido = operadorPreenchido;
    }

    @Column(name = "NCQ_CD_OPER_ANALISE")
    public String getOperadorAnalise() {
        return operadorAnalise;
    }

    public void setOperadorAnalise(String operadorAnalise) {
        this.operadorAnalise = operadorAnalise;
    }

    @Column(name = "NCQ_DS_ESTADO")
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
