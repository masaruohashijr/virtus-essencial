/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.iterator.ComponentHierarchyIterator;
import org.joda.time.DateTime;

import br.gov.bcb.sisaps.adaptadores.data.DataAtualProvider;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaARCInspetorPage;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPage;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.GerenciarES;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPageResumido;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaPage;
import br.gov.bcb.sisaps.web.page.painelConsulta.PerfilConsultaResumidoPage;
import br.gov.bcb.sisaps.web.page.painelGerente.PerfilGerentePage;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;
import br.gov.bcb.wicket.breadcrumb.ICrumb;
import br.gov.bcb.wicket.breadcrumb.LabelCrumb;
import br.gov.bcb.wicket.breadcrumb.PageLinkCrumb;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Página padrão do sistema.
 * 
 */

public abstract class DefaultPage extends AbstractBacenWebPage implements ISisApsPage {
    private static final String TAMANHO_MAXIMO_DA_TRILHA_FOI_DETECTADA = 
            "Uma tentativa de exceder o tamanho máximo da trilha foi detectada: ";

    private static final BCLogger LOG = BCLogFactory.getLogger(DefaultPage.class);
	
	private static final int TAMANHO_MAXIMO_TRILHA = 10;	

    private static boolean usarHtmlUnit;

    private static final long serialVersionUID = 1L;

    protected WebMarkupContainer scriptErro;

    private PageReference paginaAnterior;

    private boolean formSubmetido;

    private boolean ressubmissaoPermitida = true;

    private boolean isSubirTelaAoSalvar = true;

    private boolean criarLinkTrilha = true;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        scriptErro = new WebMarkupContainer("scriptErro") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(getPage().hasErrorMessage()
                        || (isSubirTelaAoSalvar && getPage().get("feedback").isVisible()));
            }
        };
        scriptErro.setOutputMarkupPlaceholderTag(true);
        scriptErro.setOutputMarkupId(true);
        scriptErro.setMarkupId(scriptErro.getId());
        add(scriptErro);
        addCkEditorJS();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new PriorityHeaderItem(StringHeaderItem.forString(
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(DefaultPage.class, "sisaps.css")));
        response.render(JavaScriptHeaderItem
                .forReference(new PackageResourceReference(DefaultPage.class, "mascara.js")));
        if (!isUsarHtmlUnit()) {
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DefaultPage.class,
                    "ckEditorUtils.js")));
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        ComponentHierarchyIterator iterator = visitChildren(Component.class);
        while (iterator.hasNext()) {
            Component next = iterator.next();
            next.setOutputMarkupId(true);
        }
    }

    public void setFormSubmetido(boolean formSubmetido) {
        if (!this.ressubmissaoPermitida) {
            this.formSubmetido = formSubmetido;
        }
    }

    public void permitirRessubmissao(boolean permissao) {
        this.ressubmissaoPermitida = permissao;
    }

    public boolean getFormSubmetido() {
        return formSubmetido;
    }

    @Override
    protected DateTime getHoje() {
        return ((DataAtualProvider) SpringUtilsExtended.get().getBean(DataAtualProvider.NOME)).getDateTime();
    }

    @Override
    protected void adicionarMigalhas(List<ICrumb> migalhas) {
        migalhas.add(0, new LabelCrumb(Model.of(this.getTitulo())));
        adicionarMigalhasPaginasAnteriores(migalhas);
        migalhas.add(0, new LabelCrumb(Model.of("Início")));
    }

    private void adicionarMigalhasPaginasAnteriores(List<ICrumb> migalhas) {
        if (paginaAnterior == null) {
            return;
        }
        DefaultPage tmp = (DefaultPage) paginaAnterior.getPage();
        while (tmp != null) {
            if (migalhas.size() < TAMANHO_MAXIMO_TRILHA) {
                if (tmp.criarLinkTrilha) {
                    IPageLink pageLink = new PageLink(tmp);
                    migalhas.add(0, new PageLinkCrumb(pageLink, Model.of(tmp.getTitulo())));
                } else {
                    migalhas.add(0, new LabelCrumb(Model.of(tmp.getTitulo())));
                }
                tmp = tmp.getPaginaAnterior();
            } else {
                LOG.warn(TAMANHO_MAXIMO_DA_TRILHA_FOI_DETECTADA + migalhas);
                tmp = null;
            }
        }
    }

    public void avancarParaNovaPagina(DefaultPage novaPagina) {
        novaPagina.setPaginaAnterior(this);
        super.setResponsePage(novaPagina);
    }

    public void avancarParaNovaPagina(DefaultPage novaPagina, DefaultPage paginaAnteiro) {
        novaPagina.setPaginaAnterior(paginaAnteiro);
        super.setResponsePage(novaPagina);
    }

    protected void setPaginaAnterior(DefaultPage paginaAnterior) {
        this.paginaAnterior = new PageReference(paginaAnterior.getPageId());
    }

    public void voltar() {
        setResponsePage(getPaginaAnterior());
    }

    public DefaultPage getPaginaAnterior() {
        if (paginaAnterior == null) {
            return null;
        }

        return (DefaultPage) paginaAnterior.getPage();
    }

    public DefaultPage getPaginaAtual() {
        return (DefaultPage) super.getPage();
    }

    private final class PageLink implements IPageLink {
        private final Page pagina;

        public PageLink(Page pagina) {
            this.pagina = pagina;
        }

        @Override
        public Class<? extends Page> getPageIdentity() {
            return pagina.getPage().getPageClass();
        }

        @Override
        public Page getPage() {
            return pagina.getPage();
        }
    }

    /**
     * Link que por padrão retorna para a página anterior da página atual.
     */
    @SuppressWarnings("rawtypes")
    protected class LinkVoltar extends Link {
        public LinkVoltar() {
            super(ConstantesWeb.WID_BOTAO_VOLTAR);
        }

        public LinkVoltar(String id) {
            super(id);
        }

        @Override
        public void onClick() {
            if (paginaAnterior == null) {
                setResponsePage(HomePage.class);
            } else {
                setResponsePage(getPaginaAnterior());
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected class LinkVoltarPerfilRisco extends Link {

        private final Integer pkCiclo;

        public LinkVoltarPerfilRisco(Integer pkCiclo) {
            super(ConstantesWeb.WID_BOTAO_VOLTAR);
            this.pkCiclo = pkCiclo;
        }

        public LinkVoltarPerfilRisco(String id, Integer pkCiclo) {
            super(id);
            this.pkCiclo = pkCiclo;
        }

        @Override
        public void onClick() {
            if (getPaginaAnterior() instanceof PerfilDeRiscoPage) {
                DefaultPage novaPagina = new PerfilDeRiscoPage(pkCiclo);
                novaPagina.setPaginaAnterior(getPaginaAnterior().getPaginaAnterior());
                super.setResponsePage(novaPagina);
            } else {
                setResponsePage(getPaginaAnterior());
            }
        }

    }

    protected void setMenuVisivel(boolean isVisivel) {
        get(WID_MENU).setVisible(isVisivel);
    }

    public static boolean isUsarHtmlUnit() {
        return usarHtmlUnit;
    }

    public static void setUsarHtmlUnit(boolean usarHtmlUnit) {
        DefaultPage.usarHtmlUnit = usarHtmlUnit;
    }

    private void addCkEditorJS() {
        WebMarkupContainer ckEditor = new WebMarkupContainer("ckEditor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!usarHtmlUnit);
            }
        };
        add(ckEditor);
    }

    public void setSubirTelaAoSalvar(boolean isSubirTelaAoSalvar) {
        this.isSubirTelaAoSalvar = isSubirTelaAoSalvar;
    }

    public Component getFeedBack() {
        return get(DefaultPage.WID_FEEDBACK);
    }

    public String[] atualizarDadosPagina(DefaultPage paginaAtual) {
        String[] dados = new String[3];
        dados[0] = getTrilha(paginaAtual);
        dados[1] = getCodigoTela();
        dados[2] = getTitulo();
        return dados;
    }

    public String getTrilha(DefaultPage paginaAtual) {
        StringBuffer valor = new StringBuffer("Início-");
        List<String> listaTitulos = new ArrayList<String>();
        DefaultPage tmp = paginaAtual;
        while (tmp != null) {
        	if (listaTitulos.size() < TAMANHO_MAXIMO_TRILHA) { 
        		listaTitulos.add(tmp.getTitulo());
        		tmp = tmp.getPaginaAnterior();
        	} else {
        		LOG.warn(TAMANHO_MAXIMO_DA_TRILHA_FOI_DETECTADA + listaTitulos);
        		tmp = null;
        	}
        }
        int i = listaTitulos.size() - 1;
        while (i >= 0) {
            valor.append(listaTitulos.get(i));
            i--;
            if (i >= 0) {
                valor.append("-");
            }
        }
        return valor.toString();
    }

    public boolean perfilSupervisor(DefaultPage paginaAtual) {
        return isPaginaAnteriorOuAtualConsultaCicloPage(getPainelPerfilBase(paginaAtual));

    }

    private boolean isPaginaAnteriorOuAtualConsultaCicloPage(DefaultPage paginaAtual) {
        return (paginaAtual.getPaginaAnterior() instanceof ConsultaCicloPage
                || paginaAtual instanceof ConsultaCicloPage)
                || (paginaAtual.getPaginaAnterior() instanceof GerenciarNotaSintesePage
                        || paginaAtual instanceof GerenciarNotaSintesePage);
    }

    public boolean perfilInspetor(DefaultPage paginaAtual) {
        return isPaginaAnteriorOuAtualInspetorPage(getPainelPerfilBase(paginaAtual));
    }

    public boolean perfilGerente(DefaultPage paginaAtual) {
        return isPaginaAnteriorOuAtualGerentePage(getPainelPerfilBase(paginaAtual));
    }

    private boolean isPaginaAnteriorOuAtualInspetorPage(DefaultPage paginaAtual) {
        return paginaAtual.getPaginaAnterior() instanceof ConsultaARCInspetorPage
                || paginaAtual instanceof ConsultaARCInspetorPage;
    }

    private boolean isPaginaAnteriorOuAtualGerentePage(DefaultPage paginaAtual) {
        return (paginaAtual.getPaginaAnterior() instanceof PerfilGerentePage
                || paginaAtual.getPaginaAnterior() instanceof GerenciarES)
                || (paginaAtual instanceof PerfilGerentePage || paginaAtual instanceof GerenciarES);
    }

    public boolean perfilConsulta(DefaultPage paginaAtual) {
        return isPaginaAnteriorOuAtualConsultaPage(getPainelPerfilBase(paginaAtual));
    }
    
    public boolean perfilConsultaResumido(DefaultPage paginaAtual) {
        return isPaginaAnteriorOuAtualConsultaResumidoPage(getPainelPerfilBase(paginaAtual));
    }

    private boolean isPaginaAnteriorOuAtualConsultaPage(DefaultPage paginaBase) {
        return paginaBase.getPaginaAnterior() instanceof PerfilConsultaPage
                || paginaBase instanceof PerfilConsultaPage
                || isAcessoPerfilDeRiscoAPSPaineis(paginaBase)
                && (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                        || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS));
    }

	private boolean isAcessoPerfilDeRiscoAPSPaineis(DefaultPage paginaBase) {
		return paginaBase.getPaginaAnterior() == null && paginaBase instanceof PerfilDeRiscoPage
			&& ((PerfilDeRiscoPage)paginaBase).isAcessoPorParametro();
	}
    
    private boolean isPaginaAnteriorOuAtualConsultaResumidoPage(DefaultPage paginaBase) {
        return paginaBase.getPaginaAnterior() instanceof PerfilConsultaResumidoPage
                || paginaBase instanceof PerfilConsultaResumidoPage
                || isAcessoPerfilDeRiscoResumidoAPSPaineis(paginaBase)
                && RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS);
    }
    
    private boolean isAcessoPerfilDeRiscoResumidoAPSPaineis(DefaultPage paginaBase) {
		return paginaBase.getPaginaAnterior() == null && paginaBase instanceof PerfilDeRiscoPageResumido
			&& ((PerfilDeRiscoPageResumido)paginaBase).isAcessoPorParametro();
	}

    public void setCriarLinkTrilha(boolean criarLinkTrilha) {
        this.criarLinkTrilha = criarLinkTrilha;
    }

    protected DefaultPage getPainelPerfilBase(DefaultPage paginaAtual) {
        HashSet<DefaultPage> paginas = new HashSet<DefaultPage>();
        
        DefaultPage pagina = paginaAtual;
        while (true) {
            if (pagina.getPaginaAnterior() == null) {
                return pagina;
            } else {
                if (paginas.add(pagina)) {
                    pagina = pagina.getPaginaAnterior();
                } else {
                    return pagina;
                }
            }
        }
    }

    @Override
    public PerfilAcessoEnum getPerfilPorPagina() {
        if (perfilSupervisor(getPaginaAtual())) {
            return PerfilAcessoEnum.SUPERVISOR;
        }

        if (perfilConsulta(getPaginaAtual())) {
            return PerfilAcessoEnum.CONSULTA_TUDO;
        }
        
        if (perfilConsultaResumido(getPaginaAtual())) {
            return PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS;
        }

        if (perfilInspetor(getPaginaAtual())) {
            return PerfilAcessoEnum.INSPETOR;
        }

        if (perfilGerente(getPaginaAtual())) {
            return PerfilAcessoEnum.GERENTE;
        }

        return null;

    }
    
    @Override
    protected String getLinkDoLogo() {
        return Constantes.ENDERECO_SISTEMA;
    }
}