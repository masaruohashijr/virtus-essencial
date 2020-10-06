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

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PEL_PAR_ELEMENTO_RIS_CON", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroElemento.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PEL_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PEL_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PEL_NU_VERSAO", nullable = false))})
public class ParametroElemento extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PEL_ID";
    private String nome;
    private String descricao;
    private ParametroGrupoRiscoControle grupoRiscoControle;
    private String enderecoManual;
    private Short ordem;
    private Metodologia metodologia;
    private TipoGrupoEnum tipo;
    private List<ParametroItemElemento> itensElemento;

    @Column(name = "PEL_NM", nullable = false)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "PEL_DS", nullable = false)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroGrupoRiscoControle.CAMPO_ID, nullable = false)
    public ParametroGrupoRiscoControle getGrupoRiscoControle() {
        return grupoRiscoControle;
    }

    public void setGrupoRiscoControle(ParametroGrupoRiscoControle grupoRiscoControle) {
        this.grupoRiscoControle = grupoRiscoControle;
    }

    @Column(name = "PEL_DS_END_MANUAL", nullable = false)
    public String getEnderecoManual() {
        return enderecoManual;
    }

    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    @Column(name = "PEL_NU_ORDEM", nullable = false)
    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }
    
    @Column(name = "PEL_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = TipoGrupoEnum.CLASS_NAME)})
    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }

    @OneToMany(mappedBy = "parametroElemento", fetch = FetchType.LAZY)
    public List<ParametroItemElemento> getItensElemento() {
        return itensElemento;
    }

    public void setItensElemento(List<ParametroItemElemento> itensElemento) {
        this.itensElemento = itensElemento;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPk()).append(nome).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParametroElemento) {
            ParametroElemento parametro = (ParametroElemento) obj;
            return new EqualsBuilder().append(getPk(), parametro.getPk()).isEquals();
        }
        return false;
    }
}
