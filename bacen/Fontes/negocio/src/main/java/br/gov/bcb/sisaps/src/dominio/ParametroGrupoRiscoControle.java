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
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PRC_PAR_GRP_RIS_CON", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroGrupoRiscoControle.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PRC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PRC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PRC_NU_VERSAO", nullable = false))})
public class ParametroGrupoRiscoControle extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PRC_ID";
    private static final int TAMANHO_ENDERECO_MANUAL = 100;
    private static final int TAMANHO_DESCRICAO = 255;
    private static final int TAMANHO_NOME_ABREVIADO = 50;
    private static final int TAMANHO_NOME = 40;
    private String nomeRisco;
    private String nomeControle;
    private String nomeAbreviado;
    private String descricao;
    private String enderecoManual;
    private SimNaoEnum sinteseObrigatoria;
    private Short ordem;
    private Metodologia metodologia;
    private List<ParametroElemento> parametrosElemento;
    private TipoParametroGrupoRiscoControleEnum tipoGrupo;

    @Column(name = "PRC_NM_RISCO", nullable = false, length = TAMANHO_NOME)
    public String getNomeRisco() {
        return nomeRisco;
    }

    public void setNomeRisco(String nome) {
        this.nomeRisco = nome;
    }

    @Column(name = "PRC_NM_CONTROLE", nullable = false, length = TAMANHO_NOME)
    public String getNomeControle() {
        return nomeControle;
    }

    public void setNomeControle(String nomeControle) {
        this.nomeControle = nomeControle;
    }

    @Column(name = "PRC_NM_ABREVIADO", nullable = false, length = TAMANHO_NOME_ABREVIADO)
    public String getNomeAbreviado() {
        return nomeAbreviado;
    }

    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }

    @Column(name = "PRC_DS", nullable = false, length = TAMANHO_DESCRICAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "PRC_DS_END_MANUAL", nullable = false, length = TAMANHO_ENDERECO_MANUAL)
    public String getEnderecoManual() {
        return enderecoManual;
    }

    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    @Column(name = "PRC_NU_ORDEM", nullable = false)
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

    @OneToMany(mappedBy = "grupoRiscoControle", fetch = FetchType.LAZY)
    public List<ParametroElemento> getParametrosElemento() {
        return parametrosElemento;
    }

    public void setParametrosElemento(List<ParametroElemento> parametrosElemento) {
        this.parametrosElemento = parametrosElemento;
    }
    
    @Column(name = "PRC_CD_TP_GRP", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = TipoParametroGrupoRiscoControleEnum.CLASS_NAME)})
    public TipoParametroGrupoRiscoControleEnum getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoParametroGrupoRiscoControleEnum tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    @Column(name = "PRC_IB_SINTESE_OBRIG", columnDefinition = "smallint", nullable = false)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getSinteseObrigatoria() {
        return sinteseObrigatoria;
    }

    public void setSinteseObrigatoria(SimNaoEnum sinteseObrigatoria) {
        this.sinteseObrigatoria = sinteseObrigatoria;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPk()).append(nomeRisco).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParametroGrupoRiscoControle) {
            ParametroGrupoRiscoControle contato = (ParametroGrupoRiscoControle) obj;
            return new EqualsBuilder().append(getPk(), contato.getPk()).isEquals();
        }
        return false;
    }
    
    
    @Transient
    public String getAbreviado() {
        return "ARCs " + getNomeAbreviado();
    }
    
    @Transient
    public String getNome(TipoGrupoEnum tipo) {
        if (TipoGrupoEnum.RISCO.equals(tipo)) {
            return nomeRisco;
        } else if (TipoGrupoEnum.CONTROLE.equals(tipo)) {
            return nomeControle;
        } else if (TipoGrupoEnum.EXTERNO.equals(tipo)) {
            return nomeAbreviado;
        } else {
            return "";
        }
    }
    
}
