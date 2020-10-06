package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum EstadoExecucaoEnum implements EnumeracaoComCodigoDescricao<Integer> {

    AGENDADO(1, "Agendado"),
    CONCLUIDO(2, "Concluído"),
    EM_ANDAMENTO(3, "Em andamento"),
    FALHA(4, "Falha");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoExecucaoEnum";

    private Integer codigo;
    private String descricao;

    private EstadoExecucaoEnum(Integer codigo, String descricao) {
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

    public static EstadoExecucaoEnum valueOfDescricao(String descricao) {
        for (EstadoExecucaoEnum e : EstadoExecucaoEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
