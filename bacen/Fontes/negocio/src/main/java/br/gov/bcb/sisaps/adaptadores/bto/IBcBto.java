/*
 * Sistema TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.adaptadores.bto;

import java.util.Date;
import java.util.Map;

public interface IBcBto {
    String NOME = "compBcBto";
    
    void agendarRotinaBatch(String jobName, String jobGroup, Date dataPedido, Map<String, String> parametros);

}
