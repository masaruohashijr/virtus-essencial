package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;

public class IndiceVO implements Serializable {
    private static Map<String, IndiceVO> mapIndiceVo = new HashMap<String, IndiceVO>();
    private String descricaoIndice;
    private BigDecimal valorIndice;
    private BigDecimal valorIndiceAjustado;
    private String valorIndiceAjustadoFormatado;
    private String valorvalorIndiceFormatado;

    public IndiceVO() {
        // TODO Auto-generated constructor stub
    }

    public IndiceVO(String descricaoIndice, BigDecimal valorIndice, BigDecimal valorIndiceAjustado) {
        this.descricaoIndice = descricaoIndice;
        this.valorIndice = valorIndice;
        this.valorIndiceAjustado = valorIndiceAjustado;
        getMapIndiceVo().put(descricaoIndice, this);
    }

    public String getDescricaoIndice() {
        return descricaoIndice;
    }

    public void setDescricaoIndice(String descricaoIndice) {
        this.descricaoIndice = descricaoIndice;
    }

    public BigDecimal getValorIndice() {
        return valorIndice;
    }

    public void setValorIndice(BigDecimal valorIndice) {
        this.valorIndice = valorIndice;
    }

    public BigDecimal getValorIndiceAjustado() {
        return valorIndiceAjustado;
    }

    public void setValorIndiceAjustado(BigDecimal valorIndiceAjustado) {
        this.valorIndiceAjustado = valorIndiceAjustado;
    }

    public Map<String, IndiceVO> getMapIndiceVo() {
        return mapIndiceVo;
    }

    public static void adicionarValores(QuadroPosicaoFinanceira quadro, List<IndiceVO> indices) {
        for (IndiceVO indiceVO : indices) {
            IndiceVO indiceVOBasileia = indiceVO.getMapIndiceVo().get(QuadroPosicaoFinanceiraVO.INDICE_DE_BASILEIA);
            if (indiceVOBasileia != null && indiceVO.getDescricaoIndice().equals(indiceVOBasileia.getDescricaoIndice())) {
                quadro.setIndiceBaseleiaAjustado(indiceVO.getValorIndiceAjustado());
            }

            IndiceVO indiceVOBasileiaAmplo =
                    indiceVO.getMapIndiceVo().get(QuadroPosicaoFinanceiraVO.INDICE_DE_BASILEIA_AMPLO_INCLUI_RBAN);
            if (indiceVOBasileiaAmplo != null
                    && indiceVO.getDescricaoIndice().equals(indiceVOBasileiaAmplo.getDescricaoIndice())) {
                quadro.setIndiceBaseleiaAmploAjustado(indiceVO.getValorIndiceAjustado());
            }

            IndiceVO indiceVOImobilizasao =
                    indiceVO.getMapIndiceVo().get(QuadroPosicaoFinanceiraVO.INDICE_DE_IMOBILIZACAO);
            if (indiceVOImobilizasao != null
                    && indiceVO.getDescricaoIndice().equals(indiceVOImobilizasao.getDescricaoIndice())) {
                quadro.setIndiceImobilizacaoAjustado(indiceVO.getValorIndiceAjustado());
            }
        }
    }

    public String getValorIndiceAjustadoFormatado() {
        if (valorIndiceAjustado != null) {
            valorIndiceAjustadoFormatado = ajustarEscala(valorIndiceAjustado);
        }
        return valorIndiceAjustadoFormatado;
    }

    public String getValorIndiceFormatado() {
        if (valorIndice != null) {
            valorvalorIndiceFormatado = ajustarEscala(valorIndice);
        }
        return valorvalorIndiceFormatado;
    }

    private String ajustarEscala(BigDecimal valor) {
        valor.setScale(4, RoundingMode.HALF_EVEN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
        df.setGroupingUsed(false);
        return df.format(valor);
    }
}
