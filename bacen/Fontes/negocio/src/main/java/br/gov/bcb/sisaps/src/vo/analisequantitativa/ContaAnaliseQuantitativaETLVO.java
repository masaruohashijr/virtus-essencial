package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ContaAnaliseQuantitativaETLVO extends ObjetoPersistenteVO implements Serializable {

    private String decricao;
    private BigDecimal valor;
    private BigDecimal ajustado;
    private String porcentagem;

    public String getDecricao() {
        return decricao;
    }

    public void setDecricao(String decricao) {
        this.decricao = decricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getAjustado() {
        return ajustado;
    }

    public void setAjustado(BigDecimal ajustado) {
        this.ajustado = ajustado;
    }

    public String getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(String porcentagem) {
        this.porcentagem = porcentagem;
    }

    public static List<ContaAnaliseQuantitativaETLVO> converterParaListaVo() {
        ArrayList<ContaAnaliseQuantitativaETLVO> lista = new ArrayList<ContaAnaliseQuantitativaETLVO>();
        ContaAnaliseQuantitativaETLVO contaAnaliseQuantitativaETLVO = new ContaAnaliseQuantitativaETLVO();
        contaAnaliseQuantitativaETLVO.setDecricao("Ativo Total Ajustado");
        contaAnaliseQuantitativaETLVO.setValor(BigDecimal.valueOf(4.276));
        contaAnaliseQuantitativaETLVO.setAjustado(BigDecimal.valueOf(4.276));
        contaAnaliseQuantitativaETLVO.setPorcentagem("100%");
        lista.add(contaAnaliseQuantitativaETLVO);
        return lista;
    }

}
