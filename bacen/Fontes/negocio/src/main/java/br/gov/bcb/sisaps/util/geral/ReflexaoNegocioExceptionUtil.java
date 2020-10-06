/*
 * Sistema de Registro e controle de processos administrativos punitivos.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

public class ReflexaoNegocioExceptionUtil {
    
    private static final String CONSTRUTOR_NAO_DEFINIDO_NA_CLASSE =
            "Construtor especificado n�o faz parte da defini��o da classe.";

    public static <T> T instanciarObjetoPorReflexao(Class<T> classe) {
        T retorno = null;
        try {
            if (classe != null) {
                retorno = BeanUtils.instantiateClass(classe);
            }
        } catch (BeanInstantiationException e) {
            SisapsUtil.lancarNegocioException(CONSTRUTOR_NAO_DEFINIDO_NA_CLASSE, e, true);
        }
        return retorno;
    }

    public static <T> T instanciarObjetoPorReflexao(Class<T> classe, Class<?>[] tiposParametros,
            Object[] valoresParametros) {
        T retorno = null;
        try {
            if (classe != null) {
                Constructor<T> construtor = classe.getConstructor(tiposParametros);
                retorno = BeanUtils.instantiateClass(construtor, valoresParametros);
            }
        } catch (BeanInstantiationException e) {
            SisapsUtil.lancarNegocioException(CONSTRUTOR_NAO_DEFINIDO_NA_CLASSE, e, true);
        } catch (NoSuchMethodException e) {
            SisapsUtil.lancarNegocioException(CONSTRUTOR_NAO_DEFINIDO_NA_CLASSE, e, true);
        }
        return retorno;
    }
}