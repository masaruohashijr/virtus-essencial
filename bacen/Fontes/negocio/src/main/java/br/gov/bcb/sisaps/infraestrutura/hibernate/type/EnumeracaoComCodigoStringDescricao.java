/*
 * Sistema APS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software ? confidencial e propriedade do Banco Central do Brasil.
 * N?o ? permitida sua distribui??o ou divulga??o do seu conte?do sem
 * expressa autoriza??o do Banco Central.
 * Este arquivo cont?m informa??es propriet?rias.
 */
package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

import br.gov.bcb.dominio.stuff.enumeracao.EnumComCodigo;

public interface EnumeracaoComCodigoStringDescricao extends EnumComCodigo<String> {

    String getDescricao();
}
