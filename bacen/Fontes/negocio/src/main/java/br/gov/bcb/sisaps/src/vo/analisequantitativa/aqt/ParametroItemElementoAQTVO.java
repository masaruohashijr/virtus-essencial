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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class ParametroItemElementoAQTVO extends ObjetoPersistenteVO {
    public static final String CAMPO_ID = "PIA_ID";
    private static final int TAMANHO_NOME_END_MANUAL = 200;
    private String descricao;
    private ParametroElementoAQT parametroElemento;
    private Short ordem;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ParametroElementoAQT getParametroElemento() {
        return parametroElemento;
    }

    public void setParametroElemento(ParametroElementoAQT parametroElemento) {
        this.parametroElemento = parametroElemento;
    }

    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }

    public static int getTamanhoNomeEndManual() {
        return TAMANHO_NOME_END_MANUAL;
    }

}
