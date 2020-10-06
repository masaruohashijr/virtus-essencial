package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoParametroGrupoRiscoControleEnum implements EnumeracaoComCodigoDescricao<Integer> {

    MATRIZ(1, "MT"),
    EXTERNO(2, "EX");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum";
    private Integer codigo;
    private String descricao;

    private TipoParametroGrupoRiscoControleEnum(Integer codigo, String descricao) {
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

    public static TipoParametroGrupoRiscoControleEnum valueOfCodigo(String codigo) {
        for (TipoParametroGrupoRiscoControleEnum e : TipoParametroGrupoRiscoControleEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TipoParametroGrupoRiscoControleEnum valueOfDescricao(String descricao) {
        for (TipoParametroGrupoRiscoControleEnum e : TipoParametroGrupoRiscoControleEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }



}
