package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TituloTelaAnefEnum implements EnumeracaoComCodigoDescricao<Integer> {

    PAGINA_DETALHE(5, "Detalhar ANEF"),
    PAGINA_EDICAO(7, "Editar ANEF"),
    PAGINA_ANALISE(2, "Análise de ANEF"),
    PAGINA_CONSULTA(3, "Consulta de ANEF");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum";
    private Integer codigo;
    private String descricao;

    private TituloTelaAnefEnum(Integer codigo, String descricao) {
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

    public static TituloTelaAnefEnum valueOfCodigo(String codigo) {
        for (TituloTelaAnefEnum e : TituloTelaAnefEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TituloTelaAnefEnum valueOfDescricao(String descricao) {
        for (TituloTelaAnefEnum e : TituloTelaAnefEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }



}
