/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum NormalidadeEnum implements EnumeracaoComCodigoDescricao<Integer> {

    NORMAL(0, "Sim"), ANORMAL(1, "N�o");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum";

    private Integer codigo;
    private String descricao;

    private NormalidadeEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public static NormalidadeEnum getTipo(Boolean valor) {
        return (valor != null && valor.booleanValue()) ? NORMAL : ANORMAL;
    }

    public static NormalidadeEnum valueOfDescricao(String descricao) {
        NormalidadeEnum value = null;
        for (NormalidadeEnum e : values()) {
            if (e.getDescricao().equalsIgnoreCase(descricao)) {
                value = e;
                break;
            }
        }
        return value;
    }

    public boolean booleanValue() {
        return this.equals(NORMAL);
    }
}