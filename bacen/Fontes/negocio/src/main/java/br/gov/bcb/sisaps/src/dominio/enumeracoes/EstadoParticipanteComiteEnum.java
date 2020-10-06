package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum EstadoParticipanteComiteEnum implements EnumeracaoComCodigoDescricao<Integer> {

    PENDENTE(1, "Pendente"), PROCESSADO(2, "Processado");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoParticipantesComiteEnum";

    private Integer codigo;
    private String descricao;

    private EstadoParticipanteComiteEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }

}
