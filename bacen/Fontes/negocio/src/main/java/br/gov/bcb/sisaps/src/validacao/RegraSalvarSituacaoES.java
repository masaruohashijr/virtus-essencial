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

import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.mediator.SituacaoESMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraSalvarSituacaoES {

    private SituacaoES situacaoES;

    public RegraSalvarSituacaoES(SituacaoES situacaoES) {
        this.situacaoES = situacaoES;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (this.situacaoES.getPk() == null) {
            SituacaoES situacaoESBase =
                    SituacaoESMediator.get().buscarSituacaoESRascunho(situacaoES.getCiclo().getPk());
            if (situacaoESBase != null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage("Erro de acesso concorrente."), true);
                SisapsUtil.lancarNegocioException(erros);
            }
        }

    }
}