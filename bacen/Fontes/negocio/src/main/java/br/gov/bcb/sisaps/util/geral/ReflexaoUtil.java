/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ReflectionUtils;

import br.gov.bcb.sisaps.util.validacao.InfraException;

public class ReflexaoUtil extends ReflexaoNegocioExceptionUtil {

    public static <T> T instanciarParametro(Class<?> classe) {
        Class<T> classeInstancia = getParameterizedType(classe);
        return instanciarObjetoPorReflexao(classeInstancia);
    }

    public static <T> Class<T> getParameterizedType(Class<?> classe) {
        return getParameterizedType(classe, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getParameterizedType(Class<?> classe, int index) {
        Type superClass = classe.getGenericSuperclass();
        while (!(superClass instanceof ParameterizedType)) {
            superClass = ((Class<T>) superClass).getGenericSuperclass();
        }
        return (Class<T>) ((ParameterizedType) superClass).getActualTypeArguments()[index];
    }

    public static <T> boolean hasField(final String field, final Class<T> clazz) {
        return ReflectionUtils.findField(clazz, field) != null;
    }

    @SuppressWarnings("unchecked")
    public static <E, R> R getValorCampo(E object, String name) {
        try {
            return (R) PropertyUtils.getProperty(object, name);
        } catch (IllegalAccessException e) {
            throw new InfraException(e);
        } catch (InvocationTargetException e) {
            throw new InfraException(e);
        } catch (NoSuchMethodException e) {
            throw new InfraException(e);
        }
    }
}