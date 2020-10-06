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

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Entity
@Table(name = "EET_ETL_ENS", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EntidadeSupervisionavelETL.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "EET_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "EET_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class EntidadeSupervisionavelETL extends ObjetoPersistenteAuditavel<Integer> {
    public static final String CAMPO_ID = "EET_ID";
    private String cnpj;
    private String nome;
    private String porte;
    private String segmento;
    private String localizacao;
    private Date dataInclusao;
    private ParametroPrioridade prioridade;
    private SimNaoEnum pertenceSrc;

    @Column(name = "EET_CD_CNPJ", nullable = false, length = EntidadeSupervisionavel.TAMANHO_MATRICULA_CNPJ)
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String conglomeradoOuCnpj) {
        this.cnpj = conglomeradoOuCnpj;
    }

    @Column(name = "EET_NM", nullable = false, length = EntidadeSupervisionavel.TAMANHO_NOME_ES)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "EET_DS_PORTE", nullable = false, length = EntidadeSupervisionavel.TAMANHO_PORTE)
    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    @Column(name = "EET_DS_SEGMENTO", nullable = false, length = EntidadeSupervisionavel.TAMANHO_SEGMENTO)
    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    @Column(name = "EET_DS_LOCALIZACAO", nullable = false, length = EntidadeSupervisionavel.TAMANHO_NOME_LOCALIZACAO)
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Column(name = "EET_DT_INCLUSAO", nullable = false, columnDefinition = "date")
    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    @ManyToOne
    @JoinColumn(name = ParametroPrioridade.CAMPO_ID)
    public ParametroPrioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(ParametroPrioridade prioridade) {
        this.prioridade = prioridade;
    }

    @Column(name = "EET_IB_PERTENCE_SRC", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPertenceSrc() {
        return pertenceSrc;
    }

    public void setPertenceSrc(SimNaoEnum pertenceSrc) {
        this.pertenceSrc = pertenceSrc;
    }

}
