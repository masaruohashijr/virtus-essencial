/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util;

import java.util.Date;

import br.gov.bcb.sisaps.util.geral.DataUtil;

/**
 * Classe para mandar as constantes utilizadas no pacote de negócios. Constantes utilizadas
 * unicamente na parte web deverão ser salvas em Constantes.
 * 
 */
public final class Constantes {
    public static final String PESQUISA = "P";
    public static final String DEMAIS = "D";
    public static final int ESCALA = 2;
    public static final int TAMANHO_MAXIMO_TEXTO_CURTO = 250;
    public static final String WID_BOTOES_EXPANDIR_COLAPSAR = "bttExpandirColapsar";
    public static final String SEM_WRAP = "semWrap";
    public static final String ERRO_CAMPO_OBRIGATORIO = "error.NotNull";
    public static final String ERRO_CAMPO_NOT_EMPTY = "error.NotEmpty";

    public static final String ERRO_CAMPO_OBRIGATORIO_SOLICITACAO = "error.NotNull.Solicitacao";
    public static final String ERRO_NAO_PREENCHIDO = "error.NaoPrenchido";
    public static final String ERRO_TAMANHO_MAXIMO = "StringValidator.maximum";

    public static final int PAGINACAO_DOIS = 2;
    public static final int PAGINACAO_CINCO = 5;
    public static final int PAGINACAO_VINTE = 20;
    public static final int PAGINACAO_DEZ = 10;
    public static final String UNCHECKED = "unchecked";
    public static final String RAWTYPES = "rawtypes";
    public static final String ISO_8859 = "ISO-8859-1";
    public static final String WID_BOTAO_VOLTAR = "bttVoltar";
    public static final String FALHA_COPY_PROPERTIES = "BeanUtils.copyProperties";
    public static final String FORMATO_DATA_BANCO = "yyyy-MM-dd";
    public static final String FORMATO_DATA_COM_BARRAS = "dd/MM/yyyy";
    public static final String FORMATO_DATA_MES_ANO = "MM/yyyy";
    public static final String FORMATO_DATA_D_M_AAAA_COM_PONTO = "d.M.yyyy";
    public static final String FORMATO_DATA_D_M_COM_PONTO = "d.M";
    public static final String FORMATO_HORA = "HH:mm:ss";
    public static final String FORMATO_HORA_AGENDA = "HH:mm";
    public static final String FORMATO_DATA_HORA = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMATO_DATA_HORA_SEMSEGUNDOS = "dd/MM/yyyy HH:mm";
    public static final String FORMATO_NUMERO_PORTARIA = "###.###";
    public static final String FORMATO_NUMERO_AUDITORIA_RECOMENDACAO = "####/###";
    public static final String FORMATO_VOTO_BCB = "####/####";
    public static final String FORMATO_CAMPO_NUMERICO_6 = "######";
    public static final String FORMATO_CAMPO_NUMERICO_7 = "#######";
    public static final String FORMATO_CAMPO_NUMERICO_8 = "########";
    public static final String FORMATO_CAMPO_NUMERICO_9 = "#########";
    public static final String FORMATO_CAMPO_NUMERICO_10 = "##########";
    public static final String ERRO_RESSUBMISSAO_FORMULARIO = "Não é permitido o reenvio das informações.";
    public static final String ERRO_CONCORRENCIA =
            "O registro atual foi modificado por outro usuário. Consulte o registro novamente.";
    public static final String TIPO_ARQUIVO_INVALIDO =
            "Tipo de arquivo inválido. Só é possível carregar arquivos com as extensões '.docx' e '.docm'.";
    public static final int TIPO_DIRETORIA = 3;
    public static final int TIPO_UNIDADE = 4;
    public static final int TIPO_GERENCIA = 5;
    public static final int TIPO_SUB_UNIDADE = 6;
    public static final String SEPARADOR_ABRANGENCIA = ", ";
    public static final String ESPACO_EM_BRANCO = " ";
    public static final String DOIS_PONTOS = ": ";
    public static final String SEPARADOR_HIFEN = " - ";

    /** Máscara de número. */
    public static final String FORMATAR_CAMPO_NUMERICO_9 =
            "javascript:return formatarCampoNumerico (this, '#########');";
    public static final String FORMATAR_CAMPO_NUMERICO_4 = "javascript:return formatarCampoNumerico (this, '####');";
    public static final String FORMATAR_CAMPO_NUMERICO_3 = "javascript:return formatarCampoNumerico (this, '###');";
    public static final String FORMATAR_CAMPO_NUMERICO_2 = "javascript:return formatarCampoNumerico (this, '##');";

    public static final String FORMATO_MATRICULA = "#.###.###-#";

    /** Class do CSS. */
    public static final String CLASS_FUNDO_PADRA_A_ESCURO_2A = "fundoPadraoAEscuro2a";

    /** Nome do evento onblur. */
    public static final String ONBLUR = "onblur";
    public static final String ONCHANGE = "onchange";

    /** Nome do evento onkeyup. */
    public static final String ONKEYUP = "onkeyup";

    /** Nome do evento onkeypress. */
    public static final String ONKEYPRESS = "onkeypress";
    public static final String ONKEYDOWN = "onkeydown";

    public static final String ONFOCUS = "onfocus";

    public static final String CENTRALIZADO = "centralizado";

    public static final String ESTILO_NOME_PROCESSO = "nomeProcesso";

    public static final String ESQUERDA = "aesquerda";

    public static final String DIREITA = "adireita";

    public static final String ESCOPO_6_LINHAS = "seisLinhasEscopo";

    /** Opção "Outra" para combo boxes */
    public static final String OPCAO_OUTRA = "Outra...";

    /** Constante utilizada para labels */
    public static final String LABEL = "Lb";

    /** Constante para definição de um formulário */
    public static final String FORMULARIO = "form";

    /**
     * Altura padrão dos campos de texto longos.
     */
    public static final int ALTURA_PADRAO_TEXTAREA = 6;
    /**
     * Altura padrão dos campos de texto longos.
     */
    public static final int ALTURA_PADRAO_TEXTAREA_12 = 12;

    /**
     * Altura padrão dos campos de texto longos.
     */
    public static final int ALTURA_TEXTAREA_ESTRUTURA = 24;
    /**
     * Largura padrão dos campos de texto longos.
     */
    public static final int LARGURA_PADRAO_TEXTAREA = 100;

    public static final int PERCENTUAL_2 = 2;
    public static final int PERCENTUAL_5 = 5;
    public static final int PERCENTUAL_6 = 6;
    public static final int PERCENTUAL_7 = 7;
    public static final int PERCENTUAL_8 = 8;
    public static final int PERCENTUAL_10 = 10;
    public static final int PERCENTUAL_11 = 11;
    public static final int PERCENTUAL_12 = 12;
    public static final int PERCENTUAL_15 = 15;
    public static final int PERCENTUAL_20 = 20;
    public static final int PERCENTUAL_25 = 25;
    public static final int PERCENTUAL_19 = 19;
    public static final int PERCENTUAL_27 = 27;
    public static final int PERCENTUAL_30 = 30;
    public static final int PERCENTUAL_34 = 34;
    public static final int PERCENTUAL_35 = 35;
    public static final int PERCENTUAL_40 = 40;
    public static final int PERCENTUAL_45 = 45;
    public static final int PERCENTUAL_50 = 50;
    public static final int PERCENTUAL_55 = 55;
    public static final int PERCENTUAL_60 = 60;
    public static final int PERCENTUAL_70 = 70;
    public static final int PERCENTUAL_76 = 76;
    public static final int PERCENTUAL_80 = 80;
    public static final int PERCENTUAL_90 = 90;
    public static final int PERCENTUAL_95 = 95;

    public static final String VERSAO = "versao";
    public static final String COMENTARIO = "comentario";
    public static final String NOME = "nome";
    public static final String DESCRICAO = "descricao";
    public static final String ORIGEM = "origem";
    public static final String ONCLICK = "onclick";
    public static final String CSS_FUNDO_PADRAO_A_ESCURO2 = "fundoPadraoAEscuro2";

    public static final String CSS_FUNDO_PADRAO_A_CLARO1 = "fundoPadraoAClaro1";

    public static final String CSS_FUNDO_PADRAO_A_CLARO1A = "fundoPadraoAClaro1a";

    public static final String CSS_FUNDO_PADRAO_A_ESCURO3 = "fundoPadraoAEscuro3";

    public static final String CSS_FUNDO_PADRAO_A_CLARO2A = "fundoPadraoAClaro2A";

    public static final String FUNDO_PADRAO_A_CLARO2 = "fundoPadraoAClaro2";
    public static final String FUNDO_PADRAO_A_CLARO3 = "fundoPadraoAClaro3";

    public static final String TEXTO_CENTRALIZADO = "textoCentralizado";
    /** Class do CSS. */
    public static final String TEXTO_ALINHADO_AO_TOPO = "aotopo";

    public static final String PONTO = ".";
    public static final String VIRGULA = ",";
    public static final String ASPAS_SIMPLES = "'";

    public static final String A_HREF = "<a href='";

    public static final String SUBLINHADO = "_";

    /** Cor Verde para estilos do Css */
    public static final String CSS_VERDE = "green pointer";

    /** Cor Vermelho para estilos do Css */
    public static final String CSS_VERMELHO = "red pointer";

    /** Cor Laranja para estilos do Css */
    public static final String CSS_LARANJA = "orange";

    /** Cor Marrom para estilos do Css */
    public static final String CSS_MARROM = "brown";

    /** Cor Marrom para estilos do Css */
    public static final String CSS_GRAY = "gray";

    /** Cor Azul do Mar para estilos do Css */
    public static final String CSS_BLUESKY = "skyblue";

    /** Cor Verde Claro para estilos do Css */
    public static final String CSS_LIGHTGREEN = "lightgreen";

    /** Cor Amarelo para estilos do Css */
    public static final String CSS_YELLOW2 = "yellow pointer";

    /** Cor Amarelo para estilos do Css */
    public static final String CSS_NAO_APLICAVEL = "naoAplicavel";

    /** Código da AUDIT **/
    public static final String CODIGO_AUDIT = "COR000207";

    public static final String TIPO_ARQUIVO_ANEXO_RELATORIO_INVALIDO =
            "Só é permitido anexar documentos no formato docx.";

    public static final String CAMPO_DATA_FIM_AUDITORIA = "Data fim da auditoria";

    public static final String CODIGO_FUNCAO_CHEFE_ADJUNTO = "FDE-2";

    public static final int TAMANHO_2000 = 2000;

    public static final int TAMANHO_6000 = 6000;
    public static final String LINK_FECHA = "]";

    public static final String AUD = "AUD";
    public static final String LINK_ABRE = "link[";
    public static final Integer MES_UM = 1;
    public static final Integer MES_DOIS = 2;
    public static final Integer MES_TRES = 3;
    public static final Integer MES_QUATRO = 4;
    public static final Integer MES_CINCO = 5;
    public static final Integer MES_SEIS = 6;
    public static final Integer MES_SETE = 7;
    public static final Integer MES_OITO = 8;
    public static final Integer MES_NOVE = 9;
    public static final Integer MES_DEZ = 10;
    public static final Integer MES_ONZE = 11;
    public static final Integer MES_DOZE = 12;
    public static final String ERRO_ANEXO_SESSAO = "Os dados da sua sessão no sistema foram alterados. "
            + "Por favor, recarregue a página e anexe os arquivos novamente. "
            + "O erro pode ter sido causado pelo uso do sistema em outra aba do navegador.";

    public static final String MSG_FORMULA_INVALIDA = "Fórmula inválida.";
    public static final String MSG_NUMERO_PORTARIA_INVALIDO = "Campo 'Número da portaria' inválido.";

    public static final String MSG_TIPO_RETORNO_FORMULA_INVALIDO =
            "Campo 'Tipo de retorno da fórmula' não é compatível com a fórmula informada.";

    public static final String RECOMENDACAO_CANCELADA = "Recomendação cancelada";
    public static final String RECOMENDACAO_CONCLUIDA = "Recomendação concluída";
    public static final String RECOMENDACAO_ATRASADA = "Recomendação atrasada";
    public static final String RECOMENDACAO_DENTRO_PREVISTO = "Recomendação dentro do previsto";
    public static final String RECOMENDACAO_PROXIMA_DATA_LIMITE = "Recomendação próxima da data limite";

    public static final String ARQUIVO = "Arquivo '";
    public static final String NAO_FOI_ENCONTRADO = "' não foi encontrado. ";

    public static final String CSS_AZUL = "blue pointer";
    public static final String CSS_VERMELHO_ESCURO = "darkred";

    public static final String MENSAGEM_SUCESSO_ENCAMINHAR_RECOMENDACOES =
            "As recomendações foram encaminhadas com sucesso.";
    public static final String MENSAGEM_SUCESSO_ENCAMINHAR_RECOMENDACAO = "A recomendação foi encaminhada com sucesso.";
    public static final String MENSAGEM_SUCESSO_RETOMAR = "As recomendações foram retornadas com sucesso.";

    public static final String MENSAGEM_SUCESSO_ENCAMINHAR_ITENS_SA_NA =
            "Os itens foram encaminhados à Auditoria com sucesso.";
    public static final String MENSAGEM_SUCESSO_ENCAMINHAR_ITEM_SA_NA =
            "O item foi encaminhado à Auditoria com sucesso.";

    public static final String MENSAGEM_SUCESSO_RETOMAR_ITENS_SA_NA =
            "Os itens de solicitações e notas de auditoria foram retomados com sucesso.";
    public static final String MENSAGEM_SUCESSO_RETOMAR_ITEM_SA_NA =
            "O item de solicitação e nota de auditoria foi retomado com sucesso.";

    public static final String MENSAGEM_DADOS_GERAIS_ANDAMENTO =
            "A alteração dos dados gerais da recomendação foi realizada com sucesso.";
    public static final String MENSAGEM_SUGERIR_RECOMENDACAO = "A recomendação foi reaberta com sucesso.";

    public static final String MENSAGEM_ALTERACAO_ASSUNTO_AUDITORIA =
            "Alteração de assunto de auditoria realizada com sucesso.";

    public static final String MENSAGEM_ALTERACAO_PROCEDIMENTO_AUDITORIA =
            "Alteração de procedimento da auditoria realizada com sucesso.";

    public static final String MENSAGEM_ALTERACAO_DADOS_GERAIS =
            "Alteração dos dados gerais da auditoria realizada com sucesso.";

    public static final String MENSAGEM_SUGERIR_ALTERACAO = "As sugestões foram encaminhas ao auditor com sucesso.";

    public static final String MENSAGEM_ALTERAR_PROCESSO_AUDITAVEL =
            "A alteração do processo auditável das recomendações foi realizada com sucesso.";

    public static final String ID_LINK_NOME_PROCESSO = "idLinkNomeProcesso";

    public static final String MENSAGEM_INCLUSAO_SOLICITACAO_AUDITORIA =
            "Inclusão de solicitação/nota de auditoria realizada com sucesso.";

    public static final String MENSAGEM_EXCLUSAO_SOLICITACAO_AUDITORIA =
            "Exclusão de solicitação/nota de auditoria realizada com sucesso.";

    public static final String MENSAGEM_SOLICITACAO_SUPERVISAO_SANA =
            "Solicitação de supervisão de SA/NA realizada com sucesso.";

    public static final String MENSAGEM_ALTERACAO_SANA =
            "Alteração de solicitação/nota de auditoria realizada com sucesso.";

    public static final String MENSAGEM_SUGERIR_ALTERACAO_SANA = "A sugestão de alterações foi realizada com sucesso.";

    public static final String MENSAGEM_RESPOSTA_GESTOR = "A recomendação foi encaminhada ao gestor com sucesso.";

    public static final String MENSAGEM_ENCAMINHAR_REC_GERENTE =
            "A recomendação foi encaminhada ao gerente com sucesso.";

    public static final String ID_PAINEL_LISTAGEM_DADOS = "idPainelListagemDadosComum";

    public static final String MENSAGEM_SUCESSO_ENCAMINHAR_ITEM_SOLICITACAO =
            "Os itens de solicitações e notas de auditoria foram encaminhados com sucesso.";

    public static final String MENSAGEM_SUCESSO_ALTERACAO_COMPONENTE_AUDITADO =
            "A alteração do componente auditado das recomendações foi realizada com sucesso.";

    public static final String MENSAGEM_SUCESSO_ALTERACAO_UNIDADE_RECOMENDADA =
            "A alteração da unidade recomendada das recomendações foi realizada com sucesso.";

    public static final Integer DUZENTOS = 200;
    public static final Integer CINQUENTA = 50;
    public static final Integer CEM = 100;
    public static final Integer ZERO = 0;

    public static final int TAMANHO_BUFFER_PADRAO = DUZENTOS;

    public static final String MARCADOR_NOME_CAMPO = "#";
    public static final String MSG_ERRO_LINK_QUEBRADO = "Campo '" + MARCADOR_NOME_CAMPO + "' apresenta link quebrado.";
    public static final int NUMERO_31 = 31;
    public static final int NUMERO_4 = 4;
    public static final int NUMERO_3 = 3;
    public static final int NUMERO_2 = 2;
    public static final int NUMERO_1 = 1;
    public static final String DEMANDAS_EXTERNAS = "Demandas externas";
    public static final String ID_CAMPO_PESQUISA = "idPesquisa";
    public static final String ACOES = "Ações";

    public static final String MENSAGEM_SUCESSO_REABRIR = "Demanda externa reaberta com sucesso.";
    public static final String DATA_PRAZO = "dataPrazo";
    public static final int POS_GRUPO_ITEM = 2;
    public static final int POS_GRUPO_HISTORICO = 4;
    public static final int POS_GRUPO_COMENTARIOS = 5;

    public static final String ATIVO = "ATIVO";
    public static final String SERVIDOR = "SERVIDOR";
    public static final String NAO_SERVIDOR = "NÃO SERVIDOR";
    public static final String PROP_MATRICULA = "matricula";
    public static final String PROP_NOME = NOME;
    public static final String PROP_ROTULO = "rotulo";
    public static final String NENHUM_REGISTRO_ENCONTRADO =
            "Nenhum registro que satisfaça os critérios de pesquisa foi encontrado.";
    public static final String N_A = "N/A";
    public static final String ASTERISCO_A = "*A";
    public static final String VAZIO = "";
    public static final String EM = " em ";
    public static final String COREC = " (Corec)";
    
    public static final String NOME_ES = "%es%";
    public static final String LOCALIZACAO = "%localização%";
    public static final String DATA_COREC = "%datacorec%";
    public static final String LOCAL = "%local%";
    public static final String HORARIO = "%horário%";
    public static final String TABELA = "%tabelacomite%";
    public static final String SEPARADOR_BARRA = "|";
    
    public static final Date DATA_LIMITE_ANEXO_ATA = DataUtil.dateFromString("27/10/2015");
    
    public static final String ENDERECO_SISTEMA = "/aps-src";
    
    public static final Date DATA_LIMITE_GRAU_PREOCUPACAO = DataUtil.dateFromString("22/02/2016");
    public static final String PARAMETRO_EMAIL_TODOS_EVENTOS = "GRUPO_TODOS_EVENTOS";
    public static final String PARAMETRO_EMAIL_AUX_TERMO = "GRUPO_AUXILIAR_ACOMP_TERMOS";
    public static final String PARAMETRO_EMAIL_AUX_INSPECOES = "GRUPO_AUXILIAR_INSPEÇOES";
    public static final String PARAMETRO_EMAIL_AUX_PRESTACAO = "GRUPO_AUXILIAR_PRESTACAO_INFORMAÇOES";
    public static final String PARAMETRO_EMAIL_AUX_OPINIAO = "GRUPO_AUXILIAR_OPINIAO_CONCLUSIVA";
    public static final String PARAMETRO_EMAIL_PERFIL_RISCO = "GRUPO_AUXILIAR_PERFIL_RISCO";
    public static final String PARAMETRO_EMAIL_REMETENTE = "REMETENTE";
    public static final String PARAMETRO_EMAIL_ANEXOS = "GRUPO_EMAIL_VERIF_ANEXOS";
    
    public static final String ASSUNTO_EMAIL  = "SisAPS - Publicação de novas informações - ";
}
