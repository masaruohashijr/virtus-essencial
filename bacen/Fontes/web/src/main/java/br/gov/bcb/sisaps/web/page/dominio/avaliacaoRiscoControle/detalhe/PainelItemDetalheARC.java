package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;

public class PainelItemDetalheARC extends Panel {

    private final Ciclo ciclo;
    private final List<ItemElemento> itensElemento;
    private final Elemento elementoARCVigente;
    private final AvaliacaoRiscoControle arc;
    private final boolean exibirColunaInspetor;
    private final boolean exibirColunaVigente;

    public PainelItemDetalheARC(String id, Ciclo ciclo, List<ItemElemento> itensElemento, Elemento elementoARCVigente, 
            AvaliacaoRiscoControle arc, boolean exibirColunaInspetor, boolean exibirColunaVigente) {
        super(id);
        this.ciclo = ciclo;
        this.itensElemento = itensElemento;
        this.elementoARCVigente = elementoARCVigente;
        this.arc = arc;
        this.exibirColunaInspetor = exibirColunaInspetor;
        this.exibirColunaVigente = exibirColunaVigente;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }

    private void addComponentes() {
        final List<ItemElemento> itensElementoARCVigente = new ArrayList<ItemElemento>();
        if (elementoARCVigente != null) {
            itensElementoARCVigente.addAll(elementoARCVigente.getItensElemento());
        }
        
        ListView<ItemElemento> listView = new ListView<ItemElemento>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElemento> item) {
                WebMarkupContainer tabelaDadosElemento = new WebMarkupContainer("tabelaDadosItem");
                final ItemElemento itemElementoARC = item.getModelObject();
                ItemElemento itemElementoARCVigente = 
                        ItemElementoMediator.get().obterItemElementoCorrespondenteARCVigente(
                        itensElementoARCVigente, itemElementoARC);
                addDadosTabela(item, tabelaDadosElemento, itemElementoARC, itemElementoARCVigente);
            }
        };
        addOrReplace(listView);
    }
    
    private void addDadosTabela(ListItem<ItemElemento> item, WebMarkupContainer tabelaDadosElemento,
            ItemElemento itemElementoARC, ItemElemento itemElementoARCVigente) {
        addTituloItem(tabelaDadosElemento, itemElementoARC);
        addPainelDocumento(item, tabelaDadosElemento, itemElementoARC, itemElementoARCVigente);
        item.add(tabelaDadosElemento);
    }

    private void addTituloItem(WebMarkupContainer tabelaDadosElemento, ItemElemento itemElementoARC) {
        tabelaDadosElemento.add(new Label("idTituloItem", new PropertyModel<String>(itemElementoARC,
                "parametroItemElemento.nome")));
    }

    private void addPainelDocumento(ListItem<ItemElemento> item, WebMarkupContainer tabelaDadosElemento,
            ItemElemento itemElementoARC, ItemElemento itemElementoARCVigente) {
        tabelaDadosElemento.addOrReplace(new PainelDocumentoDetalheARC(
                "idPainelDocumentoUploadAnexo", ciclo, itemElementoARC, 
                itemElementoARCVigente, arc, exibirColunaInspetor, exibirColunaVigente));
        tabelaDadosElemento.setMarkupId(tabelaDadosElemento.getId() + item.getModelObject().getPk());
    }

}
