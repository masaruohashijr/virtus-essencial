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

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;

@Entity
@Table(name = "EOI_ETL_OUTRA_INFO_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID, column = @Column(name = OutraInformacaoAnaliseQuantitativaETL.CAMPO_ID))})
public class OutraInformacaoAnaliseQuantitativaETL extends ObjetoPersistente<Integer> {
    public static final String CAMPO_ID = "EOI_ID";

    private OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa;
    private DataBaseES dataBaseES;
    private BigDecimal valor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = OutraInformacaoAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public OutraInformacaoAnaliseQuantitativa getOutraInformacaoAnaliseQuantitativa() {
        return outraInformacaoAnaliseQuantitativa;
    }

    public void setOutraInformacaoAnaliseQuantitativa(
            OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa) {
        this.outraInformacaoAnaliseQuantitativa = outraInformacaoAnaliseQuantitativa;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "DBE_CD", referencedColumnName = DataBaseES.CAMPO_ID, nullable = false),
            @JoinColumn(name = "DBE_CD_CNPJ", referencedColumnName = DataBaseES.CAMPO_DBE_CD_CNPJ, nullable = false)})
    public DataBaseES getDataBaseES() {
        return dataBaseES;
    }

    public void setDataBaseES(DataBaseES dataBaseES) {
        this.dataBaseES = dataBaseES;
    }

    @Column(name = "EOI_VL_OIA", columnDefinition = "DECIMAL(7,2)")
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

}
