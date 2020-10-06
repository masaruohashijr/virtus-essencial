/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.util.List;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class ErrorMessageUtil {
    
    private static final String INFORME_ERROS = "informe a lista de erros.";

    public static void adicionarErro(List<ErrorMessage> erros, ErrorMessage erro) {
        if (erros == null) {
            throw new IllegalArgumentException(INFORME_ERROS);
        }
        
        if (erro != null) {
            erros.add(erro);
        }
    }
    
    public static void adicionarErro(List<ErrorMessage> erros, ErrorMessage erro, boolean condicao) {
        if (condicao) {
            adicionarErro(erros, erro);
        }
    }


    public static void adicionarErro(List<ErrorMessage> erros, String chaveErro, boolean condicao) {
        if (condicao) {
            adicionarErro(erros, chaveErro);
        }
    }

    public static void adicionarErro(List<ErrorMessage> erros, String chaveErro) {
        if (chaveErro != null) {
            adicionarErro(erros, new ErrorMessage(chaveErro));
        }
    }

    public static void adicionarErros(List<ErrorMessage> erros, List<ErrorMessage> novosErros) {
        if (erros == null) {
            throw new IllegalArgumentException(INFORME_ERROS);
        }

        if (novosErros != null) {
            for (ErrorMessage erro : novosErros) {
                adicionarErro(erros, erro);
            }
        }
    }
}
