package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.sisaps.util.geral.DataUtil;

@Entity
@Table(name = "DBE_DATA_BASE_ENS", schema = "SUP")
public class DataBaseES implements Serializable {
    public static final String CAMPO_ID = "DBE_CD";
    public static final String CAMPO_DBE_CD_CNPJ = "DBE_CD_CNPJ";

    private Integer codigoDataBase;
    private String cnpjES;

    @Id
    @Column(name = CAMPO_ID, nullable = false, length = 6)
    public Integer getCodigoDataBase() {
        return codigoDataBase;
    }

    public void setCodigoDataBase(Integer codigoDataBase) {
        this.codigoDataBase = codigoDataBase;
    }

    @Id
    @Column(name = CAMPO_DBE_CD_CNPJ, nullable = false, length = 8, columnDefinition = "VARCHAR(8)")
    public String getCnpjES() {
        return cnpjES;
    }

    public void setCnpjES(String cnpjES) {
        this.cnpjES = cnpjES;
    }

    @Transient
    public String getDataBaseFormatada() {
        if (getCodigoDataBase() != null) {
            return DataUtil.formatoMesAno(String.valueOf(getCodigoDataBase()));
        }
        return "";
    }

}
