package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum EstadoCicloEnum implements EnumeracaoComCodigoStringDescricao {

    EM_ANDAMENTO("2", "Em andamento"),
    COREC("1", "Corec"),
    POS_COREC("5", "Pós-corec"),
    ENCERRADO("3", "Encerrado"),
    EXCLUIDO("4", "Excluído");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum";

    private String codigo;
    private String descricao;

    private EstadoCicloEnum(String codigo, String descricao) {
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
        for (EstadoCicloEnum e : EstadoCicloEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static EstadoCicloEnum valueOfDescricao(String descricao) {
        for (EstadoCicloEnum e : EstadoCicloEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
