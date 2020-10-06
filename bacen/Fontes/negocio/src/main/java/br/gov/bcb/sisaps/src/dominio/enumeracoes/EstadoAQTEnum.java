package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum EstadoAQTEnum implements EnumeracaoComCodigoDescricao<Integer> {

    PREVISTO(5, "Previsto"),
    CONCLUIDO(7, "Concluído"),
    DESIGNADO(2, "Designado"),
    EM_EDICAO(3, "Em edição"),
    PREENCHIDO(4, "Preenchido"),
    ANALISE_DELEGADA(1, "Análise delegada"),
    ANALISADO(8, "Analisado"),
    EM_ANALISE(6, "Em análise");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum";
    private Integer codigo;
    private String descricao;

    private EstadoAQTEnum(Integer codigo, String descricao) {
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

    public static EstadoAQTEnum valueOfCodigo(String codigo) {
        for (EstadoAQTEnum e : EstadoAQTEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static EstadoAQTEnum valueOfDescricao(String descricao) {
        for (EstadoAQTEnum e : EstadoAQTEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

    public static List<EstadoAQTEnum> listaEstadosPrevistoDesignado() {
        return Arrays.asList(PREVISTO, DESIGNADO);
    }

    public static List<EstadoAQTEnum> listaEstadosPreenchidoDelegadoAnalise() {
        return Arrays.asList(PREENCHIDO, ANALISE_DELEGADA, EM_ANALISE, ANALISADO);
    }

    public static List<EstadoAQTEnum> listaEstadosPreenchidoAnaliseConcluido() {
        return Arrays.asList(PREENCHIDO, EM_ANALISE, CONCLUIDO, ANALISADO);
    }

    public static List<EstadoAQTEnum> listaEstadosDelegadoAnalise() {
        return Arrays.asList(ANALISE_DELEGADA, EM_ANALISE);
    }

    public static List<EstadoAQTEnum> listaEstadosPrevistoDesignadoEdicao() {
        return Arrays.asList(PREVISTO, DESIGNADO, EM_EDICAO);
    }

    public static List<EstadoAQTEnum> listaEstadosDesignadoEdicao() {
        return Arrays.asList(DESIGNADO, EM_EDICAO);
    }

    public static List<EstadoAQTEnum> listaEstadosPrevistoPreenchidoAnalisado() {
        return Arrays.asList(PREVISTO, PREENCHIDO, ANALISADO);
    }

    public static List<EstadoAQTEnum> listaEstadosAnaliseSupervisor() {
        return Arrays.asList(ANALISE_DELEGADA, PREENCHIDO, EM_ANALISE);
    }

    public static List<EstadoAQTEnum> listaEstadosPreenchidoAnalisado() {
        return Arrays.asList(PREENCHIDO, ANALISADO);
    }

    public static List<EstadoAQTEnum> listaEstadosPreenchidoAnalisadoEmAnaliseConcluido() {
        return Arrays.asList(PREENCHIDO, ANALISADO, CONCLUIDO, EM_ANALISE, ANALISE_DELEGADA);
    }

}
