/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.excecoes.BCRInfraException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.comum.pessoa.negocio.servidor.ConsultaServidores;
import br.gov.bcb.sisaps.integracao.Cache;
import br.gov.bcb.sisaps.integracao.CacheChefiaPorLocalizacao;
import br.gov.bcb.sisaps.integracao.CacheChefiaPorSubstitutoEventual;
import br.gov.bcb.sisaps.integracao.CacheCompOrganizacional;
import br.gov.bcb.sisaps.integracao.CacheExercicioFunCom;
import br.gov.bcb.sisaps.integracao.CacheFuncoesComissionadas;
import br.gov.bcb.sisaps.integracao.CacheListCompOrganizacional;
import br.gov.bcb.sisaps.integracao.CacheServidores;
import br.gov.bcb.sisaps.integracao.CacheServidoresPorUnidade;
import br.gov.bcb.sisaps.integracao.CacheUnidades;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

/**
 * Incluir métodos que adaptem os dados dos componentes de negócio para objetos de domínio da
 * aplicação.
 */
@Component
public class BcPessoaAdapter {

    private static final String NULL = "null";
    private static final Log LOG = LogFactory.getLog(BcPessoaAdapter.class);
    private static final String ERRO_BUSCAR_SERVIDOR_POR_LOGIN = "Erro ao buscarServidorPorLogin. Login: ";
    private static final String ERRO_BUSCAR_LOCALIZACAO_POR_ROTULO = "Erro ao buscarLocalizacao. Rotulo: ";

    private static final String MSG_LOG_ERRO_BUSCAR_SERVIDOR = "Erro ao buscarServidor. ";
    private static final String MSG_LOG_ERRO_BUSCAR_EXERCICIO_FUNCAO_COMISSIONADA =
            "Erro ao buscarExercicioFuncaoComissionada. ";

    private static final String METODO_CONSULTAR_SERVIDORES = "consultarServidores";
    private static final String METODO_BUSCAR_SERVIDOR = "buscarServidor";

    private static final String COMPONENTE_BC_PESSOA = "BcPessoa";

    private static final Map<String, String> MAPA_MENSAGENS = new HashMap<String, String>();

    @Autowired
    protected IBcPessoa provider;

    static {
        MAPA_MENSAGENS.put("Ultrapassou o limite máximo de registros, utilize paginação",
                "A consulta possui muitos resultados, por favor seja mais específico nos parâmetros de pesquisa.");
        MAPA_MENSAGENS.put("Formato de login inválido", "O formato do login consultado não é válido.");
    }

    public static BcPessoaAdapter get() {
        return SpringUtils.get().getBean(BcPessoaAdapter.class);
    }

    public ServidorVO buscarServidor(String matricula, Date dataBase) {
        ServidorVO servidor = null;
        try {
            if (StringUtils.isNotBlank(matricula)) {
                System.out.println("##BUGLOCALIZACAO buscarServidor matricula: " + matricula);
                String data = obterDataBaseFormatadaChaveCache(dataBase);
                System.out.println("##BUGLOCALIZACAO buscarServidor data: " + data);
                String chave = Util.normalizarMatriculaCpf(matricula) + Constantes.SUBLINHADO + data;
                System.out.println("##BUGLOCALIZACAO buscarServidor chave: " + chave);
                CacheServidores cacheServidores = (CacheServidores) Cache.getInstancia(CacheServidores.class);
                servidor = cacheServidores.get(chave, matricula, dataBase);

                if (servidor != null) {
                    System.out
                            .println("##BUGLOCALIZACAO buscarServidor servidor matricula: " + servidor.getMatricula());

                    System.out
                            .println("##BUGLOCALIZACAO buscarServidor servidor nomeChefe: " + servidor.getNomeChefe());
                }
            }
        } catch (BCConsultaInvalidaException e) {
            LOG.error(MSG_LOG_ERRO_BUSCAR_SERVIDOR, e);
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_095, METODO_BUSCAR_SERVIDOR, e);
        }
        return servidor;
    }

    public ServidorVO buscarServidor(String matricula) {
        return this.buscarServidor(matricula, null);
    }

    private String obterDataBaseFormatadaChaveCache(Date dataBase) {
        return dataBase == null ? NULL : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS).format(dataBase);
    }

    public ServidorVO buscarServidorPorLoginSemExcecao(String login) {

        ServidorVO servidor = null;

        try {
            servidor = this.buscarServidorPorLogin(login);
        } catch (BCConsultaInvalidaException e) {
            LOG.error(ERRO_BUSCAR_SERVIDOR_POR_LOGIN + login, e);
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, login, e);
        } catch (BCRInfraException e) {
            LOG.error(MSG_LOG_ERRO_BUSCAR_SERVIDOR + login, e);
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, login, e);
        }

        return servidor;
    }

    public List<ComponenteOrganizacionalVO> consultarUnidadesAtivas() {
        ArrayList<ComponenteOrganizacionalVO> lista = null;
        try {
            lista = ((CacheUnidades) Cache.getInstancia(CacheUnidades.class)).getUnidades();
        } catch (BCConsultaInvalidaException e) {
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, "consultarUnidadesAtivas", e);
        }
        return lista;
    }

    public List<ServidorVO> consultarServidoresAtivos(String rotuloComponenteOrganizacional) {
        ArrayList<ServidorVO> lista = null;
        CacheServidoresPorUnidade cacheServidoresPorUnidade =
                (CacheServidoresPorUnidade) Cache.getInstancia(CacheServidoresPorUnidade.class);

        ArrayList<String> listaRotulo = new ArrayList<String>(Arrays.asList(rotuloComponenteOrganizacional.trim()));
        ConsultaServidores consulta = new ConsultaServidores();
        consulta.setBuscarServidoresHierarquiaInferior(true);
        consulta.setBuscarLotacoes(true);
        consulta.setComponenteOrganizacionalRotulos(listaRotulo);
        try {
            lista = cacheServidoresPorUnidade.get(rotuloComponenteOrganizacional.trim(), consulta);
        } catch (BCConsultaInvalidaException e) {
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, METODO_CONSULTAR_SERVIDORES, e);
        }
        return lista;
    }

    public ServidorVO buscarServidorPorLogin(String login) throws BCConsultaInvalidaException {
        ServidorVO servidor = null;
        CacheServidores cacheServidores = (CacheServidores) Cache.getInstancia(CacheServidores.class);
        String chave = Util.obterLoginMinusculo(login) + Constantes.SUBLINHADO + NULL;
        servidor = cacheServidores.get(chave, login);
        return servidor;
    }

    protected void lancarNegocioException(String chave, String metodo, Exception exececao) {
        ErrorMessage erro = new ErrorMessage(chave, metodo, COMPONENTE_BC_PESSOA);
        throw new NegocioException(erro, exececao);
    }

    public ServidorVO obterServidorLogado() {

        String login = Util.obterLoginAtual();
        return buscarServidorPorLoginSemExcecao(login);
    }

    public String obterMatriculaUsuarioLogado() {

        return obterServidorLogado().getMatricula();
    }

    public ExercicioFuncaoComissionadaVO buscarExercicioFuncaoComissionada(String matricula) {
        ExercicioFuncaoComissionadaVO exercicioFuncaoComissionada = null;

        CacheExercicioFunCom cacheExercicioFunCom =
                (CacheExercicioFunCom) Cache.getInstancia(CacheExercicioFunCom.class);

        try {
            if (StringUtils.isNotBlank(matricula)) {
                exercicioFuncaoComissionada = cacheExercicioFunCom.getExercicio(Util.normalizarMatriculaCpf(matricula));
            }
        } catch (BCConsultaInvalidaException e) {
            LOG.error(MSG_LOG_ERRO_BUSCAR_EXERCICIO_FUNCAO_COMISSIONADA, e);
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, "buscarExercicioFuncaoComissionada", e);
        }
        return exercicioFuncaoComissionada;
    }

    public ArrayList<FuncaoComissionadaVO> consultarTodasFuncoesComissionadas() {
        ArrayList<FuncaoComissionadaVO> lista = null;
        CacheFuncoesComissionadas cacheFuncoesComissionadas =
                (CacheFuncoesComissionadas) Cache.getInstancia(CacheFuncoesComissionadas.class);
        if (lista == null) {
            try {
                lista = cacheFuncoesComissionadas.getFuncoes();
            } catch (BCConsultaInvalidaException e) {
                lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055, "consultarTodasFuncoesComissionadas", e);
            }
        }
        return lista;
    }

    public boolean matriculaValida(String matricula) {

        return buscarServidor(matricula) != null;
    }

    public ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo(String rotulo, Date dataBase) {
        ComponenteOrganizacionalVO componente = null;

        CacheCompOrganizacional cacheComponente =
                (CacheCompOrganizacional) Cache.getInstancia(CacheCompOrganizacional.class);

        try {
            String dataChave = obterDataBaseFormatadaChaveCache(dataBase);
            componente = cacheComponente.get(rotulo.trim(), dataBase, dataChave);
        } catch (BCConsultaInvalidaException e) {
            // Se a consulta for inválida, não encontra e pronto
            e.getMessage(); // Isso aqui engana o checkstyle
        }

        return componente;
    }

    public ChefiaVO buscarChefia(ConsultaChefia consulta) {
        ChefiaVO chefiaVO = null;
        try {
            consulta.setComponenteOrganizacionalRotulo(consulta.getComponenteOrganizacionalRotulo().trim());
            CacheChefiaPorLocalizacao cacheChefiaPorLocalizacao =
                    (CacheChefiaPorLocalizacao) Cache.getInstancia(CacheChefiaPorLocalizacao.class);
            String dataBase = obterDataBaseFormatadaChaveCache(consulta.getDataBase());
            String chave = consulta.getComponenteOrganizacionalRotulo() + Constantes.SUBLINHADO + dataBase;
            System.out.println("##BUGLOCALIZACAO buscarChefia chave: " + chave);
            chefiaVO = cacheChefiaPorLocalizacao.get(chave, consulta);
        } catch (BCConsultaInvalidaException e) {
            LOG.error(ERRO_BUSCAR_LOCALIZACAO_POR_ROTULO + consulta.getComponenteOrganizacionalRotulo(), e);
            lancarNegocioException(ConstantesMensagens.MSG_ERRO_GERAL_055,
                    consulta.getComponenteOrganizacionalRotulo(), e);
        }
        return chefiaVO;
    }

    public ChefiaVO buscarChefiaPorSubstitutoEventual(ConsultaChefiaSubstitutoEventual consulta)
            throws BCConsultaInvalidaException {
        ChefiaVO chefiaVO = null;

        CacheChefiaPorSubstitutoEventual cacheChefiaPorSubstitutoEventual =
                (CacheChefiaPorSubstitutoEventual) Cache.getInstancia(CacheChefiaPorSubstitutoEventual.class);
        String dataBase = obterDataBaseFormatadaChaveCache(consulta.getDataBase());
        String chave = Util.normalizarMatriculaCpf(consulta.getMatriculaSubstitutoEventual()) + Constantes.SUBLINHADO
                + dataBase;

        chefiaVO = cacheChefiaPorSubstitutoEventual.get(chave, consulta);

        return chefiaVO;

    }

    public String buscarLocalizacao() {
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        return usuarioAplicacao.getServidorVO().getLocalizacaoAtual();
    }

    public List<ComponenteOrganizacionalVO> consultarComponentesOrganizacionais(String rotulo) {

        ArrayList<ComponenteOrganizacionalVO> lista = null;
        CacheListCompOrganizacional cacheComponente =
                (CacheListCompOrganizacional) Cache.getInstancia(CacheListCompOrganizacional.class);
        try {
            String dataAtual = obterDataBaseFormatadaChaveCache(DataUtil.getDateTimeAtual().toDate());
            lista = cacheComponente.get(rotulo.trim(), dataAtual);
        } catch (BCConsultaInvalidaException e) {
            // Se a consulta for inválida, não encontra e pronto
            e.getMessage(); // Isso aqui engana o checkstyle
        }
        return lista;
    }

}
