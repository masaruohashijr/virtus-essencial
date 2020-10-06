package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum SubstitutoEventualEnum implements EnumeracaoComCodigoStringDescricao {

    NUNCA("1", "Nunca"), // o substituto eventual nunca tem o mesmo acesso do titular da função.
    QUANDO_EM_EXERCICIO("2", "Quando em exercício"), // o substituto eventual quando em exercício tem o mesmo acesso do titular da função.
    SEMPRE("3", "Sempre"); // o substituto eventual tem o mesmo acesso do titular da função.

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum";

    private String codigo;
    private String descricao;

    private SubstitutoEventualEnum(String codigo, String descricao) {
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

    public static SubstitutoEventualEnum valueOfCodigo(String codigo) {
        for (SubstitutoEventualEnum e : SubstitutoEventualEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static SubstitutoEventualEnum valueOfDescricao(String descricao) {
        for (SubstitutoEventualEnum e : SubstitutoEventualEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
