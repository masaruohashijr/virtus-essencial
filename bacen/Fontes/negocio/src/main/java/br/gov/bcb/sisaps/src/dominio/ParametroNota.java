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

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelConcorrente;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@SuppressWarnings("serial")
@Entity
@Immutable
@Table(name = "PNO_PAR_NOTA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroNota.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PNO_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PNO_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelConcorrente.PROP_VERSAO, column = @Column(name = "PNO_NU_VERSAO", nullable = false))})
public class ParametroNota extends ObjetoPersistenteAuditavelVersionado<Integer> implements Comparable<ParametroNota> {
    public static final String CAMPO_ID = "PNO_ID";
    public static final String NAO_APLICAVEL = "N/A";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private BigDecimal valor;
    private BigDecimal limiteInferior;
    private BigDecimal limiteSuperior;
    private SimNaoEnum naoAplicavel;
    private SimNaoEnum isNotaElemento;
    private Metodologia metodologia;
    private String descricao;
    private String cor;

    @Column(name = "PNO_CD_VALOR", nullable = false, columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Column(name = "PNO_VL_LIM_INFER", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(BigDecimal limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    @Column(name = "PNO_VL_LIM_SUPER", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(BigDecimal limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    @Column(name = "PNO_IB_NOTA_NA", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getNaoAplicavel() {
        return naoAplicavel;
    }

    public void setNaoAplicavel(SimNaoEnum naoAplicavel) {
        this.naoAplicavel = naoAplicavel;
    }

    @Column(name = "PNO_IB_NOTA_ELEMENTO", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getIsNotaElemento() {
        return isNotaElemento;
    }

    public void setIsNotaElemento(SimNaoEnum isNotaElemento) {
        this.isNotaElemento = isNotaElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    @Column(name = "PNO_DS_NOTA", nullable = false)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "PNO_DS_COR", nullable = false)
    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    @Transient
    public String getDescricaoValor() {
        String descricao = null;
        if (valor != null) {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                descricao = NAO_APLICAVEL;
            } else {
                if (metodologia.getIsMetodologiaNova()) {
                    descricao = valor.toString().replace('.', ',');
                } else {
                    descricao = valor.toString().substring(0, valor.toString().length() - 3);
                }
            }
        }
        return descricao;
    }

    @Override
    public int compareTo(ParametroNota o) {
        return valor.compareTo(o.valor);
    }

    @Transient
    public String getValorString() {
        String descricao = null;
        if (valor != null) {
            descricao = valor.toString().replace('.', ',');
        }
        return descricao;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retorno = false;

        if (obj instanceof ParametroNota) {
            ParametroNota o = (ParametroNota) obj;
            if (isNotaElemento == null || isNotaElemento.booleanValue()) {
                retorno =
                        new EqualsBuilder().append(this.naoAplicavel, o.getNaoAplicavel()).append(this.valor, o.getValor())
                        .isEquals();
            } else {
                retorno =
                        new EqualsBuilder().append(this.naoAplicavel, o.getNaoAplicavel()).append(
                                this.descricao, o.getDescricao()).isEquals();
            }
        }

        return retorno;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(naoAplicavel).append(valor).toHashCode();
    }

    @Transient
    public String getValorDescricao() {
        if (valor == null || descricao == null) {
            return "";
        } else {
            return valor + " - " + descricao;

        }
    }

    @Transient
    public String getDescricaoFormatadoValor() {
        String descricao = null;
        if (valor != null) {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                descricao = NAO_APLICAVEL;
            } else {
                descricao = valor.toString() + ",00";
            }
        }
        return descricao;
    }

}
