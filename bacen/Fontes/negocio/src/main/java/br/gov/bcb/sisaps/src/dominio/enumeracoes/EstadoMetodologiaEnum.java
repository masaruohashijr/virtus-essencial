package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum EstadoMetodologiaEnum implements EnumeracaoComCodigoStringDescricao {

    EM_ALTERACAO("2", "Em alteração"), CONCLUIDA("1", "Concluída");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMetodologiaEnum";

    private String codigo;
    private String descricao;

    private EstadoMetodologiaEnum(String codigo, String descricao) {
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

    public static EnumeracaoComCodigoStringDescricao valueOfCodigo(String codigo) {
        for (EstadoMetodologiaEnum e : EstadoMetodologiaEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static EstadoMetodologiaEnum valueOfDescricao(String descricao) {
        for (EstadoMetodologiaEnum e : EstadoMetodologiaEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
