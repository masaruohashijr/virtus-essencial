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

import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraSalvarPerspectivaES {

    private PerspectivaES perspectivaES;

    public RegraSalvarPerspectivaES(PerspectivaES perspectivaES) {
        this.perspectivaES = perspectivaES;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (this.perspectivaES.getPk() == null) {
            PerspectivaES perspectivaESBase =
                    PerspectivaESMediator.get().buscarPerspectivaESRascunho(perspectivaES.getCiclo().getPk());
            if (perspectivaESBase != null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage("Erro de acesso concorrente."), true);
                SisapsUtil.lancarNegocioException(erros);
            }
        }

    }
}