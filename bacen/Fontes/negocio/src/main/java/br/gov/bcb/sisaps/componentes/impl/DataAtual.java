/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.componentes.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import br.gov.bcb.sisaps.adaptadores.data.DataAtualProvider;

@Component(value = DataAtualProvider.NOME)
public class DataAtual implements DataAtualProvider {

    public LocalDate get() {
        return new LocalDate();
    }

    @Override
    public DateTime getDateTime() {
        return new DateTime();
    }

}
