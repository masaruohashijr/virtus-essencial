/*
 * Sistema TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.bto;

import java.util.Date;
import java.util.Map;

public interface IBcBto {
    String NOME = "compBcBto";
    
    void agendarRotinaBatch(String jobName, String jobGroup, Date dataPedido, Map<String, String> parametros);

}
