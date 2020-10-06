/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class SisapsValidacaoUtilHelper extends SisapsUtilSuporte {

    public static boolean isNaoNuloOuVazio(Object... objects) {
        boolean isNaoNuloOuVazio = true;
        for (Object object : objects) {
            if (SisapsValidacaoUtil.isNuloOuVazio(object)) {
                isNaoNuloOuVazio = false;
                break;
            }
        }
        return isNaoNuloOuVazio;
    }

    public static boolean isAlgumNaoNuloOuVazio(Object... objects) {
        boolean isNaoNuloOuVazio = false;
        for (Object object : objects) {
            if (SisapsValidacaoUtil.isNaoNuloOuVazio(object)) {
                isNaoNuloOuVazio = true;
                break;
            }
        }
        return isNaoNuloOuVazio;
    }

    @SuppressWarnings(RAWTYPES)
    public static boolean isNuloOuVazio(Object object) {
        boolean resultado = object == null;
        if (!resultado) {
            if (object instanceof String) {
                String temp = (String) object;
                resultado = StringUtils.isEmpty(temp);
            }
            if (object instanceof Collection) {
                Collection temp = (Collection) object;
                resultado = temp.isEmpty();
            }

            if (object.getClass().isArray()) {
                resultado = ArrayUtils.getLength(object) == 0;
            }
        }
        return resultado;
    }
}
