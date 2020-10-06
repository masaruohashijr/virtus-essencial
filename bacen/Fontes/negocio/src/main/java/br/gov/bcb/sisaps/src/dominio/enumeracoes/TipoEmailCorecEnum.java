package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoEmailCorecEnum implements EnumeracaoComCodigoDescricao<Integer> {

    APRESENTACAO(1, "Apresentação"),
    DISPONIBILIDADE(2, "Disponibilidade"),
    SOLICITACAO_ASSINATURA(3, "Solicitação assinatura");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum";
    private Integer codigo;
    private String descricao;

    private TipoEmailCorecEnum(Integer codigo, String descricao) {
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

    public static TipoEmailCorecEnum valueOfCodigo(String codigo) {
        for (TipoEmailCorecEnum e : TipoEmailCorecEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TipoEmailCorecEnum valueOfDescricao(String descricao) {
        for (TipoEmailCorecEnum e : TipoEmailCorecEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }



}
