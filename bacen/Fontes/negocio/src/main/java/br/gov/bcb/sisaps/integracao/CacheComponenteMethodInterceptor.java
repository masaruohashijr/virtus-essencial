/*
 * Sistema APS.
 * 
 * Copyright (c) Banco Central do Brasil.
 * 
 * Este software � confidencial e propriedade do Banco Central do Brasil. N�o � permitida sua
 * distribui��o ou divulga��o do seu conte�do sem expressa autoriza��o do Banco Central. Este
 * arquivo cont�m informa��es propriet�rias.
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
    // CHECKSTYLE:OFF Lan�ar Throwable faz parte da assinatura do m�todo
            Throwable {
        // CHECKSTYLE:ON
        String targetName = invocation.getThis().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Object[] arguments = invocation.getArguments();

        LocalDate hoje = new LocalDate();
        if (tempoCache.isBefore(hoje)) { // se virou o dia ent�o o cache deve
            tempoCache = hoje;
            cache.clear();
        }

        LOGGER.debug("Procurando pelo m�todo no cache...");

        String chave = getChave(targetName, methodName, arguments);
        Serializable retorno = cache.get(chave);

        if (retorno == null) {
            LOGGER.debug("M�todo n�o est� no cache! Invocando o m�todo ent�o...");
            retorno = (Serializable) invocation.proceed();

            cache.put(chave, retorno);
            LOGGER.debug("Armazenado o retorno do m�todo invocado!");
        } else {
            LOGGER.debug("M�todo encontrado no cache!");
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
