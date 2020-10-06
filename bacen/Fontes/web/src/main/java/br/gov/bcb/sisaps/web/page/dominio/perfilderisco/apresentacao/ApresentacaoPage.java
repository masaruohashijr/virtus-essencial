package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.ApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.ISisApsPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.GerarRelatorioApresentacaoLink;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide01;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide03;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide11;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide13;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide14;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide18;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide20;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide21;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide25;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.Slide27;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.SlideAnexo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.SlideConstante;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide.SlideTexto;

public class ApresentacaoPage extends WebPage implements ISisApsPage {

    @SpringBean
    private ApresentacaoMediator apresentacaoMediator;

    // O perfil acessando a apresentação.
    private final PerfilAcessoEnum perfilMenu;
    private List<Slide> slidesPages;

    // Construtor estático.
    static {
        // DEclarações
        IPackageResourceGuard packageResourceGuard;
        SecurePackageResourceGuard guard;

        // Recupera as configurações da aplicação.
        packageResourceGuard = Application.get().getResourceSettings().getPackageResourceGuard();
        if (packageResourceGuard instanceof SecurePackageResourceGuard) {
            guard = (SecurePackageResourceGuard) packageResourceGuard;

            // Adiciona o tipo de fonte usado pelo bootstrap.
            guard.addPattern("+*.woff2");
        }
    }

    // Construtor
    public ApresentacaoPage(PerfilRisco perfilRisco, Ciclo ciclo, PerfilAcessoEnum perfilMenu) {
        super();

        perfilRisco.getCiclo().setApresentacao(ApresentacaoMediator.get().criar(perfilRisco.getCiclo()));
        // Declarações
        ApresentacaoVO apresentacaoVO;

        // Inicializações
        this.perfilMenu = perfilMenu;

        // Recupera a apresentação.
        apresentacaoVO = apresentacaoMediator.montar(perfilRisco, perfilMenu);

        // Adiciona dados gerais da apresentação.
        addDadosGerais(ciclo);

        // Adiciona os slides.
        slidesPages = montarSlides(perfilRisco, ciclo, apresentacaoVO);
        add(new ListView<Panel>("idPaineisSlides", slidesPages) {
            // Adiciona os slides.
            @Override
            protected void populateItem(ListItem<Panel> item) {
                // Adiciona o painel.
                item.add(item.getModelObject());
            }
        });

        addOrReplace(new GerarRelatorioApresentacaoLink("btnImpressao2", new File("est.pdf"), perfilRisco, ciclo,
                apresentacaoVO, getPage(), slidesPages.size(), perfilMenu));
    }

    // Retorna o perfil da página.
    @Override
    public PerfilAcessoEnum getPerfilPorPagina() {
        return this.perfilMenu;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new PriorityHeaderItem(StringHeaderItem
                .forString("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE8\">")));
        response.render(new PriorityHeaderItem(StringHeaderItem
                .forString("<meta http-equiv=\"CACHE-CONTROL\" content=\"NO-CACHE\">")));
        response.render(new PriorityHeaderItem(StringHeaderItem
                .forString("<meta http-equiv=\"PRAGMA\" content=\"NO-CACHE\">")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DefaultPage.class, "jquery.js")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(ApresentacaoPage.class,
                "ApresentacaoPage.js")));
    }

    // Adiciona dados gerais da apresentação.
    private void addDadosGerais(Ciclo ciclo) {
        // Logo do BCB.
        add(new Image("idImagemLogo", "img/logo_bc.png"));

        // Nome do conglomerado.
        add(new Label("idNomeConglomerado", ciclo.getEntidadeSupervisionavel().getNomeConglomeradoFormatado()));
    }

    // Monta os slides.
    private List<Slide> montarSlides(PerfilRisco perfilRisco, Ciclo ciclo, ApresentacaoVO apresentacaoVO) {
        // Declarações
        List<Slide> slides;

        // Inicializações
        slides = new LinkedList<Slide>();

        // Monta os slides das seções.
        for (SecaoApresentacaoEnum secao : SecaoApresentacaoEnum.values()) {
            // Verifica qual é a seção.
            switch (secao) {
            // Seções constantes sem highlight.
                case ANALISE_QUANTITATIVA:
                case ANALISE_QUALITATIVA:
                case CONCLUSAO:
                    slides.add(new SlideConstante(secao, false, apresentacaoVO));
                    break;

                // Seções constantes com highlight.
                case VOTACAO_NOTA_QUANTITATIVA:
                case VOTACAO_NOTA_QUALITATIVA:
                case VOTACAO_NOTA_FINAL:
                case VOTACAO_PERSPECTIVA:
                case VOTACAO_PROPOSTA_ACOES:
                    slides.add(new SlideConstante(secao, true, apresentacaoVO));
                    break;

                // Seções de anexo.
                case EVOLUCAO_DAS_AVALIACOES:
                case ESTRUTURA_JURIDICA_SOCIETARIA_ORGANIZACIONAL:
                case GRUPO_ECONOMICO:
                case ORGANOGRAMA_FUNCIONAL:
                case NOTAS_QUANTITATIVAS_EVOLUCAO:
                case NOTAS_QUALITATIVAS_EVOLUCAO:
                case ANEXO_ANALISE_ECONOMICO_FINANCEIRA:
                    adicionarAnexos(slides, apresentacaoVO, secao);
                    break;

                // Seções de Texto e anexo.
                case PERFIL:
                case PROPOSTA_ACOES_CICLO:
                case CARACTERISTICAS_UNIDADES_ATIVIDADES:
                case ESTRATEGIAS:
                    adicionarTextos(slides, apresentacaoVO, secao);
                    adicionarAnexos(slides, apresentacaoVO, secao);
                    break;

                // Seções de texto.

                case INFORMACOES_OUTROS_DEPARTAMANETOS:
                case INFORMACOES_OUTROS_ORGAOS:
                case EQUIPE:
                    adicionarTextos(slides, apresentacaoVO, secao);
                    break;

                // Slides únicos.
                case SRC:
                    slides.add(new Slide01(ciclo, apresentacaoVO));
                    break;
                case TRABALHOS_REALIZADOS:
                    slides.add(new Slide03(perfilRisco, ciclo, apresentacaoVO));
                    break;
                case SITUACAO:
                    new Slide11(slides, apresentacaoVO);
                    break;
                case POSICAO_FINANCEIRA_RESULTADOS:
                    slides.add(new Slide13(perfilRisco, apresentacaoVO));
                    break;
                case SINTESE_NOTA_QUANTITATIVA:
                    slides.add(new Slide14(apresentacaoVO, perfilRisco, perfilMenu));
                    break;
                case IDENTIFICACAO_UNIDADES_ATIVIDADES:
                    slides.add(new Slide18(apresentacaoVO));
                    break;
                case ANALISE_QUALITATIVA_POR_RISCOS:
                    slides.add(new Slide20(apresentacaoVO));
                    break;
                case NOTAS_ARCS:
                    slides.add(new Slide21(apresentacaoVO, perfilRisco));
                    break;
                case NOTA_FINAL_INSTITUICAO:
                    slides.add(new Slide25(apresentacaoVO, perfilRisco, perfilMenu));
                    break;
                case PERSPECTIVA_INSTITUICAO:
                    slides.add(new Slide27(apresentacaoVO));
                    break;
            }
        }

        for (int i = 1; i <= slides.size(); i++) {
            slides.get(i - 1).setTotalPaginas(slides.size());
            slides.get(i - 1).setIndicePagina(i);
        }

        return slides;
    }

    // Adiciona os anexos de uma seção.
    private void adicionarAnexos(List<Slide> slides, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        // Declarações
        List<AnexoApresentacaoVO> anexosVO;

        // Recupera os anexos da seção.
        anexosVO = apresentacaoVO.getAnexosVO(secao);
        
        Collections.sort(anexosVO, new Comparator<AnexoApresentacaoVO>() {
            @Override
            public int compare(AnexoApresentacaoVO a1, AnexoApresentacaoVO a2) {
                return a1.getLink().compareToIgnoreCase(a2.getLink());
            }
        });

        // Monta os slides dos anexos.
        for (AnexoApresentacaoVO anexoVO : anexosVO) {
            slides.add(new SlideAnexo(secao, anexoVO, apresentacaoVO));
        }
    }

    // Adiciona os textos de uma seção.
    private void adicionarTextos(List<Slide> slides, ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
        // Declarações
        List<TextoApresentacaoVO> textosVO;

        // Recupera os anexos da seção.
        textosVO = apresentacaoVO.getTextosVO(secao);
        
        if (secao.equals(SecaoApresentacaoEnum.PERFIL)) {
        	ordenarCampos(textosVO);
        }

        // Verifica se há texto para adicionar.
        if (textosVO.size() > 0) {
            slides.add(new SlideTexto(secao, textosVO, apresentacaoVO));
        }
    }

	private void ordenarCampos(List<TextoApresentacaoVO> textosVO) {
		Comparator<TextoApresentacaoVO> c = new Comparator<TextoApresentacaoVO>() {

			@Override
			public int compare(TextoApresentacaoVO o1, TextoApresentacaoVO o2) {
				return o1.getCampo().getCodigo() - o2.getCampo().getCodigo();
			}
        	
		};
        
        Collections.sort(textosVO, c);
	}
}
