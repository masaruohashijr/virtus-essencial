package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "PEA_PARAMETRO_EMAIL_APS", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroEmailAPS.CAMPO_ID))})
public class ParametroEmailAPS extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "PEA_ID";

    private String parametro;
    private String valor;

    @Column(name = "PEA_CD_ROTULO", length = 200, nullable = false)
    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    @Column(name = "PEA_DS_EMAIL", length = 200, nullable = false)
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
