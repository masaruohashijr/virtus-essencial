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
            erros.add(new ErrorMessage("Campo 'Justificativa' da 'Nova nota ajustada' é de preenchimento obrigatório."));
        }

        if (!Util.isNuloOuVazio(notaCalculadaFinal) && notaCalculadaFinal.contains("A")) {
            erros.add(new ErrorMessage(
                    "Ajuste de nota final não pode ser confirmada no perfil de risco com nota calculada igual a '*A'."));
        }

        SisapsUtil.lancarNegocioException(erros);
        
    }
}