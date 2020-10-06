/*
 * Sistema APS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.io.Serializable;

public interface EnumeracaoComCodigoCharDescricao extends Serializable {

    char getCodigo();

    String getDescricao();
}
