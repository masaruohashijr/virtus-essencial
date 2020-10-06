package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Entity
@Table(name = "LCA_LAYOUT_CONTA_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID,
        column = @Column(name = LayoutContaAnaliseQuantitativa.CAMPO_ID))})
public class LayoutContaAnaliseQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "LCA_ID";
    private static final String ENUM_INTEGER_USER_TYPE =
            "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType";
    private static final String SMALLINT = "smallint";
    private static final String ENUM_CLASS = "enumClass";

    private LayoutAnaliseQuantitativa layoutAnaliseQuantitativa;
    private ContaAnaliseQuantitativa contaAnaliseQuantitativa;
    private ContaAnaliseQuantitativa contaAnaliseQuantitativaPai;
    private Integer sequencial;
    private SimNaoEnum obrigatorio;
    private SimNaoEnum editarAjuste;
    private SimNaoEnum negrito;
    private SimNaoEnum subNivel;
    private SimNaoEnum separacaoDepois;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ForeignKey(name = "IRLCALAQ")
    @JoinColumn(name = LayoutAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public LayoutAnaliseQuantitativa getLayoutAnaliseQuantitativa() {
        return layoutAnaliseQuantitativa;
    }

    public void setLayoutAnaliseQuantitativa(LayoutAnaliseQuantitativa layoutAnaliseQuantitativa) {
        this.layoutAnaliseQuantitativa = layoutAnaliseQuantitativa;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ForeignKey(name = "IRLCACAQ")
    @JoinColumn(name = ContaAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public ContaAnaliseQuantitativa getContaAnaliseQuantitativa() {
        return contaAnaliseQuantitativa;
    }

    public void setContaAnaliseQuantitativa(ContaAnaliseQuantitativa contaAnaliseQuantitativa) {
        this.contaAnaliseQuantitativa = contaAnaliseQuantitativa;
    }

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @ForeignKey(name = "IRLCACAQPAI")
    @JoinColumn(referencedColumnName = ContaAnaliseQuantitativa.CAMPO_ID, name = "CAQ_ID_PAI")
    public ContaAnaliseQuantitativa getContaAnaliseQuantitativaPai() {
        return contaAnaliseQuantitativaPai;
    }

    public void setContaAnaliseQuantitativaPai(ContaAnaliseQuantitativa contaAnaliseQuantitativaPai) {
        this.contaAnaliseQuantitativaPai = contaAnaliseQuantitativaPai;
    }

    @Column(name = "LCA_SQ_SEQUENCIAL", columnDefinition = SMALLINT)
    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    @Column(name = "LCA_IB_OBRIGATORIO", columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(SimNaoEnum obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    @Column(name = "LCA_IB_EDITAR_AJUSTE", nullable = false, columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getEditarAjuste() {
        return editarAjuste;
    }

    public void setEditarAjuste(SimNaoEnum editarAjuste) {
        this.editarAjuste = editarAjuste;
    }

    @Column(name = "LCA_IB_NEGRITO", nullable = false, columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getNegrito() {
        return negrito;
    }

    public void setNegrito(SimNaoEnum negrito) {
        this.negrito = negrito;
    }

    @Column(name = "LCA_IB_SUBNIVEL", nullable = false, columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getSubNivel() {
        return subNivel;
    }

    public void setSubNivel(SimNaoEnum subNivel) {
        this.subNivel = subNivel;
    }

    @Column(name = "LCA_IB_SEPARACAO_DEPOIS", nullable = false, columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getSeparacaoDepois() {
        return separacaoDepois;
    }

    public void setSeparacaoDepois(SimNaoEnum separacaoDepois) {
        this.separacaoDepois = separacaoDepois;
    }

    @Transient
    public ContaAnaliseQuantitativa getRoot() {
        if (contaAnaliseQuantitativaPai != null) {
            return contaAnaliseQuantitativaPai;
        }
        return contaAnaliseQuantitativa;
    }
}
