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

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "UND_UNIDADE_MAT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Unidade.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "UND_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "UND_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "UND_NU_VERSAO", nullable = false))})
@Proxy
public class Unidade extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "UND_ID";
    public static final int TAMANHO_NOME = 40;
    private static final long serialVersionUID = 1L;

    private String nome;
    private Matriz matriz;
    private ParametroPeso parametroPeso;
    private TipoUnidadeAtividadeEnum tipoUnidade;
    private List<Atividade> atividades;
    private BigDecimal fatorRelevancia;

    @Column(name = "UND_NM", nullable = false, length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @ManyToOne
    @JoinColumn(name = "MAT_ID", nullable = false)
    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "PPS_ID")
    public ParametroPeso getParametroPeso() {
        return parametroPeso;
    }

    public void setParametroPeso(ParametroPeso parametroPeso) {
        this.parametroPeso = parametroPeso;
    }

    @Column(name = "UND_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = TipoUnidadeAtividadeEnum.CLASS_NAME)})
    public TipoUnidadeAtividadeEnum getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidadeAtividadeEnum tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }
    
    @Transient
    public void setFatorRelevancia(BigDecimal fatorRelevancia) {
        this.fatorRelevancia = fatorRelevancia;
    }

    @Transient
    public BigDecimal getFatorRelevancia() {
        return this.fatorRelevancia;
    }

}
