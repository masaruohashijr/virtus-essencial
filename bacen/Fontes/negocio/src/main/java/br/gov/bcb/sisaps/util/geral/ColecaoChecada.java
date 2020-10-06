/*
 * Sistema APS..
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class ColecaoChecada implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String RAWTYPES = "rawtypes";

    @SuppressWarnings(RAWTYPES)
    public static <T> List<T> checkedList(List lista, Class<T> classe) {
        return Collections.checkedList(lista, classe);
    }

}
