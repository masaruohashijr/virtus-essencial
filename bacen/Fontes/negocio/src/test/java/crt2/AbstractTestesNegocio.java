package crt2;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.TimeZone;

import listeners.DatabaseStrictListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.comparators.core.ComparatorDate;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.core.flow.PluginPause;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultFilter;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Warning;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.PluginScripts;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.database.impl.NullEmptyHandlerDefault;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import crt2.util.DataSourceProviderSpring;
import crt2.util.URIResolver;

public abstract class AbstractTestesNegocio {

    private static final String DELIQ_GLIQ1_GTBHO = "DELIQ/GLIQ1/GTBHO";

    // variável usada para identificar se a execução está sendo feita em ambiente windows (entenda-se máquina do
    // desenvolvedor) ou no Jenkins (ambiente Linux).
    public static boolean ehWindows = System.getProperty("os.name", "linux").toLowerCase().indexOf("windows") >= 0;

    // deinf.thiagol: para usar o banco em memória nos testes sempre é só colocar false. O comportamento padrão é: em
    // desenvolvimento usar base externa, e no Jenkins usar base em memória.
    public static boolean usarBaseExterna = ehWindows; // para habilitar banco local, coloque true, e ligue o banco via HSQL.bat antes de rodar os testes.

    @BeforeClass
    public static void preparaNegocio() throws Exception {
        ajustesGerais();
        prepararTagsNegocio();
        prepararExpressoes();
        prepararInclusoesNegocio();
        configuraUsuarioPadrao();
        configurarCaches();
    }

    private static void configurarCaches() {
        //		new CacheUnidades(1);
        //		new CacheServidores(1); 
        //		new CacheServidoresPorUnidade(1);
    }

    @BeforeClass
    public static void timezone() {
        // ajuste global para o HSQLDB entender que não deve mudar o TimeZone.
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-00:00"));
        DateTimeZone.setDefault(DateTimeZone.forID("UTC"));

    }

    private static void ajustesGerais() {
        SisapsUtil.setExecucaoTeste(true);

        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PluginPause.FEATURE_PAUSE_CONDITION, ehWindows);
        //        fm.add(AbstractConverterTimezone.FEATURE_TIMEZONE, "America/Recife");
        fm.add(IResultSet.FEATURE_RESULT_FILTER, new IResultFilter() {
            @Override
            public boolean accept(IResult result) {
                return result.getStatus() != Warning.INSTANCE;
            }
        });

        NullEmptyHandlerDefault nHandler = new NullEmptyHandlerDefault();
        nHandler.setEmptyVal("@e");
        fm.add(IDatabase.FEATURE_NULL_EMPTY_HANDLER, nHandler);
    }

    private static void prepararTagsNegocio() throws URISyntaxException, PluginException {
        IPluginFactory factory = SRServices.get(IPluginFactory.class);

        // preparando a tag de banco
        IFeatureManager fm = SRServices.getFeatureManager();
        {
            fm.add(PluginConnection.FEATURE_PROVIDER_INSTANCE, new DataSourceProviderSpring());
            fm.add(PluginConnection.FEATURE_REUSE, true);
            fm.add(PluginSchemaLoader.FEATURE_PROVIDER_INSTANCE, new SchemaLoaderXOM());
            fm.add(PluginSchemaLoader.FEATURE_REUSE, true);
            fm.add(PluginSchema.FEATURE_REUSE, true);
            fm.add(PluginDatabase.FEATURE_PROVIDER_INSTANCE, new IDatabase[] {new DatabaseDefault() {
                @Override
                protected String getAdjustContent(IContext context, INodeHolder nh) throws DatabaseException {
                    try {
                        String previous = null;
                        if (nh.hasAttribute("xml")) {
                            previous = UtilNode.getChildrenAsString(nh.getNode());
                        } else {
                            previous = nh.getValue();
                        }
                        String value =
                                UtilEvaluator.replace(nh.getAttribute(INodeHolder.ATTRIBUTE_VALUE, previous), context,
                                        true);
                        // if text has changed... adjust on screen.
                        if (previous != null && !previous.equals(value)) {
                            nh.setValue(value);
                        }
                        return value;
                    } catch (PluginException e) {
                        throw new DatabaseException(e);
                    }
                }

            }});
            fm.add(PluginDatabase.FEATURE_REUSE, true);
            // time comparators tolerance of 1000 milliseconds.
            fm.add(ComparatorDate.FEATURE_TOLERANCE, 3000L);
            fm.add(PluginInclude.FEATURE_RESOLVER, new URIResolver());

            fm.add(IDatabase.FEATURE_LISTENERS, Arrays.asList(new DatabaseStrictListener()));
        }

        IPluginGroup grupo = new PluginGroupImpl();
        {
            PluginConnection connection = new PluginConnection();
            grupo.add(connection);

            PluginSchemaLoader schemaLoader = new PluginSchemaLoader();

            grupo.add(schemaLoader);

            PluginSchema schema = new PluginSchema();
            schema.setSource("/sgbd.cfg.xml");
            grupo.add(schema);

            PluginDatabase database = new PluginDatabase();
            grupo.add(database);

            PluginScripts drop = new PluginScripts();
            drop.setFailsafe(true);
            drop.setScripts(new URI[] {new File("src/test/resources/sgbd_drop.sql").toURI()});
            grupo.add(drop);

            PluginScripts create = new PluginScripts();
            create.setFailsafe(true);
            create.setScripts(new URI[] {new File("src/test/resources/sgbd_final.sql").toURI()});
            grupo.add(create);
        }
        factory.bind(PluginKind.ELEMENT, "banco", grupo);

        IPluginGroup grupo2 = new PluginGroupImpl();
        {
            PluginConnection connection = new PluginConnection();
            grupo2.add(connection);

            PluginSchemaLoader schemaLoader = new PluginSchemaLoader();

            grupo2.add(schemaLoader);

            PluginSchema schema = new PluginSchema();
            schema.setSource("/sgbd2.cfg.xml");
            grupo2.add(schema);

            PluginDatabase database = new PluginDatabase();
            grupo2.add(database);

            PluginScripts drop = new PluginScripts();
            drop.setFailsafe(true);
            drop.setScripts(new URI[] {new File("src/test/resources/sgbd_drop.sql").toURI()});
            grupo2.add(drop);

            PluginScripts create = new PluginScripts();
            create.setFailsafe(true);
            create.setScripts(new URI[] {new File("src/test/resources/sgbd_final.sql").toURI()});
            grupo2.add(create);
        }
        factory.bind(PluginKind.ELEMENT, "banco2", grupo2);
    }

    protected static void prepararInclusoesNegocio() throws Exception {
        IPluginFactory factory = SRServices.get(IPluginFactory.class);
        PluginInclude inclusao = new PluginInclude();
        inclusao.setHref(AbstractTestesNegocio.class.getResource("dados_basicos/MOCK_BCPESSOA_stubs_dadoque.html")
                .toURI().toString());
        factory.bind(PluginKind.ELEMENT, "pessoa", inclusao);

        PluginInclude regra = new PluginInclude();
        regra.setHref(AbstractTestesNegocio.class.getResource("dados_basicos/REGRAS_PERFIS_ACESSO_GERAL.html").toURI()
                .toString());
        factory.bind(PluginKind.ELEMENT, "regra", regra);

        PluginInclude tmp = new PluginInclude();
        tmp.setHref(AbstractTestesNegocio.class
                .getResource("dominio/comum/dadoque/TestE1_carga_metodologia_e_parametros_dadoque.html").toURI()
                .toString());
        factory.bind(PluginKind.ELEMENT, "parametros", tmp);

        PluginInclude tmpNovo = new PluginInclude();
        tmpNovo.setHref(AbstractTestesNegocio.class
                .getResource("dominio/comum/dadoque/TestE1_carga_nova_metodologia_e_parametros_dadoque.html").toURI()
                .toString());
        factory.bind(PluginKind.ELEMENT, "parametrosNovo", tmpNovo);

        PluginInclude entidadeSupervisionavel = new PluginInclude();
        entidadeSupervisionavel.setHref(AbstractTestesNegocio.class
                .getResource("dominio/comum/dadoque/TestE10_1_carga_inicial_de_ES_via_Sigas_dadoque.html").toURI()
                .toString());
        factory.bind(PluginKind.ELEMENT, "entidadeSupervisionavel", entidadeSupervisionavel);

        PluginInclude matriz = new PluginInclude();
        matriz.setHref(AbstractTestesNegocio.class
                .getResource(
                        "dominio/comum/dadoque/TestE3_2_Edicao_de_matriz_alteracao_e_exclusao_de_linhas_dadoque.html")
                .toURI().toString());
        factory.bind(PluginKind.ELEMENT, "matriz", matriz);

    }

    private static void prepararExpressoes() throws URISyntaxException {
        IExpressionFactory ef = SRServices.getExpressionFactory();
        final String padraoDataString = "dd/MM/yyyy";
        ef.bindModel("hoje", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().toString(padraoDataString);
            }
        });
        ef.bindModel("ontem", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().minusDays(1).toString(padraoDataString);
            }
        });
        ef.bindModel("antesDeOntem", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().minusDays(2).toString(padraoDataString);
            }
        });
        ef.bindModel("amanha", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().plusDays(1).toString(padraoDataString);
            }
        });
        ef.bindModel("hora", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new DateTime().toString(Constantes.FORMATO_HORA);
            }
        });
        ef.bindModel("dataAtualMaisUmAno", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().plusYears(1).toString(padraoDataString);
            }
        });
        ef.bindModel("dataAtualMaisDoisAnos", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                return new LocalDate().plusYears(2).toString(padraoDataString);
            }
        });

        String name = "pattern";
        ef.bindValue(name, "HH:mm:ss").bindClass("dt", DateTime.class);
        ef.bindValue(name, Constantes.FORMATO_DATA_HORA).bindClass("dtHora", DateTime.class);
        ef.bindValue("pastaDeAnexosNegocio", new File(AbstractTestesNegocio.class.getResource("anexos").toURI()));
    }

    private static void configuraUsuarioPadrao() {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuarioTest());
        usuario.setNome(usuario.getLogin());
        usuario.setMatricula("51850257");
        usuario.setServidorVO(addServidorVO(usuario, DELIQ_GLIQ1_GTBHO));
        UsuarioCorrente.set(usuario);
    }

    private static ServidorVO addServidorVO(UsuarioAplicacao usuario, String localizacao) {
        ServidorVO novo = new ServidorVO();
        novo.setLogin(usuario.getLogin());
        novo.setMatricula(usuario.getMatricula());
        novo.setLocalizacao(localizacao);
        novo.setSituacao("116");
        return novo;
    }

    public void logar(String login) {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuarioTest(login));
        ServidorVO servidorVO =
                SpringUtils.get().getBean(BcPessoaAdapter.class).buscarServidorPorLoginSemExcecao(usuario.getLogin());
        usuario.setNome(servidorVO.getNome());
        usuario.setMatricula(servidorVO.getMatricula());
        usuario.setServidorVO(servidorVO);
        UsuarioCorrente.set(usuario);
    }

    public void logar(String login, String matricula) {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuarioTest(login));
        usuario.setNome(usuario.getLogin());
        usuario.setMatricula(matricula);
        usuario.setServidorVO(addServidorVO(usuario, DELIQ_GLIQ1_GTBHO));
        UsuarioCorrente.set(usuario);
    }

    public void logar(String login, String matricula, String localizacao) {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuarioTest(login));
        usuario.setNome(usuario.getLogin());
        usuario.setMatricula(matricula);
        usuario.setServidorVO(addServidorVO(usuario, localizacao));
        UsuarioCorrente.set(usuario);
    }

    public void logar(String login, ServidorVO servidorVO, boolean validar) {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuarioTest(login));
        usuario.setNome(usuario.getLogin());
        usuario.setMatricula(servidorVO.getMatricula());
        usuario.setServidorVO(servidorVO);
        UsuarioCorrente.set(usuario);
    }

    private static final class ProvedorInformacoesUsuarioTest implements ProvedorInformacoesUsuario {
        private String login = "deliq.sisliq101";

        public ProvedorInformacoesUsuarioTest() {
        }

        public ProvedorInformacoesUsuarioTest(String login) {
            this.login = login;
        }

        @Override
        public String getLogin() {
            return login;
        }

        @Override
        public boolean isUserInRole(String role) {
            return true;
        }
    }

    @BeforeScenario
    public void limparSessao() {
        AnaliseQuantitativaAQTMediator.get().limpaSessao();
        AvaliacaoRiscoControleMediator.get().limpaSessao();
    }
}