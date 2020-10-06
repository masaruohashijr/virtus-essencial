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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class DelegacaoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "DEA_ID";

    private String matriculaServidor;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String getMatriculaServidor() {
        return matriculaServidor;
    }

    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

}
