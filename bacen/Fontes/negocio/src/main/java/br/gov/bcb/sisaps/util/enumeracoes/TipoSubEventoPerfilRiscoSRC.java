package br.gov.bcb.sisaps.util.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum TipoSubEventoPerfilRiscoSRC implements EnumeracaoComCodigoDescricao<Integer> {

    CONCLUSAO(1, "Conclusão", ""),
    GRAU_PREOCUPACAO(2, "Grau de preocupação", ""),
    VERSAO_COREC(3, "Versão Corec", ""),
    MATRIZ(4, "Matriz", ""),
    PERFIL_ATUACAO(5, "Perfil de atuação", ""),
    PERSPECTIVA(6, "Perspectiva", ""),
    SITUACAO(7, "Situação", ""),
    SINTESE_RISCO(8, "Síntese de risco", ""),
    NOTA_MATRIZ(9, "Nota da matriz", ""),
    SINTESE_ANEF(10, "Síntese do ANEF", ""),
    PESO_ANEF(11, "Peso do ANEF", ""),
    NOTA_GERAL_ANEF(12, "Nota geral do ANEF", ""),
    QUADRO_POSICAO_FINANCEIRA(13, "Quadro da posição financeira", ""),
    PRIORIDADE_ES(14, "Prioridade da ES", "");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC";
    private Integer codigo;
    private String descricao;
    private String textoEvento;

    private TipoSubEventoPerfilRiscoSRC(Integer codigo, String descricao, String textoEvento) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.textoEvento = textoEvento;
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

    public String getTextoEvento() {
        return textoEvento;
    }

    public void setTextoEvento(String textoEvento) {
        this.textoEvento = textoEvento;
    }

    public static TipoSubEventoPerfilRiscoSRC valueOfCodigo(String codigo) {
        for (TipoSubEventoPerfilRiscoSRC e : TipoSubEventoPerfilRiscoSRC.values()) {
            if (e.getCodigo().equals(Integer.valueOf(codigo))) {
                return e;
            }
        }
        return null;
    }

    public static TipoSubEventoPerfilRiscoSRC valueOfDescricao(String descricao) {
        for (TipoSubEventoPerfilRiscoSRC e : TipoSubEventoPerfilRiscoSRC.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}