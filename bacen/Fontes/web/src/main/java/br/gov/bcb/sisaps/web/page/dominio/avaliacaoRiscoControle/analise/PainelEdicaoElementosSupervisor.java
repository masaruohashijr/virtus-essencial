package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelItemDetalheARC;

public class PainelEdicaoElementosSupervisor extends PainelSisAps {
    private static final String T_DADOS_ELEMENTOS = "tDadosElementos";
    private static final String ID_TITULO_ELEMENTO = "idTituloElemento";
    private final AvaliacaoRiscoControle avaliacao;
    private final List<Elemento> elementosARCVigente = new ArrayList<Elemento>();
    private final Ciclo ciclo;
    private List<String> idsAlertas = new ArrayList<String>();

    public PainelEdicaoElementosSupervisor(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo) {
        super(id);
        if (avaliacao.getAvaliacaoRiscoControleVigente() != null) {
            elementosARCVigente.addAll(ElementoMediator.get().buscarElementosOrdenadosDoArc(
                    avaliacao.getAvaliacaoRiscoControleVigente().getPk()));
        }
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
        this.add(new ListView<Elemento>("elementos", ElementoMediator.get().buscarElementosOrdenadosDoArc(
                avaliacao.getPk())) {
            @Override
            protected void populateItem(ListItem<Elemento> item) {
                addComponentesItem(item);

            }
        });
    }

    private void addComponentesItem(ListItem<Elemento> item) {
        WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
        Elemento elemento = item.getModelObject();
        Label nome = new Label(ID_TITULO_ELEMENTO, new PropertyModel<String>(elemento, "parametroElemento.nome"));
        nome.setMarkupId(ID_TITULO_ELEMENTO + elemento.getPk());
        nome.setOutputMarkupId(true);
        painelElemento.addOrReplace(nome);
        Elemento elementoARCVigente =
                ElementoMediator.get().obterElementoCorrespondenteARCVigente(elementosARCVigente, elemento);

        painelElemento.addOrReplace(new PainelNotaSupervisor("idPainelNotaSupervisor", elemento, elementoARCVigente,
                ciclo, avaliacao, idsAlertas));

        painelElemento.add(new PainelItemDetalheARC("idPainelItem", ciclo, ItemElementoMediator.get()
                .buscarItensOrdenadosDoElemento(elemento), elementoARCVigente, avaliacao, true, true));
        painelElemento.add(new PainelEdicaoAnaliseElementosSupervisor("idPainelAnaliseSupervisorItem", avaliacao,
                ciclo, elemento, elementoARCVigente, idsAlertas));
        painelElemento.setOutputMarkupPlaceholderTag(true);

        String pk = String.valueOf(elemento.getPk());
        if (elemento.getPk() == null) {
            pk = elemento.getParametroElemento().getNome();
        }
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + pk);
        item.addOrReplace(painelElemento);

    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    public void setIdsAlertas(List<String> idsAlertas) {
        this.idsAlertas = idsAlertas;
    }

}