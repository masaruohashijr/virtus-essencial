/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.stubs.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.adaptadores.data.DataAtualProvider;

@Component(value = DataAtualProvider.NOME)
public class DataAtualStub implements DataAtualProvider, Stub {

    private LocalDate dataAtual = new LocalDate();
    private DateTime dateTime;

    public void setDataAtual(LocalDate dataAtual) {
        this.dataAtual = dataAtual;
    }

    @Override
    public LocalDate get() {
        return dataAtual;
    }

    @Override
    public void limpar() {
        dataAtual = null;
        dateTime = null;
    }

    @Override
    public DateTime getDateTime() {
        if (dateTime == null) {
            return new DateTime();
        }
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}