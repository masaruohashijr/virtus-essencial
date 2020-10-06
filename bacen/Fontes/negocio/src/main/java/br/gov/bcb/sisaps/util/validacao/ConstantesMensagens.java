//CHECKSTYLE:OFF -  Classe que contém todas as mensagens do sistema, justificando assim seu tamanho pela quantidade de casos de uso.
/*
 * Sistema APS
 * ConstantesMensagens.java
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
//CHECKSTYLE:ON
package br.gov.bcb.sisaps.util.validacao;

public class ConstantesMensagens {

    public static final String SELECAO_DE_ARC_E_OBRIGATORIA = "Seleção de ARC é obrigatória.";

    public static final String MSG_ERRO_GERAL_055 = "MSG_ERRO_GERAL_055";
    public static final String MSG_ERRO_GERAL_095 = "MSG_ERRO_GERAL_095";
    public static final String MSG_DATA_INVALIDA = "error.dataInvalida";
    public static final String MSG_ERRO_CONCORRENCIA = "Erro de acesso concorrente.";
    public static final String MSG_ERRO_CONSTRAINT = "Erro de violação de constraint na base ou modelo defasado.";
    public static final String MSG_APS_RATING_REFACA_PESQUISA = "MSG_APS_RATING_REFACA_PESQUISA";
    public static final String MSG_APS_RATING_CAMPO_INVALIDO = "MSG_APS_RATING_CAMPO_INVALIDO";
    public static final String MSG_APS_RATING_SELECAO_ITEM_LISTA_OBRIGATORIO =
            "MSG_APS_RATING_SELECAO_ITEM_LISTA_OBRIGATORIO";

    // Mensagens de erro CICLO
    public static final String MSG_APS_CICLO_ERRO_001 =
            "A data de início de ciclo deve ser menor ou igual à data corrente.";
    public static final String MSG_APS_CICLO_ERRO_003 = "ES não está sob responsabilidade do usuário.";
    public static final String MSG_APS_CICLO_ERRO_004 = "Entidade supervisionável já possui ciclo de supervisão.";
    public static final String MSG_APS_CICLO_ERRO_005 = "Não é possível usar a metodologia selecionada para esta ES.";

    // Mensagens de erro ATIVIDADE
    public static final String MSG_APS_ATIVIDADE_ERRO_001 =
            "Não pode existir mais de uma atividade com o mesmo nome na matriz.";

    public static final String MSG_APS_ATIVIDADE_ERRO_002 =
            "Uma atividade corporativa não pode ser do tipo de atividade de negócio.";

    public static final String MSG_APS_ATIVIDADE_ERRO_003 =
            "Uma atividade de negócio não pode ser do tipo de atividade corporativo.";

    public static final String MSG_APS_ATIVIDADE_ERRO_004 = "Não é possível a edição de uma matriz no estado vigente.";

    // Mensagens de erro UNIDADE
    public static final String MSG_APS_UNIDADE_ERRO_001 =
            "Não pode existir mais de uma unidade com o mesmo nome na matriz.";

    public static final String MSG_APS_UNIDADE_ERRO_003 = "Deve existir uma única unidade corporativa na matriz.";

    public static final String MSG_APS_UNIDADE_ERRO_004 =
            "Campo Fator de relevância deve ser maior do que zero (0) e menor ou igual a um (1).";

    // Mensagens de erro MATRIZ
    public static final String MSG_APS_MATRIZ_ERRO_001 = "Não é possível a liberação de uma matriz vigente.";
    public static final String MSG_APS_MATRIZ_ERRO_002 =
            "Não é possível a liberação de uma matriz sem unidade corporativa cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_003 =
            "Não é possível a liberação de uma matriz sem atividade corporativa cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_004 =
            "Não é possível a liberação de uma matriz sem atividade de negócio cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_005 = "Não é possível a liberação de uma matriz com unidade "
            + "de negócio sem atividade de negócio associada cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_006 =
            "Não é possível a liberação de uma matriz sem atividade corporativa cadastrada com peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_007 =
            "Não é possível a liberação de uma matriz sem atividade de negócio cadastrada com peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_008 =
            "Não é possível a liberação de uma matriz, a unidade corporativa deve ter peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_009 =
            "Não é possível a liberação de uma matriz, pelo menos uma unidade de negócio deve ter peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_010 =
            "Não é possível a liberação de uma matriz, em cada unidade de neg"
                    + "ócio pelo menos uma atividade de negócio deve ter peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_011 =
            "Não é possível a liberação de uma matriz, em cada atividade corp"
                    + "orativa pelo menos um grupo de risco e controle deve ter peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_012 =
            "Não é possível a liberação de uma matriz, em cada atividade de negó"
                    + "cio pelo menos um grupo de risco e controle deve ter peso máximo.";
    public static final String MSG_APS_MATRIZ_ERRO_015 =
            "Não é possível a liberação de uma matriz para ciclo no estado corec.";
    public static final String MSG_APS_MATRIZ_ERRO_016 =
            "Não é possível a edição de uma matriz vigente para ciclo no estado ";

    // Mensagens de erro DESIGNACAO
    public static final String MSG_APS_DESIGNACAO_ERRO_001 = SELECAO_DE_ARC_E_OBRIGATORIA;
    public static final String MSG_APS_DESIGNACAO_ERRO_002 =
            "É obrigatória a seleção do campo 'Servidores da equipe' ou 'Outros servidores'.";
    public static final String MSG_APS_DESIGNACAO_ERRO_003 = "Selecione apenas um único servidor para a designação.";
    public static final String MSG_APS_DESIGNACAO_ERRO_004 =
            "Só é permitida a designação de ARC em matriz com estado Vigente.";
    public static final String MSG_APS_DESIGNACAO_ERRO_005 =
            "Só é permitida a designação para ARCs com estados Previsto ou Designado.";
    public static final String MSG_APS_DESIGNACAO_ERRO_006 =
            "Não é pertimida a designação para um ARC com estado Concluído";

    public static final String MSG_APS_DESIGNACAO_DELEGACAO_ERRO =
            "Operação não permitida para analisador com perfil inspetor.";

    // Mensagens de erro DELEGAÇÃO
    public static final String MSG_APS_DELEGACAO_ERRO_001 = "Selecione apenas um único servidor para a delegação.";

    // Mensagens de erro AVALIACAO RISCO CONTROLE
    public static final String MSG_APS_ARC_ERRO_SELECIONE_ARC = SELECAO_DE_ARC_E_OBRIGATORIA;
    public static final String MSG_APS_ARC_ERRO_001 = "Inspetor não designado para o ARC.";
    public static final String MSG_APS_ARC_ERRO_003 =
            "Não é possível edição de ARC em ciclo no estado diferente de \"Em andamento\".";
    public static final String MSG_APS_ARC_ERRO_004 =
            "Não é possível edição de ARC de matriz no estado diferente de \"Vigente\".";
    public static final String MSG_APS_ARC_ERRO_006 =
            "É necessário informar, ao menos, uma justificativa para os itens de avaliação dos elementos do ARC.";

    public static final String MSG_APS_ARC_ERRO_007 =
            "Operação permitida apenas para usuário delegado ou supervisor titular do ciclo.";
    public static final String MSG_APS_ARC_ERRO_008 =
            "Operação não permitida para ARC em ciclo no estado diferente de Em andamento.";
    public static final String MSG_APS_ARC_ERRO_009 =
            "Operação não permitida para ARC em matriz no estado diferente de Vigente.";
    public static final String MSG_APS_ARC_ERRO_010 = "Pelo menos um elemento deve ter nota diferente de N/A.";
    public static final String MSG_APS_ARC_ERRO_017 =
            "Pelo menos um elemento deve ter nota preenchida diferente de N/A.";

    // Mensagens de erro REGRA PERFIL ACESSO
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_001 =
            "Pelo menos um campo é de preenchimento obrigatório.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_002 = "Funcionalidade não autorizada.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_003 = "Campo localização inválido.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_004 = "Campo matrícula inválido.";

    // Mensagens de erro PERFIL DE RISCO
    public static final String MSG_APS_PERFIL_RISCO_ERRO_001 =
            "Entidade supervisionável não possui ciclo de supervisão.";

    // Mensagens de erro PERFIL DE RISCO
    public static final String MSG_APS_GESTAO_ARC_ERROR_001 = "Equipe do usuário não é a responsável pela ES.";

    public static final String MSG_APS_GESTAO_ARC_ERROR_002 =
            "Funcionalidade Gerenciar ARCs disponível apenas para matriz de risco e controles vigente.";
    public static final String MSG_APS_GESTAO_ARC_ERROR_003 =
            "Funcionalidade Gerenciar ARCs disponível apenas para ciclo no estado em andamento.";

    public static final String MSG_APS_ANEXO_ERROR_001 = "Nome de arquivo já existente.";

    // Mensagens de erro SINTESE
    public static final String MSG_APS_SINTESE_ERRO_001 = "Campo Nova síntese é de preenchimento obrigatório.";

    // Mensagens de erro AQT
    public static final String MSG_APS_DESIGNACAO_DELEGACAO_AQT_ERRO_001 =
            "É obrigatória a seleção do campo \'Servidores da equipe\' ou \'Outros servidores\'.";

    public static final String MSG_APS_ANEF_ERRO_0015 =
            "É necessário informar, ao menos, uma justificativa para os itens de avaliação do elemento ";

    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_001 = "Campo 'Nova localização' inválido.";
    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_002 =
            "Campo 'Nova localização' é de preenchimento obrigatório.";
    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_003 =
            "Campo 'Nova localização' não pode ser igual a sua localização atual.";

}