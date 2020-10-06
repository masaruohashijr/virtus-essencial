package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum SubstitutoEventualEnum implements EnumeracaoComCodigoStringDescricao {

    NUNCA("1", "Nunca"), // o substituto eventual nunca tem o mesmo acesso do titular da fun��o.
    QUANDO_EM_EXERCICIO("2", "Quando em exerc�cio"), // o substituto eventual quando em exerc�cio tem o mesmo acesso do titular da fun��o.
    SEMPRE("3", "Sempre"); // o substituto eventual tem o mesmo acesso do titular da fun��o.

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
