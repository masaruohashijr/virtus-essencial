package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum SecaoApresentacaoEnum implements EnumeracaoComCodigoDescricao<Integer> {

    SRC(1, "SRC - Sistema de Avaliação de Riscos e Controles"),
    EVOLUCAO_DAS_AVALIACOES(2, "Evolução das avaliações"),
    TRABALHOS_REALIZADOS(3, "Trabalhos realizados no ciclo"),
    PERFIL(4, "Perfil"),
    ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL(5, "Estrutura jurídica, societária e organizacional"),
    GRUPO_ECONOMICO(6, "Grupo econômico"),
    ORGANOGRAMA_FUNCIONAL(7, "Organograma funcional"),
    INFORMACOES_OUTROS_DEPARTAMANETOS(8, "Informações de outros departamentos"),
    INFORMACOES_OUTROS_ORGAOS(9, "Informações de outros orgãos"),
    ESTRATEGIAS(10, "Estratégias"),
    SITUACAO(11, "Situação"),
    ANALISE_QUANTITATIVA(12, "Análise Econômico-Financeira"),
    POSICAO_FINANCEIRA_RESULTADOS(13, "Posição financeira e resultados"),
    SINTESE_NOTA_QUANTITATIVA(14, "Síntese da análise econômico-financeira"),
    ANEXO_ANALISE_ECONOMICO_FINANCEIRA(32, "Análise econômico-financeira"),
    NOTAS_QUANTITATIVAS_EVOLUCAO(15, "Evolução da análise econômico-financeira"),
    VOTACAO_NOTA_QUANTITATIVA(16, "Votação da Nota da Análise Econômico-Financeira"),
    ANALISE_QUALITATIVA(17, "Análise de Riscos e Controles"),
    IDENTIFICACAO_UNIDADES_ATIVIDADES(18, "Identificação das unidades/atividades"),
    CARACTERISTICAS_UNIDADES_ATIVIDADES(19, "Características das unidades/atividades"),
    ANALISE_QUALITATIVA_POR_RISCOS(20, "Síntese da Análise de Riscos e Controles"),
    NOTAS_ARCS(21, "Notas dos ARCs"),
    NOTAS_QUALITATIVAS_EVOLUCAO(22, "Evolução da Análise de Riscos e Controles"),
    VOTACAO_NOTA_QUALITATIVA(23, "Votação da Nota da Análise de Riscos e Controles"),
    CONCLUSAO(24, "Conclusão"),
    NOTA_FINAL_INSTITUICAO(25, "Conclusão sobre a instituição"),
    VOTACAO_NOTA_FINAL(26, "Votação da Nota Final"),
    PERSPECTIVA_INSTITUICAO(27, "Perspectiva Sobre a Instituição"),
    VOTACAO_PERSPECTIVA(28, "Votação da Perspectiva"),
    PROPOSTA_ACOES_CICLO(29, "Outras proposições"),
    VOTACAO_PROPOSTA_ACOES(30, "Votação de Outras Proposições"),
    EQUIPE(31, "Equipe");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum";

    // Código
    private Integer codigo;

    // Descrição
    private String descricao;

    // Os campos da seção.
    private List<CampoApresentacaoEnum> campos;

    // Construtor
    private SecaoApresentacaoEnum(int codigo, String descricao) {
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

    public List<CampoApresentacaoEnum> getCampos() {
        // Valida os campos.
        if (campos == null) {
            // Prepara os campos.
            campos = new LinkedList<CampoApresentacaoEnum>();

            // Recupera os campos da seção.
            for (CampoApresentacaoEnum campo : CampoApresentacaoEnum.values()) {
                // Verifica se é campo da seção.
                if (campo.getSecao() == this) {
                    campos.add(campo);
                }
            }
        }

        return campos;
    }

}
