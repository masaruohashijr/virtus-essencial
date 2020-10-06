/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import java.io.Serializable;

import br.gov.bcb.dominio.stuff.enumeracao.EnumComCodigo;

public interface EnumeracaoComCodigoDescricao<T>  extends EnumComCodigo<T>, Serializable {

    String getDescricao();
}

