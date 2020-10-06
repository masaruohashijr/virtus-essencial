package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

@SuppressWarnings("serial")
public class PainelResumoElementosAnefs extends PainelSisAps {
    private static final String COLOR = "style";
    private static final String LINK_ELEMENTOAQT = "linkElemento";
    private Label lblNotaCalculadaInspetor;
    private Label lblNotaAjustadaSupervisor;
    private Label lblNotaAjustadaFinal;

    private AnaliseQuantitativaAQT aqt;
    private boolean exibirColunaFinal;
    private boolean exibirColunaSupervisor;
    private boolean exibirColunaInspetor;
    private boolean exibirColunaVigente;
    private final WebMarkupContainer wmcExibirTituloFinal = new WebMarkupContainer("wmcExibirTituloFinal");
    private final WebMarkupContainer wmcExibirTituloSupervisor = new WebMarkupContainer("wmcExibirTituloSupervisor");
    private final WebMarkupContainer wmcExibirTituloInspetor = new WebMarkupContainer("wmcExibirTituloInspetor");
    private final WebMarkupContainer wmcExibirTituloVigente = new WebMarkupContainer("wmcExibirTituloVigente");
    private final WebMarkupContainer wmcExibirTitulos = new WebMarkupContainer("wmcExibirTitulos");
    private String notaCalculadaFinalSupervisor;
    private String notaCalculadaInspetor;
    private String notaAjustInspetor;
    private String notaAjustSupervisor;
    private List<ElementoAQT> elementosANEFVigente = new ArrayList<ElementoAQT>();
    private String notaAjustada;
    private AnaliseQuantitativaAQT anefVigente;
    private Label lblNotaAjustadaInspetor;
    private final boolean isPerfilRiscoAtual;

    public PainelResumoElementosAnefs(String id, AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        super(id);
        this.aqt = aqt;
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        anefVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        exibirColunaFinal =
                AnaliseQuantitativaAQTMediator.get().exibirColunaFinal(aqt.getEstado())
                        || (getPaginaAtual() instanceof ConsultaAQT) || (getPaginaAtual() instanceof AnaliseAQT);
        exibirColunaSupervisor =
                AnaliseQuantitativaAQTMediator.get().exibirColunaSupervisor(aqt.getEstado())
                        && !(getPaginaAtual() instanceof ConsultaAQT) || (getPaginaAtual() instanceof AnaliseAQT);
        exibirColunaInspetor =
                AnaliseQuantitativaAQTMediator.get().exibirColunaInspetor(aqt.getEstado())
                        && !(getPaginaAtual() instanceof ConsultaAQT) || (getPaginaAtual() instanceof EdicaoAQT)
                        || (getPaginaAtual() instanceof AnaliseAQT);
        exibirColunaVigente =
                AnaliseQuantitativaAQTMediator.get().exibirColunaVigente(aqt.getEstado())
                        && !(getPaginaAtual() instanceof ConsultaAQT) || (getPaginaAtual() instanceof AnaliseAQT);

        notaCalculadaFinalSupervisor = aqt.getNotaCalculadaSupervisorOuInspetor();
        notaCalculadaInspetor = aqt.getNotaCalculadaInspetor();
        notaAjustInspetor = AnaliseQuantitativaAQTMediator.get().notaAjustadaInspetor(aqt);
        notaAjustSupervisor = AnaliseQuantitativaAQTMediator.get().notaAjustadaSupervisorOuInspetor(aqt);
        addComponentes();

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
        addNotaCalculadaVigente(anefVigente);
        addNotaAjustadaVigente(anefVigente);
    }

    private void addNotasFinal() {
        addNotaCalculadaFinal();
        addNotaAjustadaFinal();
    }

    private void addNotasSupervisor() {
        addNotaCalculadaSupervisor();
        addNotaAjustadaSupervisor();
    }

    private void addNotasInspetor() {
        addNotaCalculadaInspetor();
        addNotaAjustadaInspetor();
    }

    protected void addListaElementoAQTs() {
        setListaElementosANEFVigente();
        ListView<ElementoAQT> listView =
                new ListView<ElementoAQT>("lista", ElementoAQTMediator.get()
                        .buscarElementosOrdenadosDoAnef(aqt.getPk())) {
                    @Override
                    protected void populateItem(ListItem<ElementoAQT> listItemElementoAQT) {
                        ElementoAQT elementoAQT = (ElementoAQT) listItemElementoAQT.getDefaultModelObject();
                        listItemElementoAQT.setDefaultModel(new CompoundPropertyModel<ElementoAQT>(elementoAQT));
                        WebMarkupContainer component = new WebMarkupContainer(LINK_ELEMENTOAQT);
                        component.add(new AttributeAppender(ConstantesWeb.HREF, Model.of("#idTituloElemento"
                                + elementoAQT.getPk())));
                        component.add(new Label("parametroElemento.descricao"));
                        listItemElementoAQT.add(component);
                        notas(listItemElementoAQT, elementoAQT);
                    }
                };
        addOrReplace(listView);
    }

    private void setListaElementosANEFVigente() {

        if (anefVigente != null) {
            elementosANEFVigente = anefVigente.getElementos();
        }
    }

    private void notas(final ListItem<ElementoAQT> listItemElementoAQT, ElementoAQT elementoAQT) {
        notaFinal(listItemElementoAQT, elementoAQT);
        notaSupervisor(listItemElementoAQT, elementoAQT);
        notaInspetor(listItemElementoAQT, elementoAQT);
        notaVigente(listItemElementoAQT, elementoAQT);
    }

    private void notaSupervisor(final ListItem<ElementoAQT> itens, final ElementoAQT elementoAQT) {
        Label labelNotaSupervisor = new Label("notaSupervisor", elementoAQT.getNotaSupervisor()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotasElementos(elementoAQT))));
            }
        };

        labelNotaSupervisor.add(new AttributeAppender(COLOR, corNotaSupervisor(compararNotasElementos(elementoAQT))));
        labelNotaSupervisor.setVisible(exibirColunaSupervisor);
        itens.addOrReplace(labelNotaSupervisor);
    }

    private void notaInspetor(final ListItem<ElementoAQT> itens, ElementoAQT elementoAQT) {
        itens.add(new Label("notaInspetor", elementoAQT.getParametroNotaInspetor() == null ? "" : elementoAQT
                .getParametroNotaInspetor().getDescricaoValor()).setVisible(exibirColunaInspetor));
    }

    private void notaFinal(final ListItem<ElementoAQT> itens, ElementoAQT elementoAQT) {
        itens.addOrReplace(new Label("notaFinal", elementoAQT.getNotaSupervisor()).setVisible(exibirColunaFinal));
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

    private void notaVigente(final ListItem<ElementoAQT> item, ElementoAQT elementoAQT) {
        ElementoAQT elementoAnefVigente =
                ElementoAQTMediator.get().obterElementoANEFVigente(elementosANEFVigente, elementoAQT);
        LabelLinhas labelNotaVigente =
                new LabelLinhas("notaVigente", elementoAnefVigente == null ? ""
                        : elementoAnefVigente.getNotaSupervisor());
        labelNotaVigente.setVisible(exibirColunaVigente);
        item.addOrReplace(labelNotaVigente);
    }

    private void addNotaAjustadaSupervisor() {
        lblNotaAjustadaSupervisor =
                new Label("notaAjustadaSupervisor", AnaliseQuantitativaAQTMediator.get()
                        .notaAjustadaSupervisorOuInspetor(aqt)) {
                    @Override
                    protected void onConfigure() {
                        add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotaAjustada())));
                    }

                };
        lblNotaAjustadaSupervisor.setVisible(exibirColunaSupervisor);
        addOrReplace(lblNotaAjustadaSupervisor);
    }

    private void addNotaAjustadaInspetor() {
        lblNotaAjustadaInspetor = new Label("notaAjustadaInspetor", notaAjustInspetor);
        addOrReplace(lblNotaAjustadaInspetor.setVisible(exibirColunaInspetor));
    }

    private void addNotaAjustadaFinal() {
        notaAjustada =
                AnaliseQuantitativaAQTMediator.get().notaAjustadaFinal(aqt, getPaginaAtual().getPerfilPorPagina(),
                        isPerfilRiscoAtual);
        String notaAjustadaTelaConsulta =
                notaAjustada.length() > 6 ? notaAjustada.substring(0, notaAjustada.length() - 7) : notaAjustada;
        lblNotaAjustadaFinal =
                new Label("notaAjustadaFinal", (getPaginaAtual() instanceof ConsultaAQT) ? notaAjustadaTelaConsulta
                        : notaAjustada);
        lblNotaAjustadaFinal.setVisible(exibirColunaFinal);
        addOrReplace(lblNotaAjustadaFinal);
    }

    private void addNotaCalculadaVigente(AnaliseQuantitativaAQT aqtVigente) {
        addOrReplace(new Label("notaCalculadaArrastoVigente", aqtVigente == null ? ""
                : aqtVigente.getMediaCalculadaFinal()).setVisible(exibirColunaVigente));
    }

    private void addNotaAjustadaVigente(AnaliseQuantitativaAQT aqtVigente) {
        addOrReplace(new Label("notaAjustadaVigente", aqtVigente == null ? "" : AnaliseQuantitativaAQTMediator.get()
                .notaAjustadaVigente(aqtVigente, isPerfilRiscoAtual)).setVisible(exibirColunaVigente));
    }

    private void addNotaCalculadaSupervisor() {
        Label labelNotaCalculada = new Label("notaCalculadaArrastoSupervisor", notaCalculadaFinalSupervisor) {
            @Override
            protected void onConfigure() {
                add(AttributeModifier.replace(COLOR, corNotaSupervisor(compararNotaCalculada())));
            }
        };
        labelNotaCalculada.setOutputMarkupId(true);
        labelNotaCalculada.setVisible(exibirColunaSupervisor);
        addOrReplace(labelNotaCalculada);
    }

    private void addNotaCalculadaInspetor() {
        lblNotaCalculadaInspetor = new Label("notaCalculadaArrastoInspetor", notaCalculadaInspetor);
        lblNotaCalculadaInspetor.setVisible(exibirColunaInspetor);
        lblNotaCalculadaInspetor.setOutputMarkupId(true);
        addOrReplace(lblNotaCalculadaInspetor);
    }

    private void addNotaCalculadaFinal() {
        addOrReplace(new Label("notaCalculadaArrastoFinal", notaCalculadaFinalSupervisor).setVisible(exibirColunaFinal));
    }

    private void addTitulos() {
        wmcExibirTituloFinal.setVisible(exibirColunaFinal);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloFinal);

        wmcExibirTituloSupervisor.setVisible(exibirColunaSupervisor);
        wmcExibirTitulos.addOrReplace(wmcExibirTituloSupervisor);

        wmcExibirTituloInspetor.setVisible(exibirColunaInspetor || (getPaginaAtual() instanceof EdicaoAQT));
        wmcExibirTitulos.addOrReplace(wmcExibirTituloInspetor);

        wmcExibirTituloVigente.setVisible(exibirColunaVigente && !(getPaginaAtual() instanceof ConsultaAQT));
        wmcExibirTitulos.addOrReplace(wmcExibirTituloVigente);

        boolean mostrarTitulo =
                wmcExibirTituloFinal.isVisible() || wmcExibirTituloSupervisor.isVisible()
                        || wmcExibirTituloInspetor.isVisible() || wmcExibirTituloVigente.isVisible();
        wmcExibirTitulos.setVisibilityAllowed(mostrarTitulo && !(getPaginaAtual() instanceof ConsultaAQT));
        addOrReplace(wmcExibirTitulos);

    }

    private boolean compararNotasElementos(ElementoAQT elemento) {
        return elemento.getParametroNotaSupervisor() != null
                && elemento.getParametroNotaInspetor() != null
                && !elemento.getParametroNotaSupervisor().getValor()
                        .equals(elemento.getParametroNotaInspetor().getValor());
    }

    public void atualizarNotaCalculadaANEF(AjaxRequestTarget target) {

        if (lblNotaAjustadaSupervisor != null) {
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
            lblNotaAjustadaInspetor.setDefaultModelObject(notaAjustInspetor);
            target.add(lblNotaAjustadaInspetor);
        }

    }

    private boolean compararNotaAjustada() {
        return aqt.getAvaliacaoSupervisor() != null || !notaAjustSupervisor.equals(notaAjustInspetor);
    }

    private boolean compararNotaCalculada() {
        return AnaliseQuantitativaAQTMediator.get().possuiNotaElementosSupervisor(aqt)
                || !notaCalculadaFinalSupervisor.equals(notaCalculadaInspetor);
    }

    public String getNotaCalculadaFinalSupervisor() {
        return notaCalculadaFinalSupervisor;
    }

    public void setNotaCalculadaFinalSupervisor(String notaCalculadaFinalSupervisor) {
        this.notaCalculadaFinalSupervisor = notaCalculadaFinalSupervisor;
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public void setAqt(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

}