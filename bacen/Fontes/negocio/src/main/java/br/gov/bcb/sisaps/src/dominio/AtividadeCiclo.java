/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavel;

@Entity
@Table(name = "ATC_ATIVIDADE_CICLO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AtividadeCiclo.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ATC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ATC_DH_ATUALZ", nullable = false))})
public class AtividadeCiclo extends ObjetoVersionadorAuditavel {

    public static final String CAMPO_ID = "ATC_ID";

    private String cnpjES;
    private String codigo;
    private Date dataBase;
    private Short ano;
    private String descricao;
    private String situacao;
    
    @Column(name = "ATC_CD_CNPJ_ES", nullable = false, length = 8)
    public String getCnpjES() {
        return cnpjES;
    }
    
    public void setCnpjES(String cnpjES) {
        this.cnpjES = cnpjES;
    }
    
    @Column(name = "ATC_CD_ATIVIDADE", nullable = false, length = 10)
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    @Column(name = "ATC_DT_BASE", nullable = false, columnDefinition = "date")
    public Date getDataBase() {
        return dataBase;
    }
    
    public void setDataBase(Date dataBase) {
        this.dataBase = dataBase;
    }
    
    @Column(name = "ATC_AA", nullable = false, columnDefinition = "smallint")
    public Short getAno() {
        return ano;
    }
    
    public void setAno(Short ano) {
        this.ano = ano;
    }
    
    @Column(name = "ATC_DS", nullable = false, length = 200)
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    @Column(name = "ATC_CD_SITUACAO", nullable = false, length = 20)
    public String getSituacao() {
        return situacao;
    }
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = false)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }
    
    @Transient
    public String getNomeAtividadeCicloFormatado() {
        if (cnpjES != null && codigo != null) {
            return "CNPJ da ES " + cnpjES + " / Código " + codigo;
        }
        return "";
    }

}
