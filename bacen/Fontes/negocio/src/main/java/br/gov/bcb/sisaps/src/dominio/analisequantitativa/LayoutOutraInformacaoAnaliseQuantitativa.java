package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Entity
@Table(name = "LOI_LAYOUT_OUTRA_INFO_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistente.PROP_ID,
        column = @Column(name = LayoutOutraInformacaoAnaliseQuantitativa.CAMPO_ID))})
public class LayoutOutraInformacaoAnaliseQuantitativa extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "LOI_ID";
    private static final String ENUM_INTEGER_USER_TYPE =
            "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType";
    private static final String SMALLINT = "smallint";
    private static final String ENUM_CLASS = "enumClass";

    private LayoutAnaliseQuantitativa layoutAnaliseQuantitativa;
    private OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa;
    private Integer sequencial;
    private SimNaoEnum exibirNulo;
    private TipoEdicaoInformacaoEnum tipoEdicaoInformacaoEnum;
    private Integer numeroCasasInteiras;
    private Integer numeroCasasDecimais;
    
    private List<OutraInformacaoQuadroPosicaoFinanceira> outrasInformacaoQuadroPosicaoFinanceiras;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = LayoutAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public LayoutAnaliseQuantitativa getLayoutAnaliseQuantitativa() {
        return layoutAnaliseQuantitativa;
    }

    public void setLayoutAnaliseQuantitativa(LayoutAnaliseQuantitativa layoutAnaliseQuantitativa) {
        this.layoutAnaliseQuantitativa = layoutAnaliseQuantitativa;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = OutraInformacaoAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public OutraInformacaoAnaliseQuantitativa getOutraInformacaoAnaliseQuantitativa() {
        return outraInformacaoAnaliseQuantitativa;
    }

    public void setOutraInformacaoAnaliseQuantitativa(
            OutraInformacaoAnaliseQuantitativa outraInformacaoAnaliseQuantitativa) {
        this.outraInformacaoAnaliseQuantitativa = outraInformacaoAnaliseQuantitativa;
    }

    @Column(name = "LOI_SQ_SEQUENCIAL", columnDefinition = SMALLINT, nullable = false)
    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    @Column(name = "LOI_IB_EXIBIR_NULO", nullable = false, columnDefinition = SMALLINT)
    @Type(type = ENUM_INTEGER_USER_TYPE,
            parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getExibirNulo() {
        return exibirNulo;
    }

    public void setExibirNulo(SimNaoEnum exibirNulo) {
        this.exibirNulo = exibirNulo;
    }

    @Column(name = "LOI_CD_TIPO_EDICAO", nullable = false, columnDefinition = SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", 
        parameters = {@Parameter(name = ENUM_CLASS, value = TipoEdicaoInformacaoEnum.CLASS_NAME)})
    public TipoEdicaoInformacaoEnum getTipoEdicaoInformacaoEnum() {
        return tipoEdicaoInformacaoEnum;
    }

    public void setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum tipoEdicaoInformacaoEnum) {
        this.tipoEdicaoInformacaoEnum = tipoEdicaoInformacaoEnum;
    }

    @OneToMany(mappedBy = "layoutOutraInformacaoAnaliseQuantitativa", fetch = FetchType.LAZY)
    public List<OutraInformacaoQuadroPosicaoFinanceira> getOutrasInformacaoQuadroPosicaoFinanceiras() {
        return outrasInformacaoQuadroPosicaoFinanceiras;
    }

    public void setOutrasInformacaoQuadroPosicaoFinanceiras(
            List<OutraInformacaoQuadroPosicaoFinanceira> outrasInformacaoQuadroPosicaoFinanceiras) {
        this.outrasInformacaoQuadroPosicaoFinanceiras = outrasInformacaoQuadroPosicaoFinanceiras;
    }

    @Column(name = "LOI_NU_CASAS_INTEIRAS", columnDefinition = SMALLINT, nullable = false)
    public Integer getNumeroCasasInteiras() {
        return numeroCasasInteiras;
    }

    public void setNumeroCasasInteiras(Integer numeroCasasInteiras) {
        this.numeroCasasInteiras = numeroCasasInteiras;
    }

    @Column(name = "LOI_NU_CASAS_DECIMAIS", columnDefinition = SMALLINT, nullable = false)
    public Integer getNumeroCasasDecimais() {
        return numeroCasasDecimais;
    }

    public void setNumeroCasasDecimais(Integer numeroCasasDecimais) {
        this.numeroCasasDecimais = numeroCasasDecimais;
    }

}
