/*
 * Sistema APS.
 * 
 * Copyright (c) Banco Central do Brasil.
 * 
 * Este software é confidencial e propriedade do Banco Central do Brasil. Não é permitida sua
 * distribuição ou divulgação do seu conteúdo sem expressa autorização do Banco Central. Este
 * arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.integracao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheComponenteMethodInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheComponenteMethodInterceptor.class);

    private LocalDate tempoCache;
    private Map<String, Serializable> cache;

    public CacheComponenteMethodInterceptor() {
        tempoCache = new LocalDate();
        cache = new HashMap<String, Serializable>();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws
    // CHECKSTYLE:OFF Lançar Throwable faz parte da assinatura do método
            Throwable {
        // CHECKSTYLE:ON
        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();

        LocalDate hoje = new LocalDate();
        if (tempoCache.isBefore(hoje)) { // se virou o dia então o cache deve
            tempoCache = hoje;
            cache.clear();
        }

        LOGGER.debug("Procurando pelo método no cache...");

        String chave = getChave(targetName, methodName, arguments);
        Serializable retorno = cache.get(chave);

        if (retorno == null) {
            LOGGER.debug("Método não está no cache! Invocando o método então...");
            retorno = (Serializable) invocation.proceed();

            cache.put(chave, retorno);
            LOGGER.debug("Armazenado o retorno do método invocado!");
        } else {
            LOGGER.debug("Método encontrado no cache!");
        }
        return retorno;
    }

    private String getChave(String targetName, String methodName, Object[] arguments) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(targetName).append('.').append(methodName);

        if (arguments != null && arguments.length != 0) {
            for (int i = 0; i < arguments.length; i++) {
                buffer.append('.').append(arguments[i]);
                LOGGER.debug("Valor do Argumento:" + arguments[i]);
            }
        }
        LOGGER.debug("Chave:" + buffer.toString());
        return buffer.toString();
    }
}
