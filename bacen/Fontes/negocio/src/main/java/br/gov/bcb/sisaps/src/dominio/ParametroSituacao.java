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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelConcorrente;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PST_PAR_SITUACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroSituacao.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PST_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PST_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelConcorrente.PROP_VERSAO, column = @Column(name = "PST_NU_VERSAO", nullable = false))})
public class ParametroSituacao extends ObjetoPersistenteAuditavelVersionado<Integer> { 
    public static final String CAMPO_ID = "PST_ID";
    public static final int TAMANHO_NOME = 40;
    public static final int TAMANHO_DESCRICAO = 200;
    private String nome;
    private String descricao;
    private Metodologia metodologia;
    private NormalidadeEnum normalidade;

    @Column(name = "PST_DS", nullable = false, length = TAMANHO_DESCRICAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "PST_NM", nullable = false, length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    @Column(name = "PST_IB_NORMALIDADE", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = NormalidadeEnum.CLASS_NAME)})
    public NormalidadeEnum getNormalidade() {
        return normalidade;
    }

    public void setNormalidade(NormalidadeEnum normalidade) {
        this.normalidade = normalidade;
    }
    
}
