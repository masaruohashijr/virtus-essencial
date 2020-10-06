/*
 * Sistema APS.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.validacao;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.gov.bcb.sisaps.util.Constantes;

public class NegocioException extends RuntimeException {

    private static final String CHAVE_DIREITA = "}";
    private static final String KEY = "${key:";
    private final List<ErrorMessage> mensagens = new LinkedList<ErrorMessage>();

    public NegocioException(final ErrorMessage errorMessage, final Throwable cause) {
        super(errorMessage.getMessage(), cause);
        mensagens.add(errorMessage);
    }

    public NegocioException(final String chave, final Throwable cause) {
        super(null, cause);

        if (chave == null) {
            throw new IllegalArgumentException("A chave não pode ser nula!");
        }
        adicionarErro(chave, null);
    }

    public NegocioException(final List<ErrorMessage> mensagens) {
        super(null, null);
        this.mensagens.addAll(mensagens);
    }

    public NegocioException(final String chave, final String... parametros) {
        super(null, null);
        Map<String, String> param = new HashMap<String, String>();
        for (int i = 0; i < parametros.length; i++) {
            param.put(String.valueOf(i), parametros[i]);
        }
        adicionarErro(chave, param);
    }

    public List<ErrorMessage> getMensagens() {
        return mensagens;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null && !mensagens.isEmpty()) {
            message = mensagens.toString();
        }
        return message;
    }

    private void adicionarErro(final String chave, final Map<String, String> parametros) {
        mensagens.add(new ErrorMessage(chave, parametros));
    }
    
    
    public static NegocioException criarExcecaoConcorrencia() {
        return new NegocioException(Constantes.ERRO_CONCORRENCIA);
    }
    
    
    
    public static ErrorMessage criarErrorMessage(String chaveErro, String... parametros) {
        Map<String, String> mapaParametros = new HashMap<String, String>();
        for (int i = 0; i < parametros.length; i++) {
            String parametro = parametros[i];
            String sufixoLabel = "";
            if (i > 0) {
                sufixoLabel = "" + (i + 1);
            }
            mapaParametros.put("label" + sufixoLabel, "${label:" + parametro + CHAVE_DIREITA);

        }

        return new ErrorMessage(KEY + chaveErro + CHAVE_DIREITA, mapaParametros);
    }
}