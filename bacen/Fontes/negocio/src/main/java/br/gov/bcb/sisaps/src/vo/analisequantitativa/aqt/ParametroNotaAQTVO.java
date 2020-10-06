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
package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class ParametroNotaAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "PNA_ID";
    public static final String NAO_APLICAVEL = "N/A";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private Short valor;
    private BigDecimal limiteInferior;
    private BigDecimal limiteSuperior;
    private SimNaoEnum isNotaNA;
    private Metodologia metodologia;
    private String descricao;
    private String cor;

    public Short getValor() {
        return valor;
    }

    public void setValor(Short valor) {
        this.valor = valor;
    }

    public BigDecimal getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(BigDecimal limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public BigDecimal getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(BigDecimal limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public SimNaoEnum getIsNotaNA() {
        return isNotaNA;
    }

    public void setIsNotaNA(SimNaoEnum isNotaNA) {
        this.isNotaNA = isNotaNA;
    }

    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public static String getNaoAplicavel() {
        return NAO_APLICAVEL;
    }

    public static String getDefinicaoDecimal52() {
        return DEFINICAO_DECIMAL_5_2;
    }

}
