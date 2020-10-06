package br.gov.bcb.sisaps.util.geral;

import java.util.List;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class SisapsExcecaoUtil extends SisapsValidacaoUtil {

    public static void lancarNegocioException(List<ErrorMessage> mensagens) throws NegocioException {
        ExcecaoUtil.lancarNegocioException(mensagens);
    }

    public static void lancarNegocioException(String message, boolean condicao) throws NegocioException {
        ExcecaoUtil.lancarNegocioException(new ErrorMessage(message), condicao);
    }

    public static void lancarNegocioException(String mensagem, Throwable causa, boolean condicao) {
        ExcecaoUtil.lancarNegocioException(mensagem, causa, condicao);
    }

    public static void lancarNegocioException(ErrorMessage erro, boolean condicao) throws NegocioException {
        ExcecaoUtil.lancarNegocioException(erro, condicao);
    }

    public static void lancarNegocioException(ErrorMessage erro) throws NegocioException {
        ExcecaoUtil.lancarNegocioException(erro);
    }

    public static void adicionarErrorMessage(List<ErrorMessage> erros, String chaveMensagem, String... parametros) {
        erros.add(ErrorMessage.criarErrorMessage(chaveMensagem, parametros));
    }

    public static void adicionarErro(List<ErrorMessage> erros, ErrorMessage erro) {
        ErrorMessageUtil.adicionarErro(erros, erro);
    }

    public static void adicionarErro(List<ErrorMessage> erros, ErrorMessage erro, boolean condicao) {
        ErrorMessageUtil.adicionarErro(erros, erro, condicao);
    }

    public static void adicionarErro(List<ErrorMessage> erros, String chaveErro, boolean condicao) {
        ErrorMessageUtil.adicionarErro(erros, chaveErro, condicao);
    }

    public static void adicionarErro(List<ErrorMessage> erros, String chaveErro) {
        ErrorMessageUtil.adicionarErro(erros, chaveErro);
    }

    public static void adicionarErros(List<ErrorMessage> erros, List<ErrorMessage> novosErros) {
        ErrorMessageUtil.adicionarErros(erros, novosErros);
    }

}