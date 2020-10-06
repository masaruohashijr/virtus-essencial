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
package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

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
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelConcorrente;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@SuppressWarnings("serial")
@Entity
@Immutable
@Table(name = "PNA_PAR_NOTA_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroNotaAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PNA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PNA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelConcorrente.PROP_VERSAO, column = @Column(name = "PNA_NU_VERSAO", nullable = false))})
public class ParametroNotaAQT extends ObjetoPersistenteAuditavelVersionado<Integer> implements Comparable<ParametroNotaAQT> {
    public static final String CAMPO_ID = "PNA_ID";
    public static final String NAO_APLICAVEL = "N/A";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private BigDecimal valor;
    private BigDecimal limiteInferior;
    private BigDecimal limiteSuperior;
    private SimNaoEnum isNotaNA;
    private SimNaoEnum isNotaElemento;
    private Metodologia metodologia;
    private String descricao;
    private String cor;

    @Column(name = "PNA_CD_VALOR", nullable = false, columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Column(name = "PNA_VL_LIM_INFER", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(BigDecimal limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    @Column(name = "PNA_VL_LIM_SUPER", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(BigDecimal limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    @Column(name = "PNA_IB_NOTA_NA", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getNotaNA() {
        return isNotaNA;
    }

    public void setNotaNA(SimNaoEnum isNotaNA) {
        this.isNotaNA = isNotaNA;
    }

    @Column(name = "PNA_IB_NOTA_ELEMENTO", columnDefinition = "smallint")
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

    @Column(name = "PNA_DS_NOTA")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "PNA_DS_COR", nullable = false)
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
                if (metodologia.getIsCalculoMedia() == null || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO)) {
                    descricao = valor.toString().substring(0, valor.toString().length() - 3);
                } else {
                    descricao = valor.toString().replace('.', ',');
                }
            }
        }
        return descricao;
    }

    @Override
    public int compareTo(ParametroNotaAQT o) {
        return valor.compareTo(o.valor);
    }

    @Transient
    public String getValorString() {
        String descricao = null;
        if (valor != null) {
            if (metodologia.getIsCalculoMedia() == null || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO)) {
                descricao = valor.toString().substring(0, valor.toString().length() - 3);
            } else {
                descricao = valor.toString().replace('.', ',');
            }
        }
        return descricao;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retorno = false;

        if (obj instanceof ParametroNotaAQT) {
            ParametroNotaAQT o = (ParametroNotaAQT) obj;
            if (isNotaElemento == null || isNotaElemento.booleanValue()) {
                retorno =
                        new EqualsBuilder().append(this.isNotaNA, o.isNotaNA).append(this.valor, o.valor)
                        .isEquals();
            } else {
                retorno =
                        new EqualsBuilder().append(this.isNotaNA, o.isNotaNA).append(this.descricao, o.descricao)
                        .isEquals();
            }
        }

        return retorno;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(isNotaNA).append(valor).toHashCode();
    }

    @Transient
    public String getValorDescricao() {
        if (valor == null || descricao == null) {
            return "";
        } else {
            return valor + " - " + descricao;

        }
    }
}
