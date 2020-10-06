package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoInformacaoEnum implements EnumeracaoComCodigoDescricao<Integer> {
    PATRIMONIO(1, "Patrimônio"), INDICE(2, "Índice"), RESULTADO(3, "Resultado");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum";
    private Integer codigo;
    private String descricao;

    private TipoInformacaoEnum(Integer codigo, String descricao) {
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
