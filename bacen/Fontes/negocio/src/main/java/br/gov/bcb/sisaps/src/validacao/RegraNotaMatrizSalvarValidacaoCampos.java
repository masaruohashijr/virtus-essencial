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

import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraNotaMatrizSalvarValidacaoCampos {

    private final NotaMatriz notaMatrizAjustada;
    private final String notaCalculadaFinal;

    public RegraNotaMatrizSalvarValidacaoCampos(NotaMatriz notaMatrizAjustada, String notaCalculadaFinal) {
        this.notaMatrizAjustada = notaMatrizAjustada;
        this.notaCalculadaFinal = notaCalculadaFinal;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        if (notaMatrizAjustada.getNotaFinalMatriz() != null && notaMatrizAjustada.getJustificativaNota() == null) {
            erros.add(new ErrorMessage("Campo 'Justificativa' da 'Nova nota ajustada' � de preenchimento obrigat�rio."));
        }

        if (!Util.isNuloOuVazio(notaCalculadaFinal) && notaCalculadaFinal.contains("A")) {
            erros.add(new ErrorMessage(
                    "Ajuste de nota final n�o pode ser confirmada no perfil de risco com nota calculada igual a '*A'."));
        }

        SisapsUtil.lancarNegocioException(erros);
        
    }
}