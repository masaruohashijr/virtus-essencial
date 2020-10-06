/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ParametroAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "PAT_ID";
    private String descricao;
    private Short ordem;
    private Metodologia metodologia;
    private Short pesoAQT;

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

    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    public Short getPesoAQT() {
        return pesoAQT;
    }

    public void setPesoAQT(Short pesoAQT) {
        this.pesoAQT = pesoAQT;
    }

}
