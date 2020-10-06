package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.PainelAnaliseElementosAnefsNovo;

public class PainelElementosAnef extends PainelSisAps {
    private static final String UNDERLINE = "_";
    private static final String ID_PAINEL_ANALISE_SUPERVISOR_ITEM = "idPainelAnaliseSupervisorItem";
    private static final String T_DADOS_ELEMENTOS = "tDadosElementos";
    private static final String ID_TITULO_ELEMENTO = "idTituloElemento";
    private List<ElementoAQT> elementosANEFVigente = new ArrayList<ElementoAQT>();
    private final AnaliseQuantitativaAQT aqt;
    private final boolean exibirColunaSupervisor;
    private final boolean exibirColunaInspetor;
    private final boolean exibirColunaVigente;
    private AnaliseQuantitativaAQT aqtVigente = new AnaliseQuantitativaAQT();

    public PainelElementosAnef(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
        exibirColunaVigente = AnaliseQuantitativaAQTMediator.get().exibirColunaVigente(aqt.getEstado());
        exibirColunaSupervisor = AnaliseQuantitativaAQTMediator.get().exibirColunaSupervisor(aqt.getEstado());
        exibirColunaInspetor = AnaliseQuantitativaAQTMediator.get().exibirColunaInspetor(aqt.getEstado());
        aqtVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setListaElementosARCVigente();
        List<ElementoAQT> buscarElementos = new ArrayList<ElementoAQT>();

        if (aqtVigente != null && AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqtVigente.getEstado())
                && AnaliseQuantitativaAQTMediator.get().estadoPrevisto(aqt.getEstado())) {
            buscarElementos.addAll(ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(aqtVigente.getPk()));
        } else {
            buscarElementos.addAll(ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(aqt.getPk()));
        }

        add(new ListView<ElementoAQT>("elementos", buscarElementos) {
            @Override
            protected void populateItem(ListItem<ElementoAQT> item) {
                addComponentesItem(item);
            }
        });
    }

    private void setListaElementosARCVigente() {
        if (aqtVigente == null) {
            elementosANEFVigente = new ArrayList<ElementoAQT>();
        } else {
            elementosANEFVigente = ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(aqtVigente.getPk());
        }
    }

    private void addComponentesItem(ListItem<ElementoAQT> item) {
        WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
        ElementoAQT elemento = item.getModelObject();
        ElementoAQT elementoARCVigente =
                ElementoAQTMediator.get().obterElementoANEFVigente(new LinkedList<ElementoAQT>(elementosANEFVigente),
                        elemento);
        addColunasNotas(item, elemento, elementoARCVigente);
        addPainelItensElementos(painelElemento, elemento, elementoARCVigente);
        painelAnaliseElementos(item, painelElemento, elemento, elementoARCVigente);
    }

    private void painelAnaliseElementos(ListItem<ElementoAQT> item, WebMarkupContainer painelElemento,
            ElementoAQT elemento, ElementoAQT elementoANEFVigente) {
        painelElemento.addOrReplace(new PainelAnaliseElementosAnefsNovo(ID_PAINEL_ANALISE_SUPERVISOR_ITEM, 
                aqt, aqtVigente, elemento, elementoANEFVigente, exibirColunaSupervisor, exibirColunaVigente));

        painelElemento.setOutputMarkupPlaceholderTag(true);
        String pk = String.valueOf(elemento.getPk());
        pk = elemento.getParametroElemento().getDescricao();
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + UNDERLINE + pk);
        item.addOrReplace(painelElemento);
    }

    private void addPainelItensElementos(WebMarkupContainer painelElemento, ElementoAQT elemento,
            ElementoAQT elementoARCVigente) {
        painelElemento.add(new PainelItemDetalheAnef("idPainelItem", ItemElementoAQTMediator.get()
                .buscarItensOrdenadosDoElemento(elemento), elementoARCVigente, aqt, exibirColunaInspetor,
                exibirColunaVigente));
    }

    private void addColunasNotas(ListItem<ElementoAQT> item, ElementoAQT elemento,
            ElementoAQT elementoARCVigente) {
        WebMarkupContainer painelElementoNotas = new WebMarkupContainer("tDadosElementosAnef");
        Label nome = new Label(ID_TITULO_ELEMENTO, new PropertyModel<String>(elemento, "parametroElemento.descricao"));
        nome.setOutputMarkupId(true);
        nome.setMarkupId(nome.getId() + elemento.getPk());
        painelElementoNotas.addOrReplace(nome);
        colunaSupervisor(painelElementoNotas, elemento);
        colunaInspetor(painelElementoNotas, elemento);
        colunaVigente(painelElementoNotas, elementoARCVigente);
        painelElementoNotas.setOutputMarkupPlaceholderTag(true);
        String pk = elemento.getParametroElemento().getDescricao();
        painelElementoNotas.setMarkupId(painelElementoNotas.getId() + UNDERLINE + pk);
        item.addOrReplace(painelElementoNotas);
    }

    private void colunaSupervisor(WebMarkupContainer painelElemento, ElementoAQT elemento) {
        String notaSuper = getNotaSupervisor(elemento);
        Label labelNotaSupervisor = new Label("idNotaSupervisorElemento", notaSuper);
        labelNotaSupervisor.setOutputMarkupId(true);
        labelNotaSupervisor.setMarkupId(labelNotaSupervisor.getId() + elemento.getPk());
        labelNotaSupervisor.setVisible(exibirColunaSupervisor);
        painelElemento.addOrReplace(labelNotaSupervisor);
    }

    private void colunaInspetor(WebMarkupContainer painelElemento, ElementoAQT elemento) {
        Label labelNotaInspetor =
                new Label("idNotaInspetorElemento", new PropertyModel<String>(elemento,
                        "parametroNotaInspetor.descricaoValor"));
        labelNotaInspetor.setOutputMarkupId(true);
        labelNotaInspetor.setMarkupId(labelNotaInspetor.getId() + elemento.getPk());
        labelNotaInspetor.setVisible(exibirColunaInspetor);
        painelElemento.addOrReplace(labelNotaInspetor);
    }

    private void colunaVigente(WebMarkupContainer painelElemento, ElementoAQT elementoARCVigente) {
        LabelLinhas labelNotaVigente =
                new LabelLinhas("idNotaVigenteElemento", elementoARCVigente == null ? ""
                        : elementoARCVigente.getNotaSupervisor()) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                    }
                };
        labelNotaVigente.setVisible(exibirColunaVigente);
        painelElemento.addOrReplace(labelNotaVigente);
    }

    public String getNotaSupervisor(ElementoAQT elemento) {
        if (elemento.possuiNotasupervisor()) {
            return elemento.getParametroNotaSupervisor().getDescricaoValor();
        } else {
            return "";
        }
    }

}