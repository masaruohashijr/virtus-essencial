/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class NotaAjustadaAEFVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "NAA_ID";
    private NotaAjustadaAEFVO notaAjustadaAEFVigente;
    private ParametroNotaAQT paramentroNotaAQT;
    private String justificativa;
    private Ciclo ciclo;

    public NotaAjustadaAEFVO getNotaAjustadaAEFVigente() {
        return notaAjustadaAEFVigente;
    }

    public void setNotaAjustadaAEFVigente(NotaAjustadaAEFVO notaAjustadaAEFVigente) {
        this.notaAjustadaAEFVigente = notaAjustadaAEFVigente;
    }

    public ParametroNotaAQT getParamentroNotaAQT() {
        return paramentroNotaAQT;
    }

    public void setParamentroNotaAQT(ParametroNotaAQT paramentroNotaAQT) {
        this.paramentroNotaAQT = paramentroNotaAQT;
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
