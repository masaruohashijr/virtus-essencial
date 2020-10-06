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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraBotaoAnalisarARC {

    private final AvaliacaoRiscoControle avaliacaoRiscoControle;

    public RegraBotaoAnalisarARC(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        //C012 Falha - Concluir an�lise n�o informando justificativa para qualquer nota, caso o supervisor tenha registrado a nota ajustada.
        ElementoMediator.get().verificarElementosSemJustificava(avaliacaoRiscoControle.getElementos(), erros);

        validarAvaliacaoARC(avaliacaoRiscoControle, erros);

        SisapsUtil.lancarNegocioException(erros);
    }

    public void validarAvaliacaoARC(AvaliacaoRiscoControle arc, ArrayList<ErrorMessage> erros) {
        if (arc.avaliacaoSupervisor() != null) {
            AvaliacaoARC avaliacaoSupervisor = arc.avaliacaoSupervisor();
            SisapsUtil.validarObrigatoriedade(avaliacaoSupervisor.getParametroNota(), "Nota de ajuste", erros);
            SisapsUtil.validarObrigatoriedade(
                    SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(avaliacaoSupervisor.getJustificativa()),
                    "Justificativa do ajuste", erros);
        }
    }
}