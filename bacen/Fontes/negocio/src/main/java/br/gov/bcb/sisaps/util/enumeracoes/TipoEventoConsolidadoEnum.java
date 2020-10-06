package br.gov.bcb.sisaps.util.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;


public enum TipoEventoConsolidadoEnum implements EnumeracaoComCodigoDescricao<Integer> {

    INFORMACAO_ADICIONAL(1, "Informação adicional"),
    OPINIAO_CONCLUSIVA(2, "Opinião conclusiva"),
    PERFIL_ATUACAO(3, "Perfil de atuação"),
    PERFIL_DE_RISCO(4, "Perfil de risco"),
    PRESTACAO_INFORMACOES(5, "Prestação de informações"),
    COMENTARIO(6, "Comentarios"),
    TERMO_COMPARECIMENTO(7, "Item Termo Comparecimento"),
    ITEM_LAVRATURA(8, "Lavratura"),
    ITEM_RELATO(9, "Relato"),
    ITEM_ENCERRAMENTO(10, "Encerramento"),
    COMENTARIO_MIGRADO(11, "Comentário migrado");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.util.enumeracoes.TipoEventoConsolidadoEnum";
    private Integer codigo;
    private String descricao;

    private TipoEventoConsolidadoEnum(Integer codigo, String descricao) {
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

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoEventoConsolidadoEnum valueOfCodigo(String codigo) {
        for (TipoEventoConsolidadoEnum e : TipoEventoConsolidadoEnum.values()) {
            if (e.getCodigo().equals(Integer.valueOf(codigo))) {
                return e;
            }
        }
        return null;
    }

    public static TipoEventoConsolidadoEnum valueOfDescricao(String descricao) {
        for (TipoEventoConsolidadoEnum e : TipoEventoConsolidadoEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}