/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;
import br.gov.bcb.sisaps.util.geral.SisapsExcecaoUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public enum PerfisNotificacaoEnum  implements EnumeracaoComCodigoDescricao<Integer> {

    ADMINISTRADOR(6, "Administrador", "SRAT005"),
    ANALISTA_ECONOMICO_FINANCEIRO(12, "Analista econômico-financeiro", "SRAT006"),
    COMITE(7, "Comitê", "SRAT004"),
    CONSULTOR_METODOLOGIA(17, "Consultor de metodologia", "SRAT008"),
    CONSULTOR_TECNOLOGIA(8, "Consultor de tecnologia", SisapsExcecaoUtil.VAZIO),
    CONSULTOR_ESPECIALISTA(10, "Consultor especialista", "SRAT012"),
    COORDENADOR_CAMPO(14, "Coordenador de campo", SisapsExcecaoUtil.VAZIO),
    GERENTE_CONSULTOR_ESPECIALISTA(5, "Gerente consultor especialista", "SRAT011"),
    GERENTE_REVISOR(13, "Gerente do revisor", "SRAT009"),
    GERENTE_SUPERVISOR(16, "Gerente do supervisor", SisapsExcecaoUtil.VAZIO),
    GERENTE_EXECUTIVO(3, "Gerente executivo", "SRAT002"),
    GERENTE_TECNICO(9, "Gerente técnico", "SRAT003"),
    INSPETOR(2, "Inspetor", "SRAT013"),
    REVISOR(11, "Revisor", "SRAT010"),
    REVISOR_ECONOMICO_FINANCEIRO(4, "Revisor econômico-financeiro", "SRAT007"),
    SUBSTITUTO_SUPERVISOR(15, "Substituto do supervisor", SisapsExcecaoUtil.VAZIO),
    SUPERVISOR(1, "Supervisor", "SRAT001"),
    SISTEMA(0, "", "");

    public static final PerfisNotificacaoEnum[] TODOSSEMSISTEMA = new PerfisNotificacaoEnum[] {ADMINISTRADOR,
            ANALISTA_ECONOMICO_FINANCEIRO, COMITE, CONSULTOR_METODOLOGIA, CONSULTOR_TECNOLOGIA, CONSULTOR_ESPECIALISTA,
            COORDENADOR_CAMPO, GERENTE_CONSULTOR_ESPECIALISTA, GERENTE_REVISOR, GERENTE_SUPERVISOR, GERENTE_EXECUTIVO,
            GERENTE_TECNICO, INSPETOR, REVISOR, REVISOR_ECONOMICO_FINANCEIRO, SUBSTITUTO_SUPERVISOR, SUPERVISOR};

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum";
    private Integer codigo;
    private String descricao;
    private String transacao;

    private PerfisNotificacaoEnum(Integer codigo, String descricao, String transacao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.transacao = transacao;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getTransacao() {
        return transacao;
    }

    public static PerfisNotificacaoEnum valueOfDescricao(String descricao) {
        PerfisNotificacaoEnum perfil = null;
        for (PerfisNotificacaoEnum e : values()) {
            if (e.getDescricao().equalsIgnoreCase(descricao)) {
                perfil = e;
                break;
            }
        }
        return perfil;
    }

    public static PerfisNotificacaoEnum valueOfCodigo(String codigo) {
        PerfisNotificacaoEnum perfil = null;
        for (PerfisNotificacaoEnum e : values()) {
            if (e.getCodigo().toString().equals(codigo) && SisapsUtil.isNaoNuloOuVazio(codigo.trim())) {
                perfil = e;
                break;
            }
        }
        return perfil;
    }

    public static PerfisNotificacaoEnum valueOfTransacao(String transacao) {
        PerfisNotificacaoEnum perfil = null;
        for (PerfisNotificacaoEnum e : values()) {
            if (e.getTransacao().equals(transacao) && SisapsUtil.isNaoNuloOuVazio(transacao)) {
                perfil = e;
                break;
            }
        }
        return perfil;
    }

}
