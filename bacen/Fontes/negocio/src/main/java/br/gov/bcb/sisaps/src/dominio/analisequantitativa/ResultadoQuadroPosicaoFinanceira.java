package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.math.BigDecimal;
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

import org.hibernate.annotations.ForeignKey;

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "REQ_RESULTADO_QUADRO_POS_FINAN", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ID,
                column = @Column(name = ResultadoQuadroPosicaoFinanceira.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_OPERADOR_ATUALIZACAO,
                column = @Column(name = "REQ_CD_OPERADOR", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ULTIMA_ATUALIZACAO,
                column = @Column(name = "REQ_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO,
                column = @Column(name = "REQ_CD_VERSAO", nullable = false))})
public class ResultadoQuadroPosicaoFinanceira extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String QUADRO_POSICAO_FINANCEIRA = "quadroPosicaoFinanceira";
    public static final String CAMPO_ID = "REQ_ID";
    private static final String DECIMAL_COLUMN_DEFINITION = "decimal(4,1)";

    private QuadroPosicaoFinanceira quadroPosicaoFinanceira;
    private Integer periodo;
    private Integer lucroLiquido;
    private Integer lucroLiquidoAjustado;
    private BigDecimal rspla;
    private BigDecimal rsplaAjustado;
    private Integer ajuste;
    private String rsplaFormatado;

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "IRREQQPF")
    @JoinColumn(name = QuadroPosicaoFinanceira.CAMPO_ID, nullable = false)
    public QuadroPosicaoFinanceira getQuadroPosicaoFinanceira() {
        return quadroPosicaoFinanceira;
    }

    public void setQuadroPosicaoFinanceira(QuadroPosicaoFinanceira quadroPosicaoFinanceira) {
        this.quadroPosicaoFinanceira = quadroPosicaoFinanceira;
    }

    @Column(name = "REQ_NU_PERIODO")
    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    @Column(name = "REQ_VL_LUCRO")
    public Integer getLucroLiquido() {
        return lucroLiquido;
    }

    public void setLucroLiquido(Integer lucroLiquido) {
        this.lucroLiquido = lucroLiquido;
    }

    @Transient
    public Integer getLucroLiquidoAjustado() {
        if (lucroLiquido != null && ajuste != null) {
            lucroLiquidoAjustado = lucroLiquido - ajuste;
        }
        return lucroLiquidoAjustado;
    }

    public void setLucroLiquidoAjustado(Integer lucroLiquidoAjustado) {
        this.lucroLiquidoAjustado = lucroLiquidoAjustado;
    }

    @Column(name = "REQ_VL_RSPLA", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getRspla() {
        return rspla;
    }

    public void setRspla(BigDecimal rspla) {
        this.rspla = rspla;
    }

    @Column(name = "REQ_VL_RSPLA_AJUSTADO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getRsplaAjustado() {
        return rsplaAjustado;
    }

    public void setRsplaAjustado(BigDecimal rsplaAjustado) {
        this.rsplaAjustado = rsplaAjustado;
    }

    @Column(name = "REQ_VL_AJUSTE_LUCRO")
    public Integer getAjuste() {
        return ajuste;
    }

    public void setAjuste(Integer ajuste) {
        this.ajuste = ajuste;
    }

    @Transient
    public String getPeriodoFormatado() {
        return DataUtil.formatoMesAno(String.valueOf(periodo));
    }

    @Transient
    public String getRsplaFormatado() {
        if (rspla != null) {
            BigDecimal bd = rspla;
            bd.setScale(Constantes.NUMERO_3);
            DecimalFormat df = ajustarFormato();
            rsplaFormatado = df.format(bd);
        }
        return rsplaFormatado;
    }

    private DecimalFormat ajustarFormato() {
        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        df.setGroupingUsed(false);
        return df;
    }

    @Transient
    public String getRsplaAjustadoFormatado() {
        String rsplaAjustadoFormatado = null;
        if (rsplaAjustado != null) {
            BigDecimal bd = rsplaAjustado;
            bd.setScale(Constantes.NUMERO_3);
            DecimalFormat df = ajustarFormato();
            rsplaAjustadoFormatado = df.format(bd);
        }
        return rsplaAjustadoFormatado;
    }

}