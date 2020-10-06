package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoARCMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaArcPerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.EdicaoArcPage;

@SuppressWarnings("serial")
public class PainelResumoElementosArcs extends PainelSisAps {
    private static final String COLOR = "style";
    private static final String LINK_ELEMENTOAQT = "linkElemento";
    private Label lblNotaCalculadaInspetor;
    private Label lblNotaAjustadaSupervisor;
    private Label lblNotaAjustadaFinal;

    private AvaliacaoRiscoControle arc;

    private boolean exibirColunaFinal;
    private boolean exibirColunaSupervisor;
    private boolean exibirColunaInspetor;
    private boolean exibirColunaVigente;
    private final WebMarkupContainer wmcNotaAjustadaSupervisor = new WebMarkupContainer("notaAjustadaSupervisor");
    private final WebMarkupContainer wmcNotaAjustadaFinal = new WebMarkupContainer("notaAjustadaFinal");
    private final WebMarkupContainer wmcExibirTituloFinal = new WebMarkupContainer("wmcExibirTituloFinal");
    private final WebMarkupContainer wmcExibirTituloSupervisor = new WebMarkupContainer("wmcExibirTituloSupervisor");
    private final WebMarkupContainer wmcExibirTituloInspetor = new WebMarkupContainer("wmcExibirTituloInspetor");
    private final WebMarkupContainer wmcExibirTituloVigente = new WebMarkupContainer("wmcExibirTituloVigente");
    private final WebMarkupContainer wmcExibirTitulos = new WebMarkupContainer("wmcExibirTitulos");
    private final WebMarkupContainer wmcExibirNotaAjustada = new WebMarkupContainer("wmcExibirNotaAjustada");
    private String notaCalculadaFinal;
    private String notaCalculadaSupervisor;
    private String notaCalculadaInspetor;
    private String notaAjustInspetor;
    private String notaAjustSupervisor;
    private String tendenciaSupervisor;
    private String tendenciaInspetor;
    private String tendenciaFinal;
    private String tendenciaVigente;
    private List<Elemento> elementosVigente = new ArrayList<Elemento>();
    private String notaAjustada;
    private AvaliacaoRiscoControle arcVigente;
    private Label lblNotaAjustadaInspetor;
    private final Matriz matriz;
    private boolean paginaConsulta;
    private boolean paginaAnalise;
    private boolean paginaEdicao;
    private AvaliacaoRiscoControle arcRascunho;
    private final boolean isPerfilRiscoAtual;

    public PainelResumoElementosArcs(String id, AvaliacaoRiscoControle arc, Matriz matriz,
            AvaliacaoRiscoControle arcRascunho, boolean isPerfilRiscoAtual) {
        super(id);
        this.arc = arc;
        this.matriz = matriz;
        this.arcRascunho = arcRascunho;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.paginaConsulta = getPaginaAtual() instanceof ConsultaArcPerfilDeRiscoPage;
        this.paginaAnalise = getPaginaAtual() instanceof AnalisarArcPage;
        this.paginaEdicao = getPaginaAtual() instanceof EdicaoArcPage;
        arc = AvaliacaoRiscoControleMediator.get().buscar(arc.getPk());
        arcRascunho = AvaliacaoRiscoControleMediator.get().buscar(arcRascunho.getPk());
        arcVigente =
                arc.getAvaliacaoRiscoControleVigente() == null ? null : AvaliacaoRiscoControleMediator.get().buscar(
                        arc.getAvaliacaoRiscoControleVigente().getPk());

        exibirColunas();

        notaCalculadaFinal = AvaliacaoRiscoControleMediator.get().getNotaCalculadaFinal(arc);
        notaCalculadaSupervisor = AvaliacaoRiscoControleMediator.get().getNotaCalculadaSupervisor(arc);
        notaCalculadaInspetor = AvaliacaoRiscoControleMediator.get().getNotaCalculadaInspetor(arc);

        notaAjustInspetor = arc.getAvaliacaoInspetorDescricao();
        notaAjustSupervisor = arc.getNotaAvaliacaoSupervisor();
        tendenciaSupervisor = arc.getTendenciaARCInspetorOuSupervisorValor();
        tendenciaInspetor = arc.getTendenciaARCInspetorValor();
        tendenciaFinal = arc.getTendenciaARCInspetorOuSupervisorValor();
        tendenciaVigente = arc.getTendenciaARCVigenteValor();
        addComponentes();

    }

    private void exibirColunas() {
        exibirColunaFinal =
                AvaliacaoRiscoControleMediator.get().exibirColunaFinal(arc.getEstado()) || paginaConsulta
                        || paginaAnalise;
        exibirColunaSupervisor =
                AvaliacaoRiscoControleMediator.get().exibirColunaSupervisor(arc.getEstado()) && !paginaConsulta
                        || paginaAnalise;

        exibirColunaInspetor =
                AvaliacaoRiscoControleMediator.get().exibirColunaInspetor(arc.getEstado()) && !paginaConsulta
                        || paginaEdicao || paginaAnalise;
        exibirColunaVigente =
                AvaliacaoRiscoControleMediator.get().exibirColunaVigente(arc.getEstado()) && !paginaConsulta
                        || paginaAnalise;
    }

    private void addComponentes() {
        addListaElementoAQTs();
        addNotasFinal();
        addNotasSupervisor();
        addNotasInspetor();
        addNotasVigente();
        addTitulos();
    }

    private void addNotasVigente() {
        addNotaCalculadaVigente(arcVigente);
        addNotaAjustadaVigente();
        addTendenciaVigente();
    }

    private void addNotasFinal() {
        addNotaCalculadaFinal();
        addNotaAjustadaFinal();
        addTendenciaFinal();
    }

    private void addNotasSupervisor() {
        addNotaCalculadaSupervisor();
        addNotaAjustadaSupervisor();
        addTendenciaSupervisor();
    }

    private void addNotasInspetor() {
        addNotaCalculadaInspetor();
        addNotaAjustadaInspetor();
        addTendenciaInspetor();
    }

    protected void addListaElementoAQTs() {
        //TODO FAZER MOSTRA APENAS ELEMENTOS COM NOTAS 

        setListaElementosANEFVigente();
        ListView<Elemento> listView =
                new ListView<Elemento>("lista", ElementoMediator.get().buscarElementosOrdenadosDoArc(arc.getPk())) {
                    @Override
                    protected void populateItem(ListItem<Elemento> listItemElemento) {
                        Elemento elemento = (Elemento) listItemElemento.getDefaultModelObject();
                        listItemElemento.setDefaultModel(new CompoundPropertyModel<Elemento>(elemento));
                        WebMarkupContainer component = new WebMarkupContainer(LINK_ELEMENTOAQT);
                        component.add(new AttributeAppender(ConstantesWeb.HREF, Model.of("#idTituloElemento"
                                + elemento.getPk())));
                        component.add(new Label("parametroElemento.nome"));
                        listItemElemento.add(component);
                        notas(listItemElemento, elemento);
                    }
                };
        addOrReplace(listView);
    }

    private void setListaElementosANEFVigente() {

        if (arcVigente != null) {
            elementosVigente = arcVigente.getElementos();
        }
    }

    private void notas(final ListItem<Elemento> listItemElementoAQT, Elemento elemento) {
        notaFinal(listItemElementoAQT, elemento);
        notaSupervisor(listItemElementoAQT, elemento);
        notaInspetor(listItemElementoAQT, elemento);
        notaVigente(listItemElementoAQT, elemento);
    }

    private void notaSupervisor(final ListItem<Elemento> itens, final Elemento elemento) {
        Label labelNotaSupervisor = new Label("notaSupervisor", elemento.getNotaSupervisor()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotasElementos(elemento))));
            }
        };

        labelNotaSupervisor.add(new AttributeAppender(COLOR, corNotaSupervisor(compararNotasElementos(elemento))));
        labelNotaSupervisor.setVisible(exibirColunaSupervisor);
        itens.addOrReplace(labelNotaSupervisor);
    }

    private void notaInspetor(final ListItem<Elemento> itens, Elemento elemento) {
        itens.add(new Label("notaInspetor", elemento.getParametroNotaInspetor() == null ? "" : elemento
                .getParametroNotaInspetor().getDescricaoValor()).setVisible(exibirColunaInspetor));
    }

    private void notaFinal(final ListItem<Elemento> itens, Elemento elemento) {
        itens.addOrReplace(new Label("notaFinal", elemento.getNotaSupervisor()).setVisible(exibirColunaFinal));
    }

    @Transient
    private IModel<String> corNotaSupervisor(final boolean possui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (possui) {
                    return "color:#000000";
                } else {
                    return "color:#999999";
                }
            }
        };
        return model;
    }

    private void notaVigente(final ListItem<Elemento> item, Elemento elemento) {
        Elemento elementoVigente =
                ElementoMediator.get().obterElementoCorrespondenteARCVigente(elementosVigente, elemento);
        LabelLinhas labelNotaVigente =
                new LabelLinhas("notaVigente", elementoVigente == null ? "" : elementoVigente.getNotaSupervisor());
        labelNotaVigente.setVisible(exibirColunaVigente);
        item.addOrReplace(labelNotaVigente);
    }

    private void addNotaAjustadaSupervisor() {

        lblNotaAjustadaSupervisor = new Label("labelNotaAjustadaSupervisor", arc.getNotaAvaliacaoSupervisor()) {
            @Override
            protected void onConfigure() {
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotaAjustada())));
            }

        };

        lblNotaAjustadaSupervisor.setVisible(!("".equals(arc.avaliacaoSupervisorDescricao()) && arc
                .possuiNotaElementosSupervisor()));
        wmcNotaAjustadaSupervisor.addOrReplace(lblNotaAjustadaSupervisor);
        wmcNotaAjustadaSupervisor.setVisible(exibirColunaSupervisor);
        wmcExibirNotaAjustada.addOrReplace(wmcNotaAjustadaSupervisor);
    }

    private void addNotaAjustadaInspetor() {
        lblNotaAjustadaInspetor = new Label("notaAjustadaInspetor", notaAjustInspetor);
        wmcExibirNotaAjustada.addOrReplace(lblNotaAjustadaInspetor.setVisible(exibirColunaInspetor));
    }

    private void addNotaAjustadaFinal() {
        notaAjustada = "";
        boolean isCOREC = false;
        if (EstadoARCEnum.CONCLUIDO.equals(arc.getEstado()) || CicloMediator.get().cicloCorec(matriz.getCiclo())) {
            notaAjustada =
                    AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(arcRascunho, matriz.getCiclo(),
                            getPerfilPorPagina(), true, isPerfilRiscoAtual);
            if (!notaAjustada.isEmpty()) {
                isCOREC = true;
            }
        }
        if ("".equals(notaAjustada)) {
            notaAjustada = AvaliacaoARCMediator.get().getAvaliacaoArcDescricaoValor(arc.getPk());
        }
        String notaAjustadaTelaConsulta =
                notaAjustada.length() > 6 ? notaAjustada.substring(0, notaAjustada.length() - 7) : notaAjustada;
        lblNotaAjustadaFinal =
                new Label("labelNotaAjustadaFinal", paginaConsulta ? notaAjustadaTelaConsulta : notaAjustada);
        lblNotaAjustadaFinal.setVisible(isCOREC
                || !("".equals(arc.avaliacaoSupervisorDescricao()) && arc.possuiNotaElementosSupervisor()));

        wmcNotaAjustadaFinal.setVisible(exibirColunaFinal);
        wmcNotaAjustadaFinal.addOrReplace(lblNotaAjustadaFinal);
        if (paginaConsulta) {
            wmcExibirNotaAjustada.setVisible(!"".equals(notaAjustadaTelaConsulta));
        } else if (exibirColunaFinal && EstadoARCEnum.CONCLUIDO.equals(arc.getEstado())) {
            wmcExibirNotaAjustada.setVisible(!"".equals(notaAjustada));
        }

        wmcExibirNotaAjustada.addOrReplace(wmcNotaAjustadaFinal);
    }

    private void addNotaCalculadaVigente(AvaliacaoRiscoControle arcVigente) {
        addOrReplace(new Label("notaCalculadaArrastoVigente", arcVigente == null ? "" : AvaliacaoRiscoControleMediator
                .get().getNotaCalculadaFinal(arcVigente)).setVisible(exibirColunaVigente));
    }

    private void addNotaAjustadaVigente() {
        String notaAjustadaVigente =
                AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(arcRascunho, matriz.getCiclo(),
                        getPerfilPorPagina(), true, isPerfilRiscoAtual);
        if ("".equals(notaAjustadaVigente)) {
            notaAjustadaVigente = arcVigente == null ? "" : arcVigente.getAvaliacaoArcDescricaoValor();
        }

        wmcExibirNotaAjustada.addOrReplace(new Label("notaAjustadaVigente", notaAjustadaVigente)
                .setVisible(exibirColunaVigente));
    }

    private void addTendenciaVigente() {
        addOrReplace(new Label("notaTendenciaVigente", tendenciaVigente).setVisible(exibirColunaVigente));
    }

    private void addNotaCalculadaSupervisor() {
        Label labelNotaCalculada = new Label("notaCalculadaArrastoSupervisor", notaCalculadaFinal) {
            @Override
            protected void onConfigure() {
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotaCalculada())));
            }
        };
        labelNotaCalculada.setOutputMarkupId(true);
        labelNotaCalculada.setVisible(exibirColunaSupervisor);
        addOrReplace(labelNotaCalculada);
    }

    private void addTendenciaSupervisor() {
        Label labelTendenciaSupervisor = new Label("notaTendenciaSupervisor", tendenciaSupervisor) {
            @Override
            protected void onConfigure() {
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararTendencia())));
            }
        };
        labelTendenciaSupervisor.setOutputMarkupId(true);
        labelTendenciaSupervisor.setVisible(exibirColunaSupervisor);
        addOrReplace(labelTendenciaSupervisor);
    }

    private void addNotaCalculadaInspetor() {
        lblNotaCalculadaInspetor = new Label("notaCalculadaArrastoInspetor", notaCalculadaInspetor);
        lblNotaCalculadaInspetor.setVisible(exibirColunaInspetor);
        lblNotaCalculadaInspetor.setOutputMarkupId(true);
        addOrReplace(lblNotaCalculadaInspetor);
    }

    private void addTendenciaInspetor() {
        addOrReplace(new Label("notaTendenciaInspetor", tendenciaInspetor).setVisible(exibirColunaInspetor));
    }

    private void addNotaCalculadaFinal() {
        addOrReplace(new Label("notaCalculadaArrastoFinal", notaCalculadaFinal).setVisible(exibirColunaFinal));
    }

    private void addTendenciaFinal() {
        addOrReplace(new Label("notaTendenciaFinal", tendenciaFinal).setVisible(exibirColunaFinal));
    }

    private void addTitulos() {
        wmcExibirTituloFinal.setVisible(exibirColunaFinal);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloFinal);

        wmcExibirTituloSupervisor.setVisible(exibirColunaSupervisor);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloSupervisor);

        wmcExibirTituloInspetor.setVisible(exibirColunaInspetor || paginaEdicao);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloInspetor);

        wmcExibirTituloVigente.setVisible(exibirColunaVigente && !paginaConsulta);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloVigente);

        boolean mostrarTitulo =
                wmcExibirTituloFinal.isVisible() || wmcExibirTituloSupervisor.isVisible()
                        || wmcExibirTituloInspetor.isVisible() || wmcExibirTituloVigente.isVisible();
        wmcExibirTitulos.setVisibilityAllowed(mostrarTitulo && !paginaConsulta);
        addOrReplace(wmcExibirTitulos);
        addOrReplace(wmcExibirNotaAjustada);

    }

    private boolean compararNotasElementos(Elemento elemento) {
        return elemento.possuiNotasupervisor();
    }

    public void atualizarNotaCalculada(AjaxRequestTarget target) {

        if (wmcNotaAjustadaSupervisor != null) {
            lblNotaAjustadaSupervisor.setDefaultModelObject(notaAjustSupervisor);
            target.add(lblNotaAjustadaSupervisor);
        }
        if (lblNotaAjustadaFinal != null) {
            lblNotaAjustadaFinal.setDefaultModelObject(notaAjustada);
            target.add(lblNotaAjustadaFinal);
        }
        if (lblNotaCalculadaInspetor != null) {
            lblNotaCalculadaInspetor.setDefaultModelObject(notaCalculadaInspetor);
            target.add(lblNotaCalculadaInspetor);
        }

        if (notaAjustInspetor != null) {
            lblNotaAjustadaSupervisor.setDefaultModelObject(notaAjustInspetor);
            target.add(lblNotaAjustadaSupervisor);
        }

    }

    private boolean compararNotaAjustada() {
        return arc.avaliacaoSupervisor() != null || !notaAjustSupervisor.equals(notaAjustInspetor);
    }

    private boolean compararNotaCalculada() {
        return AvaliacaoRiscoControleMediator.get().possuiNotaElementosSupervisor(arc)
                || !notaCalculadaInspetor.equals(notaCalculadaSupervisor);
    }

    private boolean compararTendencia() {
        return !"".equals(arc.getTendenciaARCSupervisorValor());
    }

    public AvaliacaoRiscoControle getArc() {
        return arc;
    }

    public void setArc(AvaliacaoRiscoControle arc) {
        this.arc = arc;
    }

}