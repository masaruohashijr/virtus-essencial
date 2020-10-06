package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TituloTelaARCEnum implements EnumeracaoComCodigoDescricao<Integer> {

    PAGINA_DETALHE_CONCLUIDO(1, "Detalhes do ARC"),
    PAGINA_DETALHE_PREVISTO_DESIGNADO(2, "Detalhes do ARC"),
    PAGINA_DETALHE_PREVISTO_DESIGNADO_BRANCO(12, "Detalhes do ARC"),
    PAGINA_DETALHE_PREVISTO_BRANCO(13, "Detalhes do ARC"),
    PAGINA_DETALHE_EDICAO(3, "Detalhes do ARC"),
    PAGINA_DETALHE_DELEGADO(4, "Detalhes do ARC"),
    PAGINA_DETALHE_ANALISE(5, "Detalhes do ARC"),
    PAGINA_DETALHE_ANALISADO(6, "Detalhes do ARC"),
    PAGINA_DETALHE_PREENCHIDO(14, "Detalhes do ARC"),
    PAGINA_EDICAO(9, "Edição do ARC"),
    PAGINA_ANALISE(10, "Análise de ARC"),
    PAGINA_CONSULTA(11, "Consulta de ARCs");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum";
    private Integer codigo;
    private String descricao;

    private TituloTelaARCEnum(Integer codigo, String descricao) {
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

    public static TituloTelaARCEnum valueOfCodigo(String codigo) {
        for (TituloTelaARCEnum e : TituloTelaARCEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TituloTelaARCEnum valueOfDescricao(String descricao) {
        for (TituloTelaARCEnum e : TituloTelaARCEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }



}
