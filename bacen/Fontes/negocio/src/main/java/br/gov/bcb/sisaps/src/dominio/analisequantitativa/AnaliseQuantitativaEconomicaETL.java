package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;

@Entity
@Table(name = "EAQ_ETL_ANALISE_QUANT_ECONO", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID,
        column = @Column(name = AnaliseQuantitativaEconomicaETL.CAMPO_ID))})
public class AnaliseQuantitativaEconomicaETL extends ObjetoPersistente<Integer> {
    public static final String CAMPO_ID = "EAQ_ID";
    private static final String DECIMAL_COLUMN_DEFINITION_5_2 = "decimal(5,2)";
    private static final String DECIMAL_COLUMN_DEFINITION_4_1 = "decimal(4,1)";
    private DataBaseES dataBaseES;
    private Integer lucro;
    private BigDecimal rspla;
    private Integer prNivelUm;
    private Integer capitalPrincipal;
    private Integer capitalComplementar;
    private Integer prNivelDois;
    private BigDecimal indiceBaseleia;
    private BigDecimal indiceBaseleiaAmplo;
    private BigDecimal indiceImobilizacao;
    private BigDecimal escoreFinalAutomatico;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ForeignKey(name = "IREAQDBE")
    @JoinColumns({
            @JoinColumn(name = DataBaseES.CAMPO_ID, referencedColumnName = DataBaseES.CAMPO_ID, nullable = false),
            @JoinColumn(name = DataBaseES.CAMPO_DBE_CD_CNPJ,
                    referencedColumnName = DataBaseES.CAMPO_DBE_CD_CNPJ,
                    nullable = false)})
    public DataBaseES getDataBaseES() {
        return dataBaseES;
    }

    public void setDataBaseES(DataBaseES dataBaseES) {
        this.dataBaseES = dataBaseES;
    }

    @Column(name = "EAQ_VL_LUCRO")
    public Integer getLucro() {
        return lucro;
    }

    public void setLucro(Integer lucro) {
        this.lucro = lucro;
    }

    @Column(name = "EAQ_VL_RSPLA", columnDefinition = DECIMAL_COLUMN_DEFINITION_4_1)
    public BigDecimal getRspla() {
        return rspla;
    }

    public void setRspla(BigDecimal rspla) {
        this.rspla = rspla;
    }

    @Column(name = "EAQ_VL_PR_NIVEL_1")
    public Integer getPrNivelUm() {
        return prNivelUm;
    }

    public void setPrNivelUm(Integer prNivelUm) {
        this.prNivelUm = prNivelUm;
    }

    @Column(name = "EAQ_VL_CAPITAL_PRINCIPAL")
    public Integer getCapitalPrincipal() {
        return capitalPrincipal;
    }

    public void setCapitalPrincipal(Integer capitalPrincipal) {
        this.capitalPrincipal = capitalPrincipal;
    }

    @Column(name = "EAQ_VL_CAPITAL_COMPLEMENTAR")
    public Integer getCapitalComplementar() {
        return capitalComplementar;
    }

    public void setCapitalComplementar(Integer capitalComplementar) {
        this.capitalComplementar = capitalComplementar;
    }

    @Column(name = "EAQ_VL_PR_NIVEL_2")
    public Integer getPrNivelDois() {
        return prNivelDois;
    }

    public void setPrNivelDois(Integer prNivelDois) {
        this.prNivelDois = prNivelDois;
    }

    @Column(name = "EAQ_VL_IND_BASILEIA", columnDefinition = DECIMAL_COLUMN_DEFINITION_5_2)
    public BigDecimal getIndiceBaseleia() {
        return indiceBaseleia;
    }

    public void setIndiceBaseleia(BigDecimal indiceBaseleia) {
        this.indiceBaseleia = indiceBaseleia;
    }

    @Column(name = "EAQ_VL_IND_BASILEIA_AMPL", columnDefinition = DECIMAL_COLUMN_DEFINITION_5_2)
    public BigDecimal getIndiceBaseleiaAmplo() {
        return indiceBaseleiaAmplo;
    }

    public void setIndiceBaseleiaAmplo(BigDecimal indiceBaseleiaAmplo) {
        this.indiceBaseleiaAmplo = indiceBaseleiaAmplo;
    }

    @Column(name = "EAQ_VL_IND_IMOBILIZACAO", columnDefinition = DECIMAL_COLUMN_DEFINITION_5_2)
    public BigDecimal getIndiceImobilizacao() {
        return indiceImobilizacao;
    }

    public void setIndiceImobilizacao(BigDecimal indiceImobilizacao) {
        this.indiceImobilizacao = indiceImobilizacao;
    }

    @Column(name = "EAQ_VL_ESCORE_FINAL_AUTO", columnDefinition = "decimal(2,1)")
    public BigDecimal getEscoreFinalAutomatico() {
        return escoreFinalAutomatico;
    }

    public void setEscoreFinalAutomatico(BigDecimal escoreFinalAutomatico) {
        this.escoreFinalAutomatico = escoreFinalAutomatico;
    }

}
