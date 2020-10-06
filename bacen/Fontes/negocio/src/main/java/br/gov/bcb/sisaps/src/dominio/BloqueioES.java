/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "BLE_BLOQUEIO_ES", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = BloqueioES.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "BLE_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "BLE_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "BLE_NU_VERSAO"))})
public class BloqueioES extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "BLE_ID";
    private String cnpj;
    private DateTime dataBloqueio;
    private String operadorBloqueio;
    private DateTime dataDesbloqueio;
    private String operadorDesbloqueio;

    @Basic(fetch = FetchType.EAGER)
    @Column(name = "BLE_CD_CNPJ")
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Column(name = "BLE_DH_BLOQUEIO")
    public DateTime getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(DateTime dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    @Column(name = "BLE_CD_OPER_BLOQUEIO")
    public String getOperadorBloqueio() {
        return operadorBloqueio;
    }

    public void setOperadorBloqueio(String operadorBloqueio) {
        this.operadorBloqueio = operadorBloqueio;
    }

    @Column(name = "BLE_DH_DESBLOQUEIO")
    public DateTime getDataDesbloqueio() {
        return dataDesbloqueio;
    }

    public void setDataDesbloqueio(DateTime dataDesbloqueio) {
        this.dataDesbloqueio = dataDesbloqueio;
    }

    @Column(name = "BLE_CD_OPER_DESBLOQUEIO")
    public String getOperadorDesbloqueio() {
        return operadorDesbloqueio;
    }

    public void setOperadorDesbloqueio(String operadorDesbloqueio) {
        this.operadorDesbloqueio = operadorDesbloqueio;
    }

}
