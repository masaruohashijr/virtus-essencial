package br.gov.bcb.sisaps.adaptadores.data;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public interface DataAtualProvider {

    String NOME = "dataAtual";

    LocalDate get();
    
    DateTime getDateTime();
}
