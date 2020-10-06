package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "CQP_CONTA_QUADRO_POSICAO_FINANCEIRA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ID,
                column = @Column(name = ContaQuadroPosicaoFinanceira.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_OPERADOR_ATUALIZACAO,
                column = @Column(name = "CQP_CD_OPERADOR", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ULTIMA_ATUALIZACAO,
                column = @Column(name = "CQP_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO,
                column = @Column(name = "CQP_CD_VERSAO", nullable = false))})
public class ContaQuadroPosicaoFinanceira extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "CQP_ID";
    public static final String QUADRO_POSICAO_FINANCEIRA = "quadroPosicaoFinanceira";
    private static final String BR = "BR";
    private static final String PT = "pt";
    private static final String FORMATO_INT = "#,###";
    private static final String BARRA_N = "\n";
    private static final Integer ZERO = 0;
    private static final String HIFEN = "-";
    private QuadroPosicaoFinanceira quadroPosicaoFinanceira;
    private LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa;
    private Integer valor;
    private Integer valorAjuste;
    private SimNaoEnum exibir = SimNaoEnum.NAO;
    private Integer ajustado;
    private Integer valorTotalAjustado;
    private String escoreFormatado;
    private String ajustadoFormatado;

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "IRCQPQPF")
    @JoinColumn(name = QuadroPosicaoFinanceira.CAMPO_ID, nullable = false)
    public QuadroPosicaoFinanceira getQuadroPosicaoFinanceira() {
        return quadroPosicaoFinanceira;
    }

    public void setQuadroPosicaoFinanceira(QuadroPosicaoFinanceira quadroPosicaoFinanceira) {
        this.quadroPosicaoFinanceira = quadroPosicaoFinanceira;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "IRCQPLCA")
    @JoinColumn(name = LayoutContaAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public LayoutContaAnaliseQuantitativa getLayoutContaAnaliseQuantitativa() {
        return layoutContaAnaliseQuantitativa;
    }

    public void setLayoutContaAnaliseQuantitativa(LayoutContaAnaliseQuantitativa layoutContaAnaliseQuantitativa) {
        this.layoutContaAnaliseQuantitativa = layoutContaAnaliseQuantitativa;
    }

    @Column(name = "CQP_VL_VALOR")
    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    @Column(name = "CQP_VL_AJUSTE")
    public Integer getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(Integer valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    @Column(name = "CQP_IB_EXIBIR", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType",
            parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getExibir() {
        return exibir;
    }

    public void setExibir(SimNaoEnum exibir) {
        this.exibir = exibir;
    }

    @Transient
    public String getValorFormatado() {
        return formatar(valor);
    }

    private String formatar(Integer valorInteiro) {
        String valorFormatado = "";
        if (BARRA_N.equals(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativa().getDescricao())) {
            return valorFormatado;
        } else if (valorInteiro == null || ZERO.equals(valorInteiro)) {
            valorFormatado = HIFEN;
        } else {
            DecimalFormat df = new DecimalFormat(FORMATO_INT);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale(PT, BR)));
            valorFormatado = df.format(valorInteiro);
        }
        return valorFormatado;
    }

    private String formatarAjustado(Integer valorInteiro) {
        String valorFormatado = "";
        if (valorInteiro != null) {
            DecimalFormat df = new DecimalFormat(FORMATO_INT);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale(PT, BR)));
            valorFormatado = df.format(valorInteiro);
        }
        return valorFormatado;
    }

    @Transient
    public String getEscore() {
        return formatarEscore(valorTotalAjustado);
    }

    public void setEscore(String escoreFormatado) {
        this.escoreFormatado = escoreFormatado;
    }

    public String formatarEscore(Integer valorTotalAjustadoContaPai) {
        BigDecimal escore = null;
        ContaQuadroPosicaoFinanceira conta = this;
        LayoutContaAnaliseQuantitativa layout = conta.getLayoutContaAnaliseQuantitativa();
        Integer ajustado = conta.getAjustado();

        if (conta.isContaRaiz() && !BARRA_N.equals(layout.getContaAnaliseQuantitativa().getDescricao())) {
            escore = new BigDecimal(100);
        } else if (valorTotalAjustadoContaPai != null && valorTotalAjustadoContaPai != 0) {
            BigDecimal val = null;
            if (conta.getValor() == null) {
                val = new BigDecimal(ajustado == null ? 0 : ajustado);
            } else {
                val = new BigDecimal(ajustado == null ? conta.getValor() : ajustado);
            }
            escore =
                    val.divide(new BigDecimal(valorTotalAjustadoContaPai), 3, RoundingMode.HALF_DOWN).multiply(
                            new BigDecimal(100));
        }
        escoreFormatado = "";
        return  contFormatEscore(escore);
    }

    private String contFormatEscore(BigDecimal escore) {
        if (escore != null) {
            if (0 == escore.intValue() && isLinhaEmBranco()) {
                return escoreFormatado;
            } else {
                BigDecimal bd = escore;
                bd.setScale(Constantes.NUMERO_3, RoundingMode.HALF_DOWN);

                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale(PT, BR)));
                df.setMaximumFractionDigits(1);
                if (escore.equals(new BigDecimal(100))) {
                    df.setMinimumFractionDigits(0);
                } else {
                    df.setMinimumFractionDigits(1);
                }
                df.setGroupingUsed(false);
                escoreFormatado = df.format(bd).concat("%");
                if ("100,0%".equals(escoreFormatado)) {
                    return "100%";
                }
            }
        }
        return escoreFormatado;
    }

    @Transient
    public Integer getAjustado() {
        ajustado = valorAjuste == null ? null : ((valor == null) ? 0 : valor) - valorAjuste;
        return ajustado;
    }

    @Transient
    public String getAjustadoFormatado() {
        ajustadoFormatado = formatarAjustado(getAjustado());
        return ajustadoFormatado;
    }

    public void setAjustadoFormatado(String ajustadoFormatado) {
        String ajustadoFormatadoTextoNumeral = SisapsUtil.getNumeros(ajustadoFormatado);
        if (StringUtils.isNotBlank(ajustadoFormatadoTextoNumeral)) {
            this.ajustado = Integer.valueOf(ajustadoFormatadoTextoNumeral);
        } else {
            this.ajustado = null;
        }
        this.ajustadoFormatado = ajustadoFormatado;
    }

    public void setAjustado(Integer ajustado) {
        this.ajustado = ajustado;
    }

    @Transient
    public Integer getValorTotalAjustado() {
        return valorTotalAjustado;
    }

    public void setValorTotalAjustado(Integer valorTotalAjustado) {
        this.valorTotalAjustado = valorTotalAjustado;
    }

    @Transient
    public boolean isContaRaiz() {
        return layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativaPai() == null;
    }

    @Transient
    public Boolean isLinhaEmBranco() {
        return BARRA_N.equals(layoutContaAnaliseQuantitativa.getContaAnaliseQuantitativa().getDescricao());
    }
}
