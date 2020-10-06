//CHECKSTYLE:OFF -  Classe que cont�m todas as mensagens do sistema, justificando assim seu tamanho pela quantidade de casos de uso.
/*
 * Sistema APS
 * ConstantesMensagens.java
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
//CHECKSTYLE:ON
package br.gov.bcb.sisaps.util.validacao;

public class ConstantesMensagens {

    public static final String SELECAO_DE_ARC_E_OBRIGATORIA = "Sele��o de ARC � obrigat�ria.";

    public static final String MSG_ERRO_GERAL_055 = "MSG_ERRO_GERAL_055";
    public static final String MSG_ERRO_GERAL_095 = "MSG_ERRO_GERAL_095";
    public static final String MSG_DATA_INVALIDA = "error.dataInvalida";
    public static final String MSG_ERRO_CONCORRENCIA = "Erro de acesso concorrente.";
    public static final String MSG_ERRO_CONSTRAINT = "Erro de viola��o de constraint na base ou modelo defasado.";
    public static final String MSG_APS_RATING_REFACA_PESQUISA = "MSG_APS_RATING_REFACA_PESQUISA";
    public static final String MSG_APS_RATING_CAMPO_INVALIDO = "MSG_APS_RATING_CAMPO_INVALIDO";
    public static final String MSG_APS_RATING_SELECAO_ITEM_LISTA_OBRIGATORIO =
            "MSG_APS_RATING_SELECAO_ITEM_LISTA_OBRIGATORIO";

    // Mensagens de erro CICLO
    public static final String MSG_APS_CICLO_ERRO_001 =
            "A data de in�cio de ciclo deve ser menor ou igual � data corrente.";
    public static final String MSG_APS_CICLO_ERRO_003 = "ES n�o est� sob responsabilidade do usu�rio.";
    public static final String MSG_APS_CICLO_ERRO_004 = "Entidade supervision�vel j� possui ciclo de supervis�o.";
    public static final String MSG_APS_CICLO_ERRO_005 = "N�o � poss�vel usar a metodologia selecionada para esta ES.";

    // Mensagens de erro ATIVIDADE
    public static final String MSG_APS_ATIVIDADE_ERRO_001 =
            "N�o pode existir mais de uma atividade com o mesmo nome na matriz.";

    public static final String MSG_APS_ATIVIDADE_ERRO_002 =
            "Uma atividade corporativa n�o pode ser do tipo de atividade de neg�cio.";

    public static final String MSG_APS_ATIVIDADE_ERRO_003 =
            "Uma atividade de neg�cio n�o pode ser do tipo de atividade corporativo.";

    public static final String MSG_APS_ATIVIDADE_ERRO_004 = "N�o � poss�vel a edi��o de uma matriz no estado vigente.";

    // Mensagens de erro UNIDADE
    public static final String MSG_APS_UNIDADE_ERRO_001 =
            "N�o pode existir mais de uma unidade com o mesmo nome na matriz.";

    public static final String MSG_APS_UNIDADE_ERRO_003 = "Deve existir uma �nica unidade corporativa na matriz.";

    public static final String MSG_APS_UNIDADE_ERRO_004 =
            "Campo Fator de relev�ncia deve ser maior do que zero (0) e menor ou igual a um (1).";

    // Mensagens de erro MATRIZ
    public static final String MSG_APS_MATRIZ_ERRO_001 = "N�o � poss�vel a libera��o de uma matriz vigente.";
    public static final String MSG_APS_MATRIZ_ERRO_002 =
            "N�o � poss�vel a libera��o de uma matriz sem unidade corporativa cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_003 =
            "N�o � poss�vel a libera��o de uma matriz sem atividade corporativa cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_004 =
            "N�o � poss�vel a libera��o de uma matriz sem atividade de neg�cio cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_005 = "N�o � poss�vel a libera��o de uma matriz com unidade "
            + "de neg�cio sem atividade de neg�cio associada cadastrada.";
    public static final String MSG_APS_MATRIZ_ERRO_006 =
            "N�o � poss�vel a libera��o de uma matriz sem atividade corporativa cadastrada com peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_007 =
            "N�o � poss�vel a libera��o de uma matriz sem atividade de neg�cio cadastrada com peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_008 =
            "N�o � poss�vel a libera��o de uma matriz, a unidade corporativa deve ter peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_009 =
            "N�o � poss�vel a libera��o de uma matriz, pelo menos uma unidade de neg�cio deve ter peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_010 =
            "N�o � poss�vel a libera��o de uma matriz, em cada unidade de neg"
                    + "�cio pelo menos uma atividade de neg�cio deve ter peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_011 =
            "N�o � poss�vel a libera��o de uma matriz, em cada atividade corp"
                    + "orativa pelo menos um grupo de risco e controle deve ter peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_012 =
            "N�o � poss�vel a libera��o de uma matriz, em cada atividade de neg�"
                    + "cio pelo menos um grupo de risco e controle deve ter peso m�ximo.";
    public static final String MSG_APS_MATRIZ_ERRO_015 =
            "N�o � poss�vel a libera��o de uma matriz para ciclo no estado corec.";
    public static final String MSG_APS_MATRIZ_ERRO_016 =
            "N�o � poss�vel a edi��o de uma matriz vigente para ciclo no estado ";

    // Mensagens de erro DESIGNACAO
    public static final String MSG_APS_DESIGNACAO_ERRO_001 = SELECAO_DE_ARC_E_OBRIGATORIA;
    public static final String MSG_APS_DESIGNACAO_ERRO_002 =
            "� obrigat�ria a sele��o do campo 'Servidores da equipe' ou 'Outros servidores'.";
    public static final String MSG_APS_DESIGNACAO_ERRO_003 = "Selecione apenas um �nico servidor para a designa��o.";
    public static final String MSG_APS_DESIGNACAO_ERRO_004 =
            "S� � permitida a designa��o de ARC em matriz com estado Vigente.";
    public static final String MSG_APS_DESIGNACAO_ERRO_005 =
            "S� � permitida a designa��o para ARCs com estados Previsto ou Designado.";
    public static final String MSG_APS_DESIGNACAO_ERRO_006 =
            "N�o � pertimida a designa��o para um ARC com estado Conclu�do";

    public static final String MSG_APS_DESIGNACAO_DELEGACAO_ERRO =
            "Opera��o n�o permitida para analisador com perfil inspetor.";

    // Mensagens de erro DELEGA��O
    public static final String MSG_APS_DELEGACAO_ERRO_001 = "Selecione apenas um �nico servidor para a delega��o.";

    // Mensagens de erro AVALIACAO RISCO CONTROLE
    public static final String MSG_APS_ARC_ERRO_SELECIONE_ARC = SELECAO_DE_ARC_E_OBRIGATORIA;
    public static final String MSG_APS_ARC_ERRO_001 = "Inspetor n�o designado para o ARC.";
    public static final String MSG_APS_ARC_ERRO_003 =
            "N�o � poss�vel edi��o de ARC em ciclo no estado diferente de \"Em andamento\".";
    public static final String MSG_APS_ARC_ERRO_004 =
            "N�o � poss�vel edi��o de ARC de matriz no estado diferente de \"Vigente\".";
    public static final String MSG_APS_ARC_ERRO_006 =
            "� necess�rio informar, ao menos, uma justificativa para os itens de avalia��o dos elementos do ARC.";

    public static final String MSG_APS_ARC_ERRO_007 =
            "Opera��o permitida apenas para usu�rio delegado ou supervisor titular do ciclo.";
    public static final String MSG_APS_ARC_ERRO_008 =
            "Opera��o n�o permitida para ARC em ciclo no estado diferente de Em andamento.";
    public static final String MSG_APS_ARC_ERRO_009 =
            "Opera��o n�o permitida para ARC em matriz no estado diferente de Vigente.";
    public static final String MSG_APS_ARC_ERRO_010 = "Pelo menos um elemento deve ter nota diferente de N/A.";
    public static final String MSG_APS_ARC_ERRO_017 =
            "Pelo menos um elemento deve ter nota preenchida diferente de N/A.";

    // Mensagens de erro REGRA PERFIL ACESSO
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_001 =
            "Pelo menos um campo � de preenchimento obrigat�rio.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_002 = "Funcionalidade n�o autorizada.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_003 = "Campo localiza��o inv�lido.";
    public static final String MSG_APS_REGRA_PERFIL_ACESSO_ERRO_004 = "Campo matr�cula inv�lido.";

    // Mensagens de erro PERFIL DE RISCO
    public static final String MSG_APS_PERFIL_RISCO_ERRO_001 =
            "Entidade supervision�vel n�o possui ciclo de supervis�o.";

    // Mensagens de erro PERFIL DE RISCO
    public static final String MSG_APS_GESTAO_ARC_ERROR_001 = "Equipe do usu�rio n�o � a respons�vel pela ES.";

    public static final String MSG_APS_GESTAO_ARC_ERROR_002 =
            "Funcionalidade Gerenciar ARCs dispon�vel apenas para matriz de risco e controles vigente.";
    public static final String MSG_APS_GESTAO_ARC_ERROR_003 =
            "Funcionalidade Gerenciar ARCs dispon�vel apenas para ciclo no estado em andamento.";

    public static final String MSG_APS_ANEXO_ERROR_001 = "Nome de arquivo j� existente.";

    // Mensagens de erro SINTESE
    public static final String MSG_APS_SINTESE_ERRO_001 = "Campo Nova s�ntese � de preenchimento obrigat�rio.";

    // Mensagens de erro AQT
    public static final String MSG_APS_DESIGNACAO_DELEGACAO_AQT_ERRO_001 =
            "� obrigat�ria a sele��o do campo \'Servidores da equipe\' ou \'Outros servidores\'.";

    public static final String MSG_APS_ANEF_ERRO_0015 =
            "� necess�rio informar, ao menos, uma justificativa para os itens de avalia��o do elemento ";

    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_001 = "Campo 'Nova localiza��o' inv�lido.";
    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_002 =
            "Campo 'Nova localiza��o' � de preenchimento obrigat�rio.";
    public static final String MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_003 =
            "Campo 'Nova localiza��o' n�o pode ser igual a sua localiza��o atual.";

}