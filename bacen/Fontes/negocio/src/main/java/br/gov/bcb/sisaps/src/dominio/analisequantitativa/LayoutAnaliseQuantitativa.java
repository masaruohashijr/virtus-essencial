package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;

@Entity
@Table(name = "LAQ_LAYOUT_ANALISE_QUANTITATIVA", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID,
        column = @Column(name = LayoutAnaliseQuantitativa.CAMPO_ID))})
public class LayoutAnaliseQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "LAQ_ID";
    private Integer codigoDataBaseInicio;
    private Integer codigoDataBaseFim;

    @Column(name = "LAQ_CD_DATABE_INI", nullable = false)
    public Integer getCodigoDataBaseInicio() {
        return codigoDataBaseInicio;
    }

    public void setCodigoDataBaseInicio(Integer codigoDataBaseInicio) {
        this.codigoDataBaseInicio = codigoDataBaseInicio;
    }

    @Column(name = "LAQ_CD_DATABE_FIM")
    public Integer getCodigoDataBaseFim() {
        return codigoDataBaseFim;
    }

    public void setCodigoDataBaseFim(Integer codigoDataBaseFim) {
        this.codigoDataBaseFim = codigoDataBaseFim;
    }

}
