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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ParametroElementoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "PEA_ID";
    private String descricao;
    private Short ordem;
    private ParametroAQT paramentoAqt;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }

    public ParametroAQT getParamentoAqt() {
        return paramentoAqt;
    }

    public void setParamentoAqt(ParametroAQT paramentoAqt) {
        this.paramentoAqt = paramentoAqt;
    }

}
