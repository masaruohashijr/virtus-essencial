package br.gov.bcb.sisaps.util.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoStatusDocumento implements EnumeracaoComCodigoDescricao<Integer> {
    PUBLICADO(1, "Publicado"), EM_EDICAO(2, "Em edição"), EM_HOMOLOGACAO(3, "Em homologação");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.util.enumeracoes.TipoStatusDocumento";
    private Integer codigo;
    private String descricao;

    private TipoStatusDocumento(Integer codigo, String descricao) {
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

    public static TipoStatusDocumento valueOfCodigo(String codigo) {
        for (TipoStatusDocumento e : TipoStatusDocumento.values()) {
            if (e.getCodigo().equals(Integer.valueOf(codigo))) {
                return e;
            }
        }
        return null;
    }

    public static TipoStatusDocumento valueOfDescricao(String descricao) {
        for (TipoStatusDocumento e : TipoStatusDocumento.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}