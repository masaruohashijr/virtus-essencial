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

import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraSalvarPerfilAtuacaoES {

    private PerfilAtuacaoES perfilAtuacaoES;

    public RegraSalvarPerfilAtuacaoES(PerfilAtuacaoES perfilAtuacaoES) {
        this.perfilAtuacaoES = perfilAtuacaoES;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (this.perfilAtuacaoES.getPk() == null) {
            PerfilAtuacaoES perfilAtuacaoESBase =
                    PerfilAtuacaoESMediator.get().buscarPerfilAtuacaoESRascunho(perfilAtuacaoES.getCiclo().getPk());
            if (perfilAtuacaoESBase != null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage("Erro de acesso concorrente."), true);
                SisapsUtil.lancarNegocioException(erros);
            }
        }

    }
}