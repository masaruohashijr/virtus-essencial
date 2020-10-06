package br.gov.bcb.sisaps.util.enumeracoes;

import br.gov.bcb.dominio.stuff.enumeracao.EnumComCodigo;

public interface EnumComCodigoDescricao<T> extends EnumComCodigo<T> {

    String getDescricao();
}