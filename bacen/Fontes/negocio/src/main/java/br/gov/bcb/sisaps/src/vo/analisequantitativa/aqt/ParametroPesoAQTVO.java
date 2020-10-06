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

import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ParametroPesoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "PPA_ID";
    private static final int TAMANHO_DESCRICAO = 40;
    private static final int TAMANHO_SIGLA = 2;
    private String descricao;
    private String sigla;
    private Short quantidade;
    private Metodologia metodologia;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Short getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Short quantidade) {
        this.quantidade = quantidade;
    }

    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    public static int getTamanhoDescricao() {
        return TAMANHO_DESCRICAO;
    }

    public static int getTamanhoSigla() {
        return TAMANHO_SIGLA;
    }

}
