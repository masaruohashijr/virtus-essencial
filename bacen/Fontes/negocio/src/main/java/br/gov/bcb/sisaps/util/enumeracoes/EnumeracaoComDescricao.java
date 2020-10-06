/*
 * Sistema APS.
 * EnumeracaoComDescricao.java
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil. Não é permitida sua
 * distribuição ou divulgação do seu conteúdo sem expressa autorização do Banco Central. Este
 * arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.enumeracoes;

import java.io.Serializable;

public interface EnumeracaoComDescricao extends Serializable {

    String getDescricao();
}
