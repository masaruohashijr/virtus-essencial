package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum SubstitutoPrazoCertoEnum implements EnumeracaoComCodigoStringDescricao {

    NUNCA("1", "Nunca"), // o substituto prazo certo nunca tem o mesmo acesso do titular da função.
    QUANDO_EM_EXERCICIO("2", "Quando em exercício"); // o substituto prazo certo quando em exercício tem o mesmo acesso do titular da função.

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoPrazoCertoEnum";

    private String codigo;
    private String descricao;

    private SubstitutoPrazoCertoEnum(String codigo, String descricao) {
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

    public static SubstitutoPrazoCertoEnum valueOfCodigo(String codigo) {
        for (SubstitutoPrazoCertoEnum e : SubstitutoPrazoCertoEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static SubstitutoPrazoCertoEnum valueOfDescricao(String descricao) {
        for (SubstitutoPrazoCertoEnum e : SubstitutoPrazoCertoEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
