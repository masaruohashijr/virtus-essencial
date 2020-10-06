package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraNotaAjustadaAEF {

    private final NotaAjustadaAEF notaRascunho;
    private final String notaAEFCalculada;

    public RegraNotaAjustadaAEF(NotaAjustadaAEF notaRascunho, String notaAEFCalculada) {
        this.notaRascunho = notaRascunho;
        this.notaAEFCalculada = notaAEFCalculada;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        campoObrigatorioConfirmar(erros);
        SisapsUtil.lancarNegocioException(erros);
    }

    private void campoObrigatorioConfirmar(ArrayList<ErrorMessage> erros) {
        if (notaRascunho.getParamentroNotaAQT() != null && notaRascunho.getJustificativa() == null) {
            erros.add(new ErrorMessage(
                    "Campo 'Justificativa' da 'Nova nota ajustada' é de preenchimento obrigatório."));
        }

        if (!Util.isNuloOuVazio(notaAEFCalculada) && notaAEFCalculada.contains("A")) {
            erros.add(new ErrorMessage(
                    "Ajuste de nota final não pode ser confirmada no perfil de risco com nota calculada igual a '*A'."));
        }

        SisapsUtil.lancarNegocioException(erros);
    }
}
