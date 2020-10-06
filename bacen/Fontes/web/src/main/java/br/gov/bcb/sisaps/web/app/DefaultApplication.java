/*
 * Sistema: sisaps.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacões proprietarias.
 */
package br.gov.bcb.sisaps.web.app;

import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.Component;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.application.ComponentOnAfterRenderListenerCollection;
import org.apache.wicket.application.IComponentOnAfterRenderListener;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlRenderer;
import org.apache.wicket.request.cycle.PageRequestHandlerTracker;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.joda.time.DateTimeZone;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.HomePage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.SisApsRoleAuthorizationStrategy;
import br.gov.bcb.sisaps.web.page.dominio.agenda.AgendaPage;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.AnaliseAQT;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.DetalharAQT;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.EdicaoAQT;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaARCInspetorPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcAnalisado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcAnalise;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcConcluido;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcDelegado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcPreenchido;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.EdicaoArcPage;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.inclusao.CadastrarCicloPage;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPage;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.GerenciarES;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.painelAdministrador.ConsultaAdministradorPage;
import br.gov.bcb.sisaps.web.page.painelComite.PerfilComitePage;
import br.gov.bcb.sisaps.web.page.painelComite.TransacoesPage;
import br.gov.bcb.sisaps.web.page.painelConsulta.InicializaConsultaPage;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaPage;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaResumidoPage;
import br.gov.bcb.sisaps.web.page.painelGerente.PerfilGerentePage;
import br.gov.bcb.wicket.AbstractBacenWebApplication;
import br.gov.bcb.wicket.TamanhoPaginaComponentOnAfterRenderListener;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;
import br.gov.bcb.wicket.stuff.converters.BcWicketStuffConverterLocator;
import br.gov.bcb.wicket.stuff.protocol.http.BcWebSession;

public class DefaultApplication extends AbstractBacenWebApplication {

    @Override
    public void init() {

        super.init();
        // Favor, não remover 'America/Sao_Paulo' por causa de bugs com relação a Dados criados pelo teste.
        if (SisapsUtil.isExecucaoTeste()) {
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        } else {
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo")));
        }

        // ajustes de markup
        ajusteMarkup();

        getSecuritySettings().setAuthorizationStrategy(new SisApsRoleAuthorizationStrategy(this));

        // ajustes dos listeners
        ajustesListeners();

        // removendo os listeners desnecessários
        removendoListenersInuteis();

        menuAnaliseQualitativa();
        menuAdministracao();
        menuPorPerfil();
    }

    protected void ajusteMarkup() {
        // Importante. Nos ambientes corporativos a codificacao padrao do SO nao e ISO-8859-1.
        getMarkupSettings().setDefaultMarkupEncoding("ISO-8859-1");
        getMarkupSettings().setDefaultAfterDisabledLink(null);
        getMarkupSettings().setDefaultBeforeDisabledLink(null);
        // removendo as TAGs inúteis do wicket.
        getMarkupSettings().setStripWicketTags(true);
    }

    protected void ajustesListeners() {
        // Montar as páginas
        getRequestCycleListeners().add(new DefaultRequestCycleListener());

        getRequestCycleListeners().add(new PageRequestHandlerTracker());

        // Injetor to wicket
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        // ajusta todos os componentes para terem o outputmarkupid
        getComponentPreOnBeforeRenderListeners().add(new AjustadorOutputMarkupListener());
    }

    protected void removendoListenersInuteis() {
        // removendo listener de tamanho, que não deve ser impedimento para construção de sistema
        // bem como não deve onerar a execução de testes automatizados.
        ComponentOnAfterRenderListenerCollection lista = getComponentOnAfterRenderListeners();
        for (IComponentOnAfterRenderListener listener : lista) {
            if (listener instanceof TamanhoPaginaComponentOnAfterRenderListener) {
                lista.remove(listener);
            }
        }
    }

    private void menuAnaliseQualitativa() {
        mountPage("analisequalitativa/arc/consultaInspetor", ConsultaARCInspetorPage.class);
    }

    private void menuAdministracao() {
        mountPage("administracao/ciclo/inclusao", CadastrarCicloPage.class);
        mountPage("administracao/ciclo/consulta", ConsultaCicloPage.class);

    }

    private void menuPorPerfil() {
        mountPage("administrador", ConsultaAdministradorPage.class);
        mountPage("supervisor", ConsultaCicloPage.class);
        mountPage("inspetor", ConsultaARCInspetorPage.class);
        mountPage("painelconsulta", InicializaConsultaPage.class);
        mountPage("comite", PerfilComitePage.class);
        mountPage("processamentos", TransacoesPage.class);
        mountPage("historico", ConsultaHistoricoPage.class);
        mountPage("gerente", PerfilGerentePage.class);
        mountPage("agenda", AgendaPage.class);

        mountPage("/perfilDeRisco/#{cnpj}", PerfilDeRiscoPage.class);
        mountPage("/perfilDeRisco/#{idPerfil}", PerfilDeRiscoPage.class);
        mountPage("/edicaoArc/#{pkArc}#{pkCiclo}#{pkAtividade}", EdicaoArcPage.class);
        mountPage("/analiseArc/#{pkArc}/#{pkMatriz}/#{pkAtividade}", AnalisarArcPage.class);
        mountPage("/gerenciarSintese/#{pkEntidade}/#{pkParametro}", GerenciarNotaSintesePage.class);
        mountPage("/edicaoAnef/#{pkAnef}", EdicaoAQT.class);
        mountPage("/analiseAnef/#{pkAnef}", AnaliseAQT.class);
        mountPage("/gerenciarSinteseAnef/#{pkAnef}", GerenciarNotaSinteseAQTPage.class);
        mountPage("/gestaoDetalhesEs/#{pkCiclo}", GerenciarES.class);
        
        mountPage("/detalharAnef/#{pkAnef}", DetalharAQT.class);
        mountPage("/detalheArcAnalisado/#{pkArc}/#{pkMatriz}/#{pkAtividade}", DetalharArcAnalisado.class);
        mountPage("/detalheArcAnalise/#{pkArc}/#{pkMatriz}/#{pkAtividade}", DetalharArcAnalise.class);
        mountPage("/detalheArcConcluido/#{pkArc}/#{pkMatriz}/#{pkAtividade}", DetalharArcConcluido.class);
        mountPage("/detalheArcDelegado/#{pkArc}/#{pkMatriz}/#{pkAtividade}", DetalharArcDelegado.class);
        mountPage("/detalheArcPreenchido/#{pkArc}/#{pkMatriz}/#{pkAtividade}", DetalharArcPreenchido.class);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        RegraPerfilAcessoMediator regraPerfilAcessoMediator =
                SpringUtils.get().getBean(RegraPerfilAcessoMediator.class);

        if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.SUPERVISOR)) {
            return ConsultaCicloPage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.INSPETOR)) {
            return ConsultaARCInspetorPage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                || regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)) {
            return PerfilConsultaPage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS)) {
            return PerfilConsultaResumidoPage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.ADMINISTRADOR)) {
            return ConsultaAdministradorPage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.COMITE)) {
            return PerfilComitePage.class;
        } else if (regraPerfilAcessoMediator.isAcessoPermitido(PerfilAcessoEnum.GERENTE)) {
            return PerfilGerentePage.class;
        } else {
            return HomePage.class;
        }
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new BcWebSession<UsuarioAplicacao>(request);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        return new BcWicketStuffConverterLocator();
    }

    @Override
    protected WebResponse newWebResponse(WebRequest webRequest, HttpServletResponse httpServletResponse) {
        return new FixedServletWebResponse((ServletWebRequest) webRequest, httpServletResponse);
    }

    /*
     * Gambiarra para correção do bug de redirecionamento do wicket no websphere
     * http://stackoverflow
     * .com/questions/16787785/wicket-was-calling-url-causes-a-redirect-to-a-wrong-url-causing-404
     */
    private static class FixedServletWebResponse extends ServletWebResponse {
        private final ServletWebRequest webRequest;

        protected FixedServletWebResponse(ServletWebRequest webRequest, HttpServletResponse httpServletResponse) {
            super(webRequest, httpServletResponse);
            this.webRequest = webRequest;
        }

        @Override
        public String encodeRedirectURL(CharSequence url) {
            Url relativeUrl = Url.parse(url);
            return new UrlRenderer(webRequest).renderFullUrl(relativeUrl);
        }
    }

    @Override
    public String getTituloSistema(AbstractBacenWebPage page) {
        return "Sistema APS-SRC";
    }

    @Override
    public String getSistemaParaBilhetagem() {
        return "sisaps";
    }

    @Override
    public Component createMenuSistema(AbstractBacenWebPage page, String containerId) {
        return new AppMenu("menu");
    }
}