package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

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
@Table(name = "ECA_ETL_CONTA_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID, column = @Column(name = ContaAnaliseQuantitativaETL.CAMPO_ID))})
public class ContaAnaliseQuantitativaETL extends ObjetoPersistente<Integer> {
    public static final String CAMPO_ID = "ECA_ID";

    private ContaAnaliseQuantitativa contaAnaliseQuantitativa;
    private DataBaseES dataBaseES;
    private Integer valor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ForeignKey(name = "IRECACAQ")
    @JoinColumn(name = ContaAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public ContaAnaliseQuantitativa getContaAnaliseQuantitativa() {
        return contaAnaliseQuantitativa;
    }

    public void setContaAnaliseQuantitativa(ContaAnaliseQuantitativa contaAnaliseQuantitativa) {
        this.contaAnaliseQuantitativa = contaAnaliseQuantitativa;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ForeignKey(name = "IRECADBE")
    @JoinColumns({
            @JoinColumn(name = DataBaseES.CAMPO_ID, referencedColumnName = DataBaseES.CAMPO_ID, nullable = false),
            @JoinColumn(name = DataBaseES.CAMPO_DBE_CD_CNPJ, referencedColumnName = DataBaseES.CAMPO_DBE_CD_CNPJ, nullable = false)})
    public DataBaseES getDataBaseES() {
        return dataBaseES;
    }

    public void setDataBaseES(DataBaseES dataBaseES) {
        this.dataBaseES = dataBaseES;
    }

    @Column(name = "ECA_VL_CAQ")
    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

}
