/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum TipoLinhaMatrizVOEnum implements EnumeracaoComCodigoStringDescricao {

    UNIDADE("1", "U"), ATIVIDADE("2", "A");

    public static final String CLASS_NAME =
            "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaMatrizVOEnum";
    private String codigo;
    private String descricao;

    private TipoLinhaMatrizVOEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public static TipoLinhaMatrizVOEnum valueOfDescricao(String descricao) {
        for (TipoLinhaMatrizVOEnum e : values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
