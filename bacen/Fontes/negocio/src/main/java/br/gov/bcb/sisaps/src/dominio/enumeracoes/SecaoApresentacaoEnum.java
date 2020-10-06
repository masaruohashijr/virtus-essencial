package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;

public enum SecaoApresentacaoEnum implements EnumeracaoComCodigoDescricao<Integer> {

    SRC(1, "SRC - Sistema de Avalia��o de Riscos e Controles"),
    EVOLUCAO_DAS_AVALIACOES(2, "Evolu��o das avalia��es"),
    TRABALHOS_REALIZADOS(3, "Trabalhos realizados no ciclo"),
    PERFIL(4, "Perfil"),
    ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL(5, "Estrutura jur�dica, societ�ria e organizacional"),
    GRUPO_ECONOMICO(6, "Grupo econ�mico"),
    ORGANOGRAMA_FUNCIONAL(7, "Organograma funcional"),
    INFORMACOES_OUTROS_DEPARTAMANETOS(8, "Informa��es de outros departamentos"),
    INFORMACOES_OUTROS_ORGAOS(9, "Informa��es de outros org�os"),
    ESTRATEGIAS(10, "Estrat�gias"),
    SITUACAO(11, "Situa��o"),
    ANALISE_QUANTITATIVA(12, "An�lise Econ�mico-Financeira"),
    POSICAO_FINANCEIRA_RESULTADOS(13, "Posi��o financeira e resultados"),
    SINTESE_NOTA_QUANTITATIVA(14, "S�ntese da an�lise econ�mico-financeira"),
    ANEXO_ANALISE_ECONOMICO_FINANCEIRA(32, "An�lise econ�mico-financeira"),
    NOTAS_QUANTITATIVAS_EVOLUCAO(15, "Evolu��o da an�lise econ�mico-financeira"),
    VOTACAO_NOTA_QUANTITATIVA(16, "Vota��o da Nota da An�lise Econ�mico-Financeira"),
    ANALISE_QUALITATIVA(17, "An�lise de Riscos e Controles"),
    IDENTIFICACAO_UNIDADES_ATIVIDADES(18, "Identifica��o das unidades/atividades"),
    CARACTERISTICAS_UNIDADES_ATIVIDADES(19, "Caracter�sticas das unidades/atividades"),
    ANALISE_QUALITATIVA_POR_RISCOS(20, "S�ntese da An�lise de Riscos e Controles"),
    NOTAS_ARCS(21, "Notas dos ARCs"),
    NOTAS_QUALITATIVAS_EVOLUCAO(22, "Evolu��o da An�lise de Riscos e Controles"),
    VOTACAO_NOTA_QUALITATIVA(23, "Vota��o da Nota da An�lise de Riscos e Controles"),
    CONCLUSAO(24, "Conclus�o"),
    NOTA_FINAL_INSTITUICAO(25, "Conclus�o sobre a institui��o"),
    VOTACAO_NOTA_FINAL(26, "Vota��o da Nota Final"),
    PERSPECTIVA_INSTITUICAO(27, "Perspectiva Sobre a Institui��o"),
    VOTACAO_PERSPECTIVA(28, "Vota��o da Perspectiva"),
    PROPOSTA_ACOES_CICLO(29, "Outras proposi��es"),
    VOTACAO_PROPOSTA_ACOES(30, "Vota��o de Outras Proposi��es"),
    EQUIPE(31, "Equipe");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum";

    // C�digo
    private Integer codigo;

    // Descri��o
    private String descricao;

    // Os campos da se��o.
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

            // Recupera os campos da se��o.
            for (CampoApresentacaoEnum campo : CampoApresentacaoEnum.values()) {
                // Verifica se � campo da se��o.
                if (campo.getSecao() == this) {
                    campos.add(campo);
                }
            }
        }

        return campos;
    }

}
