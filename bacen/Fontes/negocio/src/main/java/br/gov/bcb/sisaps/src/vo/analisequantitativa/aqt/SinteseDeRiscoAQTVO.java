/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class SinteseDeRiscoAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "SIA_ID";
    private SinteseDeRiscoAQTVO sinteseVigente;
    private ParametroAQT paramentroAQT;
    private String justificativa;
    private Ciclo ciclo;

    public SinteseDeRiscoAQTVO getSinteseVigente() {
        return sinteseVigente;
    }

    public void setSinteseVigente(SinteseDeRiscoAQTVO sinteseVigente) {
        this.sinteseVigente = sinteseVigente;
    }

    public ParametroAQT getParamentroAQT() {
        return paramentroAQT;
    }

    public void setParamentroAQT(ParametroAQT paramentroAQT) {
        this.paramentroAQT = paramentroAQT;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

}
