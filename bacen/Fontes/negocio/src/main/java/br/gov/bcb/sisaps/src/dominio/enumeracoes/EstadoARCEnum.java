package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum EstadoARCEnum implements EnumeracaoComCodigoDescricao<Integer> {

    PREVISTO(5, "Previsto"),
    CONCLUIDO(7, "Concluído"),
    DESIGNADO(2, "Designado"),
    EM_EDICAO(3, "Em edição"),
    PREENCHIDO(4, "Preenchido"),
    ANALISE_DELEGADA(1, "Análise delegada"),
    ANALISADO(8, "Analisado"),
    EM_ANALISE(6, "Em análise");

    public static final String CLASS_NAME =
 "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum";
    private Integer codigo;
    private String descricao;

    private EstadoARCEnum(Integer codigo, String descricao) {
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

    public static EstadoARCEnum valueOfCodigo(String codigo) {
        for (EstadoARCEnum e : EstadoARCEnum.values()) {
            if (e.getCodigo().toString().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static EstadoARCEnum valueOfDescricao(String descricao) {
        for (EstadoARCEnum e : EstadoARCEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

    public static List<EstadoARCEnum> listaEstados() {
        return Arrays.asList(EstadoARCEnum.values());
    }

    public static List<EstadoARCEnum> listaEstadosPrevistoDesignado() {
        return Arrays.asList(PREVISTO, DESIGNADO);
    }

    public static List<EstadoARCEnum> listaEstadosPreenchidoDelegadoAnalise() {
        return Arrays.asList(PREENCHIDO, ANALISE_DELEGADA, EM_ANALISE, ANALISADO);
    }

    public static List<EstadoARCEnum> listaEstadosPreenchidoAnaliseConcluido() {
        return Arrays.asList(PREENCHIDO, EM_ANALISE, CONCLUIDO, ANALISADO, ANALISE_DELEGADA);
    }

    public static List<EstadoARCEnum> listaEstadosDelegadoAnalise() {
        return Arrays.asList(ANALISE_DELEGADA, EM_ANALISE);
    }

    public static List<EstadoARCEnum> listaEstadosPrevistoDesignadoEdicao() {
        return Arrays.asList(PREVISTO, DESIGNADO, EM_EDICAO);
    }

    public static List<EstadoARCEnum> listaEstadosDesignadoEdicao() {
        return Arrays.asList(DESIGNADO, EM_EDICAO);
    }

    public static List<EstadoARCEnum> listaEstadosPrevistoPreenchidoAnalisado() {
        return Arrays.asList(PREVISTO, PREENCHIDO, ANALISADO);
    }

    public static List<EstadoARCEnum> listaEstadosPreenchidoAnalisado() {
        return Arrays.asList(PREENCHIDO, ANALISADO);
    }
}
