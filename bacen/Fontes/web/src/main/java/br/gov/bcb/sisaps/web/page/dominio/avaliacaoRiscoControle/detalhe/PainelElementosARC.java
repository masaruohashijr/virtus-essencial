package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;

public class PainelElementosARC extends PainelSisAps {

    private static final String T_DADOS_ELEMENTOS = "tDadosElementos";
    private AvaliacaoRiscoControle arc;
    private final Ciclo ciclo;
    private AvaliacaoRiscoControle arcVigente;
    private List<Elemento> elementosVigente = new ArrayList<Elemento>();
    private boolean exibirColunaSupervisor;
    private boolean exibirColunaInspetor;
    private boolean exibirColunaVigente;

    public PainelElementosARC(String id, AvaliacaoRiscoControle arc, Ciclo ciclo) {
        super(id);
        this.arc = arc;
        this.ciclo = ciclo;
    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.arcVigente = arc.getAvaliacaoRiscoControleVigente();
        this.exibirColunaSupervisor = AvaliacaoRiscoControleMediator.get().exibirColunaSupervisor(arc.getEstado());
        this.exibirColunaInspetor = AvaliacaoRiscoControleMediator.get().exibirColunaInspetor(arc.getEstado());
        this.exibirColunaVigente = AvaliacaoRiscoControleMediator.get().exibirColunaVigente(arc.getEstado());
        addComponentes();
    }

    private void addComponentes() {
        if (arcVigente != null) {
            elementosVigente = ElementoMediator.get().buscarElementosOrdenadosDoArc(arcVigente.getPk());
        }
        
        List<Elemento> buscarElementos = new ArrayList<Elemento>();
        if (arcVigente != null && AvaliacaoRiscoControleMediator.get().estadoConcluido(arcVigente.getEstado())
                && AvaliacaoRiscoControleMediator.get().estadoPrevisto(arc.getEstado())) {
            buscarElementos.addAll(ElementoMediator.get().buscarElementosOrdenadosDoArc(arcVigente.getPk()));
        } else {
            buscarElementos.addAll(ElementoMediator.get().buscarElementosOrdenadosDoArc(arc.getPk()));
        }
        
        addOrReplace(new ListView<Elemento>("elementos", buscarElementos) {
            @Override
            protected void populateItem(ListItem<Elemento> item) {
                addComponentesItem(item);
            }
        });
    }
    
    private void addComponentesItem(ListItem<Elemento> item) {
        WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
        Elemento elemento = item.getModelObject();
        Elemento elementoARCVigente =
                ElementoMediator.get().obterElementoCorrespondenteARCVigente(
                        new LinkedList<Elemento>(elementosVigente), elemento);
        addColunasNotas(item, elemento, elementoARCVigente);
        addPainelItensElementos(painelElemento, elemento, elementoARCVigente);
        painelAnaliseElementos(item, painelElemento, elemento, elementoARCVigente);
    }

    private void addColunasNotas(ListItem<Elemento> item, Elemento elemento, Elemento elementoARCVigente) {
        WebMarkupContainer painelElementoNotas = new WebMarkupContainer("tDadosElementosArc");
        Label nome = new Label("idTituloElemento", new PropertyModel<String>(elemento, "parametroElemento.nome"));
        nome.setOutputMarkupId(true);
        nome.setMarkupId(nome.getId() + elemento.getPk());
        painelElementoNotas.addOrReplace(nome);
        colunaSupervisor(painelElementoNotas, elemento);
        colunaInspetor(painelElementoNotas, elemento);
        colunaVigente(painelElementoNotas, elementoARCVigente);
        painelElementoNotas.setOutputMarkupPlaceholderTag(true);
        painelElementoNotas.setMarkupId(painelElementoNotas.getId() + Constantes.SUBLINHADO + elemento.getPk());
        item.addOrReplace(painelElementoNotas);
    }

    private void colunaSupervisor(WebMarkupContainer painelElemento, Elemento elemento) {
        String notaSuper = getNotaSupervisor(elemento);
        Label labelNotaSupervisor = new Label("idNotaSupervisorElemento", notaSuper);
        labelNotaSupervisor.setOutputMarkupId(true);
        labelNotaSupervisor.setMarkupId(labelNotaSupervisor.getId() + elemento.getPk());
        labelNotaSupervisor.setVisible(exibirColunaSupervisor);
        painelElemento.addOrReplace(labelNotaSupervisor);
    }

    private void colunaInspetor(WebMarkupContainer painelElemento, Elemento elemento) {
        Label labelNotaInspetor =
                new Label("idNotaInspetorElemento", new PropertyModel<String>(elemento,
                        "parametroNotaInspetor.descricaoValor"));
        labelNotaInspetor.setOutputMarkupId(true);
        labelNotaInspetor.setMarkupId(labelNotaInspetor.getId() + elemento.getPk());
        labelNotaInspetor.setVisible(exibirColunaInspetor);
        painelElemento.addOrReplace(labelNotaInspetor);
    }

    private void colunaVigente(WebMarkupContainer painelElemento, Elemento elementoARCVigente) {
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

    private void addPainelItensElementos(WebMarkupContainer painelElemento, Elemento elemento,
            Elemento elementoARCVigente) {
        painelElemento.add(new PainelItemDetalheARC("idPainelItem", ciclo, ItemElementoMediator.get()
                .buscarItensOrdenadosDoElemento(elemento), elementoARCVigente, arc, exibirColunaInspetor,
                exibirColunaVigente));
    }

    private void painelAnaliseElementos(ListItem<Elemento> item, WebMarkupContainer painelElemento, Elemento elemento,
            Elemento elementoARCVigente) {
        painelElemento.addOrReplace(new PainelAnaliseElementosARC("idPainelAnaliseSupervisorItem", 
                arc, elemento, elementoARCVigente, exibirColunaSupervisor, exibirColunaVigente));

        painelElemento.setOutputMarkupPlaceholderTag(true);
        String pk = String.valueOf(elemento.getPk());
        pk = elemento.getParametroElemento().getDescricao();
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + Constantes.SUBLINHADO + pk);
        item.addOrReplace(painelElemento);
    }

    private String getNotaSupervisor(Elemento elemento) {
        if (elemento.possuiNotasupervisor()) {
            return elemento.getParametroNotaSupervisor().getDescricaoValor();
        } else {
            return "";
        }
    }
    
    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.arc = avaliacao;
    }
}
