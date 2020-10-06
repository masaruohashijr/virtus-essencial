package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoConta implements EnumeracaoComCodigoDescricao<Integer> {
    ATIVO(1, "Ativo"), PASSIVO(2, "Passivo");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta";
    private Integer codigo;
    private String descricao;

    private TipoConta(Integer codigo, String descricao) {
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

}
