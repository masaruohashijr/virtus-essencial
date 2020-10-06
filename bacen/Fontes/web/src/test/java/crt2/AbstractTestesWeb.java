package crt2;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.jetty.PluginStartJetty;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.impl.FinderXPath;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;
import org.specrunner.webdriver.impl.WebDriverFactoryIe;

import br.gov.bcb.sisaps.web.page.DefaultPage;
import crt2.login.TestLogin;

public abstract class AbstractTestesWeb extends AbstractTestesNegocio {

    // deinf.thiagol: indica se o teste deve ou não usar o HtmlUnit como
    // browser. O default é, quando não for Windows (máquina de desenvolvedor)
    // deve-se usá-lo.
    public static final boolean usarHtmlUnit = true; // !ehWindows
    public static final boolean usarJsCKEditor = true;

    @BeforeClass
    public static void preparaWeb() throws Exception {
        prepararTagsWeb();
        prepararBrowser();
        prepararExpressoes();
        prepararXPaths();
        timezone();
    }

    private static void prepararTagsWeb() throws Exception {
        IPluginFactory factory = SRServices.get(IPluginFactory.class);

        // pode-se associar um plugin a qualquer tag HTML (predefinida ou
        // inventada), nesse caso associa-se o jetty ao elemento 'jetty',
        // sempre que houver um <jetty/> no teste o jetty será iniciado ou
        // reutilizado.
        PluginStartJetty jetty = new PluginStartJetty();
        jetty.setFile("/jetty.xml");
        jetty.setDynamic(true);
        jetty.setReuse(true);
        factory.bind(PluginKind.ELEMENT, "jetty", jetty);

        // logins que viram tags HTML
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("./src/main/config/jetty/realm.properties");
        properties.load(in);
        in.close();
        for (Object s : properties.keySet()) {
            String[] split = String.valueOf(s).split("_");
            String valorPropriedade = properties.getProperty(s.toString());
            String[] valores = String.valueOf(valorPropriedade).split(",");
            PluginInclude usuario = new PluginInclude();
            usuario.setHref(TestLogin.class.getResource("login.html").toURI().toString() + "?unidade=" + split[0]
                    + "&usuário=" + split[1] + "&senha=" + valores[0]);
            factory.bind(PluginKind.ELEMENT, split[1], usuario);
        }

        // plugin que dispara sempre que no texto aparecer a palavra logout.
        PluginInclude logout = new PluginInclude();
        logout.setDir(AbstractTestesWeb.class.getResource("./logout").toURI().toString());
        logout.setHref("logout.html");
        factory.bind(PluginKind.ELEMENT, "logout", logout);
    }

    private static void prepararBrowser() {
        IFeatureManager fm = SRServices.getFeatureManager();
        DefaultPage.setUsarHtmlUnit(!usarJsCKEditor);
        // ajuste para o driver Chrome
        if (!usarHtmlUnit) {
            // // ajuste para o driver IE
            //            fm.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryIe.class.getName());
            //            fm.add(WebDriverFactoryIe.FEATURE_DRIVER, "bin/IEDriverServer.exe");
            // ajuste para o driver Chrome.
            fm.add(WebDriverFactoryChrome.FEATURE_CHROME, "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe");
            fm.add(WebDriverFactoryChrome.FEATURE_DRIVER, "bin/chromedriver.exe");
            fm.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY, WebDriverFactoryChrome.class.getName());

        }
    }

    private static void prepararExpressoes() throws URISyntaxException {
        // na fábrica de expressões podemos associar diversos valores
        // predefinidos, ou dinâmicos. Esses nomes podem ser usados em
        // qualquer lugar no texto.
        IExpressionFactory ef = SRServices.getExpressionFactory();
        ef.bindModel("pausaSeIE", new IModel<Long>() {
            @Override
            public Long getObject(IContext context) throws SpecRunnerException {
                Object tipo = SRServices.getFeatureManager().get(PluginBrowser.FEATURE_WEBDRIVER_FACTORY);
                if (ehWindows && tipo != null && !tipo.equals(WebDriverFactoryIe.class.getName())) {
                    // esperando 2s se o navegador for IE.
                    System.out.println("Vai esperar 2s pelo IE...");
                    return 2000L;
                } else {
                    return 0L;
                }
            }
        });

        ef.bindModel("usuarioAdmin", new IModel<Boolean>() {
            @Override
            public Boolean getObject(IContext context) throws SpecRunnerException {
                String login = String.valueOf(context.getByName("usuário"));
                return login.equalsIgnoreCase("admin");
            }
        });
        ef.bindValue("pastaDeAnexos", new File(AbstractTestesWeb.class.getResource("anexos").toURI()));
    }

    private static void prepararXPaths() {
        // é possível criar macros de XPaths para serem usados de forma direta.
        // vide o login.html, observe que a ação de clicar em 'Entrar' já usa
        // esse novo construtor.
        FinderXPath.get().addStrategy("conter", "//input[contains(@value,'{0}')]");
        FinderXPath.get().addStrategy("legenda", "//table/caption[contains(text(),'{0}')]/..");
        FinderXPath.get().addStrategy("codigoTela", "//span[contains(text(), '{0}')]");
    }
}
