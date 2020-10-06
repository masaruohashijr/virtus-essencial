package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoEdicaoInformacaoEnum implements EnumeracaoComCodigoDescricao<Integer> {
    AJUSTE(1, "Ajuste"), AJUSTADO(2, "Ajustado");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum";
    private Integer codigo;
    private String descricao;

    private TipoEdicaoInformacaoEnum(Integer codigo, String descricao) {
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
