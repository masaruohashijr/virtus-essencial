/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class ExcecaoUtil {

    public static void verificarLancamentoExcecao(ErrorMessage mensagem, boolean condicao) {
        verificarLancamentoExcecao(Arrays.asList(mensagem), condicao);
    }

    public static void verificarLancamentoExcecao(List<ErrorMessage> mensagens, boolean condicao) {
        verificarLancamentoExcecao(new NegocioException(mensagens), condicao);
    }

    public static void verificarLancamentoExcecao(NegocioException excecao, boolean condicao) {
        if (condicao) {
            throw excecao;
        }
    }

    public static void lancarNegocioException(List<ErrorMessage> mensagens) {
        if (!mensagens.isEmpty()) {
            throw new NegocioException(mensagens);
        }
    }

    public static void lancarNegocioException(String chaveMensagem, Throwable causa, boolean condicao) {
        if (condicao) {
            throw new NegocioException(chaveMensagem, causa);
        }
    }

    public static void lancarNegocioException(ErrorMessage erro, boolean condicao) throws NegocioException {
        List<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        ErrorMessageUtil.adicionarErro(erros, erro, condicao);
        lancarNegocioException(erros);
    }

    public static void lancarNegocioException(ErrorMessage erro) throws NegocioException {
        List<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        ErrorMessageUtil.adicionarErro(erros, erro);
        lancarNegocioException(erros);
    }

}