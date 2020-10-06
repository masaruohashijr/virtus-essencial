package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;
import br.gov.bcb.sisaps.util.Util;

public enum CampoApresentacaoEnum implements EnumeracaoComCodigoDescricao<Integer> {

    EAV_ANEXO(SecaoApresentacaoEnum.EVOLUCAO_DAS_AVALIACOES, 1, "", CampoApresentacaoEnum.TC_ANEXO),

    PRF_SEGMENTO(SecaoApresentacaoEnum.PERFIL, 1, "Segmento", CampoApresentacaoEnum.TC_TEXTO),
    PRF_RANKING(SecaoApresentacaoEnum.PERFIL, 2, "Ranking", CampoApresentacaoEnum.TC_TEXTO),
    PRF_AUTORIZACOES(SecaoApresentacaoEnum.PERFIL, 3, "Autorizações", CampoApresentacaoEnum.TC_TEXTO),
    PRF_DEALER(SecaoApresentacaoEnum.PERFIL, 4, "Dealer", CampoApresentacaoEnum.TC_TEXTO),
    PRF_RATING(SecaoApresentacaoEnum.PERFIL, 5, "Rating", CampoApresentacaoEnum.TC_TEXTO_ANEXO),

    EJSO_ANEXO(
            SecaoApresentacaoEnum.ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL,
            1,
            "",
            CampoApresentacaoEnum.TC_ANEXO),

    GEC_ANEXO(SecaoApresentacaoEnum.GRUPO_ECONOMICO, 1, "", CampoApresentacaoEnum.TC_ANEXO),

    OFU_ANEXO(SecaoApresentacaoEnum.ORGANOGRAMA_FUNCIONAL, 1, "", CampoApresentacaoEnum.TC_ANEXO),

    IOD_DESIG(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 1, "Desig", CampoApresentacaoEnum.TC_TEXTO),
    IOD_DEBAN(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 2, "Deban", CampoApresentacaoEnum.TC_TEXTO),
    IOD_DECON(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 3, "Decon", CampoApresentacaoEnum.TC_TEXTO),
    IOD_DEATI(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 4, "Deati", CampoApresentacaoEnum.TC_TEXTO),
    IOD_DEORF(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 5, "Deorf", CampoApresentacaoEnum.TC_TEXTO),
    IOD_DESUC(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 6, "Desuc", CampoApresentacaoEnum.TC_TEXTO),
    IOD_OUTROS(SecaoApresentacaoEnum.INFORMACOES_OUTROS_DEPARTAMANETOS, 7, Util.OUTROS, CampoApresentacaoEnum.TC_TEXTO),

    IOO_CVM(SecaoApresentacaoEnum.INFORMACOES_OUTROS_ORGAOS, 1, "CVM", CampoApresentacaoEnum.TC_TEXTO),
    IOO_RECEITA_FEDERAL(
            SecaoApresentacaoEnum.INFORMACOES_OUTROS_ORGAOS,
            2,
            "Receita Federal",
            CampoApresentacaoEnum.TC_TEXTO),
    IOO_OUTROS(SecaoApresentacaoEnum.INFORMACOES_OUTROS_ORGAOS, 3,  Util.OUTROS, CampoApresentacaoEnum.TC_TEXTO),

    EST_ANEXO(SecaoApresentacaoEnum.ESTRATEGIAS, 1, "", CampoApresentacaoEnum.TC_TEXTO_ANEXO),

    NQT_ANEXO(SecaoApresentacaoEnum.NOTAS_QUANTITATIVAS_EVOLUCAO, 1, "", CampoApresentacaoEnum.TC_ANEXO),

    CUA_TEXTO(SecaoApresentacaoEnum.CARACTERISTICAS_UNIDADES_ATIVIDADES, 1, "", CampoApresentacaoEnum.TC_TEXTO_ANEXO),

    NQL_ANEXO(SecaoApresentacaoEnum.NOTAS_QUALITATIVAS_EVOLUCAO, 1, "", CampoApresentacaoEnum.TC_ANEXO),

    PAC_TEXTO(SecaoApresentacaoEnum.PROPOSTA_ACOES_CICLO, 1, "", CampoApresentacaoEnum.TC_TEXTO_ANEXO),

    EQP_TEXTO(SecaoApresentacaoEnum.EQUIPE, 1, "", CampoApresentacaoEnum.TC_TEXTO),
    AEF_ANEXO(SecaoApresentacaoEnum.ANEXO_ANALISE_ECONOMICO_FINANCEIRA, 1, "", CampoApresentacaoEnum.TC_ANEXO);

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum";

    // Tipos de campos.
    public static final int TC_ANEXO = 1;
    public static final int TC_TEXTO = 2;
    public static final int TC_TEXTO_ANEXO = 3;

    // Seção
    private SecaoApresentacaoEnum secao;

    // Id
    private Integer codigo;

    // Descrição
    private String descricao;

    // Tipo do campo.
    private int tipo;

    // Construtor
    private CampoApresentacaoEnum(SecaoApresentacaoEnum secao, Integer codigo, String descricao, int tipo) {
        // Inicializações
        this.secao = secao;
        this.codigo = 100 * secao.getCodigo() + codigo;
        this.descricao = descricao;
        this.tipo = tipo;
    }

    public SecaoApresentacaoEnum getSecao() {
        return secao;
    }

    @Override
    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public int getTipo() {
        return tipo;
    }

    public String getIdTag() {
        return this.secao.getCodigo() + "_" + this.getCodigo();
    }

}
