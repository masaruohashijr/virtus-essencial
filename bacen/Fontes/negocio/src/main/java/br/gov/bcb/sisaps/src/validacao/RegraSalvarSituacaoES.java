/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
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