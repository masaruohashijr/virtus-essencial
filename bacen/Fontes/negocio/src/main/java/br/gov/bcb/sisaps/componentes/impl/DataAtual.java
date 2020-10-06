/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
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
