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

public enum TipoUnidadeAtividadeEnum implements EnumeracaoComCodigoStringDescricao {

    CORPORATIVO("1", "Corporativa", "C"), NEGOCIO("2", "Negócio", "N");

    public static final String CLASS_NAME =
            "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum";
    private String codigo;
    private String descricao;
    private String sigla;

    private TipoUnidadeAtividadeEnum(String codigo, String descricao, String sigla) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.sigla = sigla;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
    
    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public static TipoUnidadeAtividadeEnum valueOfDescricao(String descricao) {
        for (TipoUnidadeAtividadeEnum e : values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
