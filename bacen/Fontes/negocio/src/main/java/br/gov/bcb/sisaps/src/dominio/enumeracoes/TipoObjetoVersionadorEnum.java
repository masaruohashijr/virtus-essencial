package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum TipoObjetoVersionadorEnum implements EnumeracaoComCodigoStringDescricao {

    ARC("2", "Avaliação de risco e controle"), ESTADO_CICLO("3", "Estado do ciclo"), MATRIZ("4", "Matriz"),
    //NOTA_CICLO("5", "Notas do ciclo"),
    ENTIDADE_SUPERVISIONAVEL("6", "Entidade supervisionável"),
    SINTESE_RISCO("7", "Sintese de risco"),
    NOTA_MATRIZ("8", "Nota da matriz"),
    PERSPECTIVA_ES("9", "Perspectiva ES"),
    CONCLUSAO_ES("10", "Conclusão ES"),
    PERFIL_ATUACAO_ES("11", "Perfil de atuação ES"),
    GRAU_PREOCUPACAO_ES("12", "Grau de preocupação ES"),
    SITUACAO_ES("13", "Situação ES"),
    ATIVIDADE_CICLO("14", "Atividades ciclo"),
    QUADRO_POSISAO_FINANCEIRA("15", "Quadro da posição financeira"),
    ANEXO_CICLO("16", "Anexo de ciclo"),
    CELULA_RISCO_CONTROLE("17", "Célula de risco e controle da matriz"),
    AQT("18", "Analise quantitativa"),
    SINTESE_AQT("19", "Síntese do ANEF"),
    NOTA_AQT("20", "Nota geral do ANEF"),
    PESO_AQT("21", "Peso do ANEF");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum";

    private String codigo;
    private String descricao;

    private TipoObjetoVersionadorEnum(String codigo, String descricao) {
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
        for (TipoObjetoVersionadorEnum e : TipoObjetoVersionadorEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TipoObjetoVersionadorEnum valueOfDescricao(String descricao) {
        for (TipoObjetoVersionadorEnum e : TipoObjetoVersionadorEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
