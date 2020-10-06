package br.gov.bcb.sisaps.web.page.dominio.aqt.consulta;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.ControleExpansivel;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

public class PainelResumoSinteseAQT extends PainelSisAps {

    private String A = "*A";

    private static final String T_RESUMO_SINTESE_AQT_CICLO = "tResumoSinteseAQTCiclo";
    @SpringBean
    private NotaAjustadaAEFMediator notaAjustadaAEFMediator;

    private PerfilRisco perfilRisco;

    private final boolean isExibirTitulo;

    private NotaAjustadaAEF notaAjustadaAEF;

    private ParametroNotaAQT notaAjustadaCorec;

    private final PerfilAcessoEnum perfilMenu;

    private final boolean isNovoQuadro;

    private String notaCalculada;

    public PainelResumoSinteseAQT(String id, PerfilRisco perfilRisco, boolean isExibirTitulo,
            PerfilAcessoEnum perfilMenu, boolean isNovoQuadro) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.isExibirTitulo = isExibirTitulo;
        this.perfilMenu = perfilMenu;
        this.isNovoQuadro = isNovoQuadro;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (isNovoQuadro) {
            this.notaAjustadaAEF = notaAjustadaAEFMediator.buscarNotaAjustadaRascunho(perfilRisco.getCiclo());
        } else {
            this.notaAjustadaAEF = notaAjustadaAEFMediator.buscarNotaAjustadaAEF(perfilRisco.getCiclo(), perfilRisco);
        }
        notaAjustadaCorec =
                AjusteCorecMediator.get().notaAjustadaCorecAEF(perfilRisco, perfilRisco.getCiclo(), perfilMenu);
        addComponentes();

    }

    private void addComponentes() {
        String notaMatriz = "";
        String justificativaNotaAjustadaMatriz = "";
        WebMarkupContainer tabelaPainel = new WebMarkupContainer(T_RESUMO_SINTESE_AQT_CICLO);
        tabelaPainel.setMarkupId(getMarkupIdTabelaPainel());
        addOrReplace(tabelaPainel);
        addTitulo(tabelaPainel);
        addContainerNotaAjustada(notaMatriz, justificativaNotaAjustadaMatriz, tabelaPainel);
        addLabelNotaCalculada(tabelaPainel);
    }

    private void addLabelNotaCalculada(WebMarkupContainer tabelaPainel) {
        A = AnaliseQuantitativaAQTMediator.get().getNotaCalculadaAEF(perfilRisco, getPerfilPorPagina(), isNovoQuadro);
        Label labelNotaCalculada = new Label("colunaNotaCalculada", A);
        tabelaPainel.addOrReplace(labelNotaCalculada);

        notaCalculada = A;
        Label labelNotaRefinada =
                new Label("colunaNotarRefinada", AnaliseQuantitativaAQTMediator.get().getNotaRefinadaAEF(perfilRisco,
                        getPerfilPorPagina(), isNovoQuadro));

        tabelaPainel.addOrReplace(labelNotaRefinada);
    }

    private void addContainerNotaAjustada(String notaMatriz, String justificativaNotaAjustadaMatriz,
            WebMarkupContainer tabelaPainel) {
        WebMarkupContainer linhaJustificativaNotaAjustada =
                new WebMarkupContainer("idLinhaJustificativaNotaAjustadaAQT") {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(exibirNotaAjustada() || notaAjustadaCorec != null);
                    }
                };
        String justificativaNotaAjustada =
                notaAjustadaAEF == null || notaAjustadaCorec != null ? "" : notaAjustadaAEF.getJustificativa();

        linhaJustificativaNotaAjustada.addOrReplace(new Label("idJustificativaNotaAjustadaAQT",
                justificativaNotaAjustada).setEscapeModelStrings(false));
        tabelaPainel.addOrReplace(linhaJustificativaNotaAjustada);

        ControleExpansivel controle = new ControleExpansivel("controle", false, linhaJustificativaNotaAjustada) {
            @Override
            public String getMarkupIdLink() {
                if (notaAjustadaAEF == null) {
                    return "idExpandirJustificativaNotaAjustada";
                } else {
                    return "idExpandirJustificativaNotaAjustada_" + notaAjustadaAEF.getPk();
                }
            }
        };
        controle.setOutputMarkupPlaceholderTag(true);
        controle.setVisible(PerfilDeRiscoPage.class.equals(getPage().getClass()));
        WebMarkupContainer colunaControle = colunaControle(tabelaPainel);
        colunaControle.addOrReplace(controle);
        colunaNotaAjustada(tabelaPainel);
        WebMarkupContainer colunaNotaAjustadaValor = colunaNotaAjustadaValor(tabelaPainel, notaAjustadaAEF);
        notaAjustada(notaAjustadaAEF, colunaNotaAjustadaValor);
    }

    private void notaAjustada(final NotaAjustadaAEF notaAjustadaAEF, WebMarkupContainer colunaNotaAjustadaValor) {
        String notaAjustada;
        if (naoExisteNotaRascunhoParaNovoQuadro(notaAjustadaAEF) 
                || (!isNovoQuadro && notaAjustadaCorec != null)) {
        	String descricaoCorec = PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS.equals(perfilMenu) ? "" : " (Corec)";
            if (notaAjustadaCorec.getIsNotaElemento() == null 
                    || (notaAjustadaCorec.getIsNotaElemento() != null 
                    && notaAjustadaCorec.getIsNotaElemento().booleanValue())) {
                notaAjustada = notaAjustadaCorec.getDescricaoValor() + descricaoCorec;
            } else {
                notaAjustada = notaAjustadaCorec.getDescricao() + descricaoCorec;
            }

        } else {
            notaAjustada = "";
            if (notaAjustadaAEF != null && notaAjustadaAEF.getParamentroNotaAQT() != null) {
                if (notaAjustadaAEF.getParamentroNotaAQT().getIsNotaElemento() == null 
                        || (notaAjustadaAEF.getParamentroNotaAQT().getIsNotaElemento() != null
                        && notaAjustadaAEF.getParamentroNotaAQT().getIsNotaElemento().booleanValue())) {
                        notaAjustada = notaAjustadaAEF.getParamentroNotaAQT().getDescricaoValor();
                    } else {
                        notaAjustada = notaAjustadaAEF.getParamentroNotaAQT().getDescricao();
                    }
                }
        }
        colunaNotaAjustadaValor.addOrReplace(new Label("notaAjustadaAQT", notaAjustada));
    }

    private boolean naoExisteNotaRascunhoParaNovoQuadro(final NotaAjustadaAEF notaAjustadaAEF) {
        return isNovoQuadro && notaAjustadaAEF == null && notaAjustadaCorec != null;
    }

    private WebMarkupContainer colunaControle(WebMarkupContainer tabelaPainel) {
        WebMarkupContainer colunaControle = new WebMarkupContainer("colunaControle") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(exibirNotaAjustada());
            }

        };
        tabelaPainel.addOrReplace(colunaControle);
        return colunaControle;
    }

    private WebMarkupContainer colunaNotaAjustadaValor(WebMarkupContainer tabelaPainel,
            final NotaAjustadaAEF notaAjustadaAEF) {
        WebMarkupContainer colunaNotaAjustadaValor = new WebMarkupContainer("colunaNotaAjustadaValor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(exibirNotaAjustada() || notaAjustadaCorec != null);
            }
        };
        tabelaPainel.addOrReplace(colunaNotaAjustadaValor);
        return colunaNotaAjustadaValor;
    }

    private void colunaNotaAjustada(WebMarkupContainer tabelaPainel) {
        WebMarkupContainer colunaNotaAjustada = new WebMarkupContainer("colunaNotaAjustada") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(exibirNotaAjustada() || (!isNovoQuadro && notaAjustadaCorec != null));
            }
        };
        tabelaPainel.addOrReplace(colunaNotaAjustada);
    }

    private void addTitulo(WebMarkupContainer tabelaPainel) {
        WebMarkupContainer titulo = new WebMarkupContainer("idTitulo") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirTitulo);
            }
        };

        tabelaPainel.addOrReplace(titulo);
    }

    private boolean exibirNotaAjustada() {
        if (isNovoQuadro) {
            return (notaAjustadaAEF != null && notaAjustadaAEF.getParamentroNotaAQT() != null) 
                    || (notaAjustadaCorec != null && notaAjustadaAEF == null);
        } else {
            return (notaAjustadaAEF != null && notaAjustadaAEF.getParamentroNotaAQT() != null) && notaAjustadaCorec == null;
        }
    }

    public String getMarkupIdTabelaPainel() {
        return T_RESUMO_SINTESE_AQT_CICLO;
    }

    public String getNotaCalculada() {
        return notaCalculada;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

}
