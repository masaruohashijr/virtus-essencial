package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

@SuppressWarnings("serial")
public class PainelResumoElementosPreenchidoArcs extends PainelSisAps {
    private static final String LINK_ELEMENTOAQT = "linkElemento";
    private Label lblNotaCalculadaInspetor;
    private Label lblNotaAjustadaSupervisor;
    private AvaliacaoRiscoControle arc;
    private String notaCalculadaInspetor;
    private String notaAjustInspetor;
    private String tendenciaInspetor;
    private String tendenciaVigente;
    private List<Elemento> elementosVigente = new ArrayList<Elemento>();
    private AvaliacaoRiscoControle arcVigente;
    private Label lblNotaAjustadaInspetor;
    private final Matriz matriz;
    private AvaliacaoRiscoControle arcRascunho;
    private final boolean isPerfilAtual;

    public PainelResumoElementosPreenchidoArcs(String id, AvaliacaoRiscoControle arc, Matriz matriz,
            AvaliacaoRiscoControle arcRascunho, boolean isPerfilAtual) {
        super(id);
        this.arc = arc;
        this.matriz = matriz;
        this.arcRascunho = arcRascunho;
        this.isPerfilAtual = isPerfilAtual;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        arc = AvaliacaoRiscoControleMediator.get().buscar(arc.getPk());
        arcRascunho = AvaliacaoRiscoControleMediator.get().buscar(arcRascunho.getPk());
        arcVigente =
                arc.getAvaliacaoRiscoControleVigente() == null ? null : AvaliacaoRiscoControleMediator.get().buscar(
                        arc.getAvaliacaoRiscoControleVigente().getPk());

        notaCalculadaInspetor = AvaliacaoRiscoControleMediator.get().getNotaCalculadaInspetor(arc);
        notaAjustInspetor = arc.getAvaliacaoInspetorDescricao();
        tendenciaInspetor = arc.getTendenciaARCInspetorValor();
        tendenciaVigente = arc.getTendenciaARCVigenteValor();
        addComponentes();

    }

    private void addComponentes() {
        addListaElementoAQTs();
        addNotasInspetor();
        addNotasVigente();
    }

    private void addNotasVigente() {
        addNotaCalculadaVigente(arcVigente);
        addNotaAjustadaVigente();
        addTendenciaVigente();
    }

    private void addNotasInspetor() {
        addNotaCalculadaInspetor();
        addNotaAjustadaInspetor();
        addTendenciaInspetor();
    }

    protected void addListaElementoAQTs() {
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
        notaInspetor(listItemElementoAQT, elemento);
        notaVigente(listItemElementoAQT, elemento);
    }

    private void notaInspetor(final ListItem<Elemento> itens, Elemento elemento) {
        itens.add(new Label("notaInspetor", elemento.getParametroNotaInspetor() == null ? "" : elemento
                .getParametroNotaInspetor().getDescricaoValor()));
    }

    private void notaVigente(final ListItem<Elemento> item, Elemento elemento) {
        Elemento elementoVigente =
                ElementoMediator.get().obterElementoCorrespondenteARCVigente(elementosVigente, elemento);
        LabelLinhas labelNotaVigente =
                new LabelLinhas("notaVigente", elementoVigente == null ? "" : elementoVigente.getNotaSupervisor());
        item.addOrReplace(labelNotaVigente);
    }

    private void addNotaAjustadaInspetor() {
        lblNotaAjustadaInspetor = new Label("notaAjustadaInspetor", notaAjustInspetor);
        addOrReplace(lblNotaAjustadaInspetor);
    }


    private void addNotaCalculadaVigente(AvaliacaoRiscoControle arcVigente) {
        addOrReplace(new Label("notaCalculadaArrastoVigente", arcVigente == null ? "" : AvaliacaoRiscoControleMediator
                .get().getNotaCalculadaFinal(arcVigente)));
    }

    private void addNotaAjustadaVigente() {
        String notaAjustadaVigente =
                AvaliacaoRiscoControleMediator.get().notaArcIndicadorCorec(arcRascunho, matriz.getCiclo(),
                        getPerfilPorPagina(), true, isPerfilAtual);
        if ("".equals(notaAjustadaVigente)) {
            notaAjustadaVigente = arcVigente == null ? "" : arcVigente.getAvaliacaoArcDescricaoValor();
        }

        addOrReplace(new Label("notaAjustadaVigente", notaAjustadaVigente));
    }

    private void addTendenciaVigente() {
        addOrReplace(new Label("notaTendenciaVigente", tendenciaVigente));
    }


    private void addNotaCalculadaInspetor() {
        lblNotaCalculadaInspetor = new Label("notaCalculadaArrastoInspetor", notaCalculadaInspetor);
        lblNotaCalculadaInspetor.setOutputMarkupId(true);
        addOrReplace(lblNotaCalculadaInspetor);
    }

    private void addTendenciaInspetor() {
        addOrReplace(new Label("notaTendenciaInspetor", tendenciaInspetor));
    }


    public void atualizarNotaCalculada(AjaxRequestTarget target) {

        if (lblNotaCalculadaInspetor != null) {
            lblNotaCalculadaInspetor.setDefaultModelObject(notaCalculadaInspetor);
            target.add(lblNotaCalculadaInspetor);
        }

        if (notaAjustInspetor != null) {
            lblNotaAjustadaSupervisor.setDefaultModelObject(notaAjustInspetor);
            target.add(lblNotaAjustadaSupervisor);
        }

    }


    public AvaliacaoRiscoControle getArc() {
        return arc;
    }

    public void setArc(AvaliacaoRiscoControle arc) {
        this.arc = arc;
    }

}