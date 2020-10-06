package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;

public class PatrimonioVO implements Serializable {
    private static Map<String, PatrimonioVO> mapPatrimonioVo = new HashMap<String, PatrimonioVO>();
    private String descricaoPatrimonio;
    private Integer valorPatrimonio;
    private Integer ajustePatrimonio;
    private Integer valorPatrimonioAjustado;
    private String valorPatrimonioFormatado;

    public PatrimonioVO() {
        // TODO Auto-generated constructor stub
    }

    public PatrimonioVO(String descricaoPatrimonio, Integer valorPatrimonio, Integer ajustePatrimonio) {
        this.descricaoPatrimonio = descricaoPatrimonio;
        this.valorPatrimonio = valorPatrimonio;
        this.ajustePatrimonio = ajustePatrimonio;
        getMapPatrimonioVo().put(descricaoPatrimonio, this);
    }

    public String getDescricaoPatrimonio() {
        return descricaoPatrimonio;
    }

    public void setDescricaoPatrimonio(String descricaoPatrimonio) {
        this.descricaoPatrimonio = descricaoPatrimonio;
    }

    public Integer getValorPatrimonio() {
        return valorPatrimonio;
    }

    public void setValorPatrimonio(Integer valorPatrimonio) {
        this.valorPatrimonio = valorPatrimonio;
    }

    public Integer getAjustePatrimonio() {
        return ajustePatrimonio;
    }

    public void setAjustePatrimonio(Integer ajustePatrimonio) {
        this.ajustePatrimonio = ajustePatrimonio;
    }

    public Integer getValorPatrimonioAjustado() {
        if (valorPatrimonio != null && ajustePatrimonio != null) {
            valorPatrimonioAjustado = valorPatrimonio - ajustePatrimonio;
        }
        return valorPatrimonioAjustado;
    }

    public void setValorPatrimonioAjustado(Integer valorPatrimonioAjustado) {
        this.valorPatrimonioAjustado = valorPatrimonioAjustado;
    }

    public Map<String, PatrimonioVO> getMapPatrimonioVo() {
        return mapPatrimonioVo;
    }

    public static void adicionarValores(QuadroPosicaoFinanceira quadro, List<PatrimonioVO> patrimonios) {
        for (PatrimonioVO patrimonioVO : patrimonios) {
            patrimonioCapitalComplementar(quadro, patrimonioVO);
            patrimonioCapitalPrincipal(quadro, patrimonioVO);
            patrimonioPrNivel1(quadro, patrimonioVO);
            patrimonioPrNivel2(quadro, patrimonioVO);
        }
    }



    private static void patrimonioCapitalPrincipal(QuadroPosicaoFinanceira quadro, PatrimonioVO patrimonioVO) {
        PatrimonioVO patrimonioCapitalPrincipal =
                patrimonioVO.getMapPatrimonioVo().get(QuadroPosicaoFinanceiraVO.CAPITAL_PRINCIPAL);
        if (patrimonioCapitalPrincipal != null
                && patrimonioCapitalPrincipal.getDescricaoPatrimonio()
                        .equals(patrimonioVO.getDescricaoPatrimonio())) {
            quadro.setCapitalPrincipal(patrimonioVO.getValorPatrimonio());
            quadro.setAjusteCapitalPrincipal(patrimonioVO.getAjustePatrimonio());
        }
    }

    private static void patrimonioCapitalComplementar(QuadroPosicaoFinanceira quadro, PatrimonioVO patrimonioVO) {
        PatrimonioVO patrimonioCapitalComplementar =
                patrimonioVO.getMapPatrimonioVo().get(QuadroPosicaoFinanceiraVO.CAPITAL_COMPLEMENTAR);
        if (patrimonioCapitalComplementar != null
                && patrimonioCapitalComplementar.getDescricaoPatrimonio().equals(
                        patrimonioVO.getDescricaoPatrimonio())) {
            quadro.setCapitalComplementar(patrimonioVO.getValorPatrimonio());
            quadro.setAjusteCapitalComplementar(patrimonioVO.getAjustePatrimonio());
        }
    }


    private static void patrimonioPrNivel1(QuadroPosicaoFinanceira quadro, PatrimonioVO patrimonioVO) {
        PatrimonioVO patrimonioPrNivel1 = patrimonioVO.getMapPatrimonioVo().get(QuadroPosicaoFinanceiraVO.PR_NIVEL_1);
        if (patrimonioPrNivel1 != null
                && patrimonioPrNivel1.getDescricaoPatrimonio().equals(patrimonioVO.getDescricaoPatrimonio())) {
            quadro.setPrNivelUm(patrimonioVO.getValorPatrimonio());
            quadro.setAjustePrNivelUm(patrimonioVO.getAjustePatrimonio());
        }
    }

    private static void patrimonioPrNivel2(QuadroPosicaoFinanceira quadro, PatrimonioVO patrimonioVO) {
        PatrimonioVO patrimonioPrNivel2 = patrimonioVO.getMapPatrimonioVo().get(QuadroPosicaoFinanceiraVO.PR_NIVEL_2);
        if (patrimonioPrNivel2 != null
                && patrimonioPrNivel2.getDescricaoPatrimonio().equals(patrimonioVO.getDescricaoPatrimonio())) {
            quadro.setPrNivelDois(patrimonioVO.getValorPatrimonio());
            quadro.setAjustePrNivelDois(patrimonioVO.getAjustePatrimonio());
        }
    }

    public String getValorPatrimonioFormatado() {
        if (valorPatrimonio != null) {
            DecimalFormat df = new DecimalFormat("#,###");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
            valorPatrimonioFormatado = df.format(valorPatrimonio);
        }
        return valorPatrimonioFormatado;
    }

}
