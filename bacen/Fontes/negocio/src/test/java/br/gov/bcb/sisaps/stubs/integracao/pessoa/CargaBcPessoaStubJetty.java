/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.stubs.integracao.pessoa;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ExercicioFuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.FuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtilSuporte;

/**
 * Realiza a carga do Stub quando o Jetty Local é iniciado. Ver "appContext-stubs.xml".
 */
public class CargaBcPessoaStubJetty {

    private static final String PAULO_AUGUSTO_SEIXAS = "PAULO AUGUSTO SEIXAS";
    private static final String DELIQ = "DELIQ";
    private static final String DELIQ_GLIQ1_GTBH3 = "DELIQ/GLIQ1/GTBH3";
    private static final String DELIQ_GLIQ1_GTBH2 = "DELIQ/GLIQ1/GTBH2";
    private static final String DELIQ_GLIQ1_GTBH1 = "DELIQ/GLIQ1/GTBH1";
    private static final String DELIQ_GLIQ1_GTBHO_COSUP_01 = "DELIQ/GLIQ1/GTBHO/COSUP-01";
    private static final String CHEFE_COSUP_1 = "CHEFE COSUP-01";
    private static final String CHEFE_GLIQ1 = "CHEFE GLIQ1";
    private static final String DELIQ_GLIQ1 = "DELIQ/GLIQ1";
    private static final String MATRICULA_5_555_555_5 = "5.555.555-5";
    private static final String MATRICULA_6_666_666_6 = "6.666.666-6";
    private static final String DESUP_GBPM1_GTSP4 = "DESUP/GBPM1/GTSP4";
    private static final String MARCUS_VINICIUS_ESPUDARO = "MARCUS VINICIUS ESPUDARO";
    private static final String MATRICULA_6_820_342_X = "6.820.342-X";
    private static final String COSUP_01 = "COSUP-01";
    private static final String DESUP_GBPM1_GTSP4_COSUP_01 = "DESUP/GBPM1/GTSP4/COSUP-01";
    private static final String MATRICULA_11283766 = "1.128.376-6";
    private static final String DEINF_GEDEG_DIREC = "DEINF/GEDEG/DIREC";
    private static final String MATRICULA_7_895_462_1 = "7.895.462-1";
    private static final String COORDENADOR = "COORDENADOR";
    private static final String MATRICULA_65264155 = "6.526.415-5";
    private static final String S = "S";
    private static final String DELIQ_GLIQ1_GTBHO = "DELIQ/GLIQ1/GTBHO";
    private static final String SITUACAO_ATIVO_NORMAL_116 = "116";
    private static final String CHEFE_DE_SUBUNIDADE = "CHEFE DE SUBUNIDADE";
    private static final String FUNCAO_FDT_1 = "FDT-1";
    private static final String FUNCAO_FDO_1 = "FDO-1";
    private static final String MATRICULA_1_234_567_8 = "1.234.567-8";
    private static final String MATRICULA_9_618_618_6 = "9.618.618-6";
    private static final String ANFRISIO_RODRIGUES_VIEIRA = "ANFRISIO RODRIGUES VIEIRA";
    private static final String MATRICULA_1_111_111_1 = "1.111.111-1";
    private static final String MATRICULA_1_111_111_X = "1.111.111-X";
    private static final String CAIO_DUARTE_CINTRA = "CAIO DUARTE CINTRA";
    private static final String MATRICULA_2_222_222_2 = "2.222.222-2";
    @Autowired
    private BcPessoaProviderStub stub;

    public void fazerCarga() {
        carregarServidoresColaboradores();
        carregarFuncoesComissionadasEmExercicio();
        carregarFuncoesComissionadas();
        carregarComponenteOrganizacionalVO();
        carregarChefiaPorLotacao();
        carregarChefiaSubstitutoEventual();
    }

    private void carregarChefiaSubstitutoEventual() {
        stub.incluirChefiaSubstitutoEventual("96186186",
                criarChefia(DELIQ_GLIQ1, servidorDeliqSisliq123(), servidorDeliqSisliq124(), servidorDeliqSisliq123()));
    }

    private void carregarChefiaPorLotacao() {
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1_GTBHO, servidor101(), servidor105(),
                servidor101()));
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1_GTBHO_COSUP_01, servidorDeliqSisliq121(),
                servidorDeliqSisliq122(), servidorDeliqSisliq121()));
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1, servidorDeliqSisliq123(),
                servidorDeliqSisliq124(), servidorDeliqSisliq123()));
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DEINF_GEDEG_DIREC, servidor106(), servidor106(),
                servidor106()));
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1_GTBH1, servidor151(), servidor151(),
                servidor151()));

        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1_GTBH2, servidor152(), servidor152(),
                servidor152()));

        stub.incluirChefiaComponenteOrganizacional(criarChefia(DELIQ_GLIQ1_GTBH3, servidor153(), servidor153(),
                servidor153()));
        stub.incluirChefiaComponenteOrganizacional(criarChefia(DESUP_GBPM1_GTSP4, servidor110(), servidor110(),
                servidor110()));
    }

    private void carregarFuncoesComissionadas() {
        stub.incluirFuncaoComissionada(criarFuncaoComissionada(FUNCAO_FDT_1, CHEFE_DE_SUBUNIDADE));
        stub.incluirFuncaoComissionada(criarFuncaoComissionada(FUNCAO_FDO_1, COORDENADOR));
        stub.incluirFuncaoComissionada(criarFuncaoComissionada("FDE-1", "CHEFE DE UNIDADE"));
        stub.incluirFuncaoComissionada(criarFuncaoComissionada("FDE-2", "CHEFE ADJUNTO DE UNIDADE"));
    }

    private void carregarComponenteOrganizacionalVO() {
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ, DELIQ));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DEINF_GEDEG_DIREC, "DEINF"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1_GTBHO, "GTBHO"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1_GTBHO_COSUP_01,
                "COSUP-01"));

        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DESUP_GBPM1_GTSP4, "DESUP"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DESUP_GBPM1_GTSP4_COSUP_01, COSUP_01));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DESUP_GBPM1_GTSP4_COSUP_01, "APOSE"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DESUP_GBPM1_GTSP4_COSUP_01, "EXONE"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1, "GLIQ1"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1_GTBH1, "GTBH1"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1_GTBH2, "GTBH2"));
        stub.incluirComponenteOrganizacionalVO(criarComponenteOrganizacionalVO(DELIQ_GLIQ1_GTBH3, "GTBH3"));

    }

    private void carregarFuncoesComissionadasEmExercicio() {
        stub.incluirExercicioFuncaoComissionada(criarExercicioFuncaoComissionada(MATRICULA_1_234_567_8, FUNCAO_FDT_1,
                CHEFE_DE_SUBUNIDADE, "E"));
        /*
         * stub.incluirExercicioFuncaoComissionada(criarExercicioFuncaoComissionada(
         * MATRICULA_9_618_618_6, FUNCAO_FDT_1, CHEFE_DE_SUBUNIDADE, S));
         */
        stub.incluirExercicioFuncaoComissionada(criarExercicioFuncaoComissionada(MATRICULA_1_111_111_1, FUNCAO_FDT_1,
                CHEFE_DE_SUBUNIDADE, S));
        stub.incluirExercicioFuncaoComissionada(criarExercicioFuncaoComissionada(MATRICULA_1_111_111_1, FUNCAO_FDT_1,
                CHEFE_DE_SUBUNIDADE, S));
        stub.incluirExercicioFuncaoComissionada(criarExercicioFuncaoComissionada(MATRICULA_11283766, FUNCAO_FDO_1,
                COORDENADOR, S));

    }

    private ExercicioFuncaoComissionadaVO criarExercicioFuncaoComissionada(String matricula,
            String codigoFuncaoComissionada, String descricao, String tipoExercicioFuncaoComissionada) {
        ExercicioFuncaoComissionadaVO exercicioFuncaoComissionada = new ExercicioFuncaoComissionadaVO();
        exercicioFuncaoComissionada.setMatricula(SisapsUtilSuporte.removerFormatacaoMatricula(matricula));
        exercicioFuncaoComissionada.setCodigoFuncaoComissionada(codigoFuncaoComissionada);
        exercicioFuncaoComissionada.setDescricao(descricao);
        exercicioFuncaoComissionada.setTipoExercicioFuncaoComissionada(tipoExercicioFuncaoComissionada);
        return exercicioFuncaoComissionada;
    }

    private void carregarServidoresColaboradores() {
        stub.incluirServidor(criarServidor("deinf.admin", MATRICULA_1_234_567_8, "Administrador", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("deinf.user", "2.334.567-8", "Usuario Deinf", "", "", "", "", null, null,
                null));
        stub.incluirServidor(servidor101());
        stub.incluirServidor(servidor105());
        stub.incluirServidor(servidor106());
        stub.incluirServidor(servidor109());
        stub.incluirServidor(servidor110());
        stub.incluirServidor(criarServidor("deliq.sisliq113", "9.999.083-0", "TESTESLQB13 MASSA DE TESTE",
                "Chefe inspetor 113", "", "Substituto inspetor 113", "", null, DELIQ_GLIQ1_GTBHO,
                SITUACAO_ATIVO_NORMAL_116));
        stub.incluirServidor(criarServidor("deliq.sisliq114", "1.357.902-4", "Supervidor 114", "Chefe supervisor 114",
                "", "Substituto supervidor 114", "", null, null, null));
        stub.incluirServidor(criarServidor("deliq.sisliq108", MATRICULA_1_111_111_X, "Consulta SNB 108",
                "Chefe consulta SNB 108", "", "Substituto consulta SNB 108", MATRICULA_7_895_462_1, FUNCAO_FDT_1,
                DELIQ_GLIQ1_GTBHO, SITUACAO_ATIVO_NORMAL_116));
        stub.incluirServidor(criarServidor("deliq.anfrisio", MATRICULA_1_111_111_1, ANFRISIO_RODRIGUES_VIEIRA, "", "",
                "", "", null, null, null));
        stub.incluirServidor(criarServidor("deliq.caio", MATRICULA_2_222_222_2, CAIO_DUARTE_CINTRA, "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.adriana", "0.169.394-8", "ADRIANA FERREIRA DE OLIVEIRA MOREIRA", "",
                "", "", "", null, null, null));
        stub.incluirServidor(criarServidor("desup.fukuwara", "0.230.039-7", "AGNALDO TAKASHI FUKUWARA", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.joseluiz", "5.625.578-0", "JOSE LUIZ LOEBENS", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.ramos", "2.521.947-2", "DOMINGOS RAMOS GARCIA", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.marcus", MATRICULA_6_820_342_X, MARCUS_VINICIUS_ESPUDARO,
                PAULO_AUGUSTO_SEIXAS, "8.059.134-5", MARCUS_VINICIUS_ESPUDARO, MATRICULA_6_820_342_X, null,
                DESUP_GBPM1_GTSP4_COSUP_01, SITUACAO_ATIVO_NORMAL_116));
        stub.incluirServidor(servidorDeliqSisliq121());
        stub.incluirServidor(servidorDeliqSisliq122());
        stub.incluirServidor(servidorDeliqSisliq123());
        stub.incluirServidor(servidorDeliqSisliq124());
        stub.incluirServidor(criarServidor("deliq.sisliq104", "2.569.873-3", "Comitê 104", "Chefe comitê 104", "",
                "Substituto comitê 104", "", null, DELIQ_GLIQ1_GTBHO, SITUACAO_ATIVO_NORMAL_116));
        stub.incluirServidor(servidorDeliqSisliq102());
        stub.incluirServidor(criarServidor("desup.marcelo", "6.767.218-3", "MARCELO DAVI XAVIER DA SILVEIRA DATZ", "",
                "", "", "", FUNCAO_FDT_1, "DESUP/GBPM1/GTRJA", SITUACAO_ATIVO_NORMAL_116));

        stub.incluirServidor(criarServidor("desup.lemos", "18380840", "CARLOS JOSE BRAZ GOMES DE LEMOS", "", "", "",
                "", null, null, null));
        stub.incluirServidor(criarServidor("desup.wilma", "98457616", "WILMA DOS SANTOS LIMA DE AQUINO", "", "", "",
                "", null, null, null));
        stub.incluirServidor(criarServidor("desup.carneiro", "31615651", "FABIO LACERDA CARNEIRO", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.gianfranco", "37405853", "GIANFRANCO CATINELLA", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.inglez", "67665918", "MARCELO COLLI INGLEZ", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.maranhao", "6542543X", "LUIZ MARANHAO DE MELLO", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.ettrich", "98306642", "WERNER ETTRICH", "", "", "", "", null, null,
                null));
        stub.incluirServidor(criarServidor("desup.costa", "58517669", "JOSE ROMUALDO COSTA JUNIOR", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.zeni", "86110756", "RICARDO SEVIERE ZENI", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.cancado", "96976101", "WALTER BATISTA CANCADO", "", "", "", "", null,
                null, null));

        stub.incluirServidor(criarServidor("desup.henrique", "48856509", "JOAO HENRIQUE LEITE MARTINS", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.pereira", "91732662", "SIMAO PINTO PEREIRA", "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.camargo", "67955371", "MARCO ANTONIO DE CAMARGO", "", "", "", "",
                null, null, null));
        stub.incluirServidor(criarServidor("desup.magina", "73654914", "NELIO RODRIGUES MAGINA JUNIOR", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.cordeiro", "18829856", "CARLOS ROBERTO CREDIDIO CORDEIRO", "", "",
                "", "", null, null, null));
        stub.incluirServidor(criarServidor("desup.seixas", "80591345", PAULO_AUGUSTO_SEIXAS, "", "", "", "", null,
                null, null));
        stub.incluirServidor(criarServidor("desup.gregio", "94305633", "VALDIR GREGIO", "", "", "", "", null, null,
                null));

        stub.incluirServidor(criarServidor("desup.peroba", "18076084", "CARLOS EDUARDO PEROBA ANGELO", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.ohara", "36001406", "GEORGE OHARA", "", "", "", "", null, null, null));

        stub.incluirServidor(criarServidor("desup.machado", "62064061", "LEONARDO BAHIA MACHADO FILHO", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.contador", "6781638X", "MARCIO CONTADOR CAMARGO", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.sequeira", "90616006", "SERGIO DOMINGUES SEQUEIRA", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.vsousa", "94217661", "VALDEMIR FORTES DE SOUSA", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.borges", "94493790", "VALTER BORGES DE ARAUJO NETO", "", "", "", "",
                null, null, null));

        stub.incluirServidor(criarServidor("desup.ahmar", "17315824", "CARLOS AHMAR", "", "", "", "", null, null, null));

    }

    private ServidorVO servidor106() {
        return criarServidor("deliq.sisliq106", MATRICULA_11283766, "Consulta 106", "Chefe consulta tudo 106",
                "78954621", "Substituto consulta tudo 106", "", FUNCAO_FDO_1, DEINF_GEDEG_DIREC,
                SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor109() {
        return criarServidor("deliq.sisliq109", "0.404.040-4", "Consulta resumido 109",
                "Chefe consulta resumido tudo 109", "78954681", "Substituto consulta resumido tudo 106", "",
                FUNCAO_FDO_1, DEINF_GEDEG_DIREC, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor110() {
        return criarServidor("deliq.sisliq110", "6.521.489-2", "CHRISTIANO ATHAÍDE DROGBA", "Consulta SRPNB 109",
                "04040404", "Consulta SRPNB 110", "2805041X", FUNCAO_FDT_1, DESUP_GBPM1_GTSP4,
                SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidorDeliqSisliq102() {
        return criarServidor("deliq.sisliq102", "2.569.457-7", "Gerente 102", "", "", "", "", FUNCAO_FDT_1,
                DELIQ_GLIQ1, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidorDeliqSisliq121() {
        return criarServidor("deliq.sisliq121", MATRICULA_5_555_555_5, CHEFE_COSUP_1, "", "", "", "", FUNCAO_FDO_1,
                DELIQ_GLIQ1_GTBHO_COSUP_01, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidorDeliqSisliq122() {
        return criarServidor("deliq.sisliq122", "5.555.555-6", "SUBSTITUTO EVENTUAL COSUP-01", CHEFE_COSUP_1,
                MATRICULA_5_555_555_5, "", "", null, DELIQ_GLIQ1_GTBHO_COSUP_01, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidorDeliqSisliq123() {
        return criarServidor("deliq.sisliq123", MATRICULA_6_666_666_6, CHEFE_GLIQ1, "", "", "", "", FUNCAO_FDO_1,
                DELIQ_GLIQ1, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidorDeliqSisliq124() {
        return criarServidor("deliq.sisliq124", "6.666.666-7", "SUBSTITULO EVENTUAL GLIQ1", CHEFE_GLIQ1,
                MATRICULA_6_666_666_6, "", "", null, DELIQ_GLIQ1, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor101() {
        return criarServidor("deliq.sisliq101", MATRICULA_9_618_618_6, "ROBERTO SANTOS ALENCAR",
                ANFRISIO_RODRIGUES_VIEIRA, MATRICULA_1_111_111_1, CAIO_DUARTE_CINTRA, MATRICULA_2_222_222_2,
                /* FUNCAO_FDT_1 */null, DELIQ_GLIQ1_GTBHO, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor151() {
        return criarServidor("deliq.sisliq151", MATRICULA_9_618_618_6, "CHEFE GTBH1", ANFRISIO_RODRIGUES_VIEIRA,
                MATRICULA_1_111_111_1, CAIO_DUARTE_CINTRA, MATRICULA_2_222_222_2,
                /* FUNCAO_FDT_1 */null, DELIQ_GLIQ1_GTBH1, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor152() {
        return criarServidor("deliq.sisliq152", MATRICULA_9_618_618_6, "CHEFE GTBH2", ANFRISIO_RODRIGUES_VIEIRA,
                MATRICULA_1_111_111_1, CAIO_DUARTE_CINTRA, MATRICULA_2_222_222_2,
                /* FUNCAO_FDT_1 */null, DELIQ_GLIQ1_GTBH2, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor153() {
        return criarServidor("deliq.sisliq153", MATRICULA_9_618_618_6, "CHEFE GTBH3", ANFRISIO_RODRIGUES_VIEIRA,
                MATRICULA_1_111_111_1, CAIO_DUARTE_CINTRA, MATRICULA_2_222_222_2,
                /* FUNCAO_FDT_1 */null, DELIQ_GLIQ1_GTBH3, SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO servidor105() {
        return criarServidor("deliq.sisliq105", MATRICULA_65264155, "Administrador 105", "Chefe administrador 105",
                MATRICULA_1_111_111_1, "Substituto administrador 105", "32545124", FUNCAO_FDT_1, DELIQ_GLIQ1_GTBHO,
                SITUACAO_ATIVO_NORMAL_116);
    }

    private ServidorVO criarServidor(String login, String matricula, String nome, String nomeChefe,
            String matriculaChefe, String nomeSubstituto, String matriculaSubstituto, String funcao,
            String localizacao, String situacao) {
        ServidorVO serv = new ServidorVO();
        serv.setLogin(login);
        serv.setMatricula(SisapsUtilSuporte.removerFormatacaoMatricula(matricula));
        serv.setNome(nome);
        serv.setNomeChefe(nomeChefe);
        serv.setMatriculaChefe(SisapsUtilSuporte.removerFormatacaoMatricula(matriculaChefe));
        serv.setNomeSubstituto(nomeSubstituto);
        serv.setMatriculaSubstituto(SisapsUtilSuporte.removerFormatacaoMatricula(matriculaSubstituto));
        serv.setUnidade(login.substring(0, 5).toUpperCase());
        serv.setFuncao(funcao);
        serv.setLocalizacao(localizacao);
        serv.setSituacao(situacao);
        return serv;
    }

    private FuncaoComissionadaVO criarFuncaoComissionada(String codigo, String descricao) {
        FuncaoComissionadaVO funcao = new FuncaoComissionadaVO();
        funcao.setCodigo(codigo);
        funcao.setDescricao(descricao);
        return funcao;
    }

    private ComponenteOrganizacionalVO criarComponenteOrganizacionalVO(String rotulo, String sigla) {
        ComponenteOrganizacionalVO componente = new ComponenteOrganizacionalVO();
        componente.setRotulo(rotulo);
        componente.setSigla(sigla);
        return componente;
    }

    private ChefiaVO criarChefia(String localizacao, ServidorVO chefeTitular, ServidorVO substitutoEventual,
            ServidorVO emExercicio) {
        ChefiaVO chefia = new ChefiaVO();
        chefia.setRotuloComponenteOrganizacional(localizacao);
        chefia.setChefeTitular(chefeTitular);
        chefia.setSubstitutoEventual(substitutoEventual);
        chefia.setChefeEmExercicio(emExercicio);
        return chefia;
    }

}
