/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraBotaoAnalisarAnef {

    private final AnaliseQuantitativaAQT aqt;

    public RegraBotaoAnalisarAnef(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        ElementoAQTMediator.get().verificarElementosSemJustificava(aqt, erros);

        validarAvaliacaoARC(aqt.avaliacaoSupervisor(), erros);

        SisapsUtil.lancarNegocioException(erros);
    }

    public void validarAvaliacaoARC(AvaliacaoAQT avaliacaoAnef, ArrayList<ErrorMessage> erros) {
        if (avaliacaoAnef != null) {
            SisapsUtil.validarObrigatoriedade(avaliacaoAnef.getParametroNota(), "Nota de ajuste", erros);
            SisapsUtil.validarObrigatoriedade(
                    SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(avaliacaoAnef.getJustificativa()),
                    "Justificativa do ajuste", erros);
        }
    }
}