package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.PainelDocumentoDetalheAnefNovo;

public class PainelItemDetalheAnef extends Panel {
    private static final String ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO = "idPainelDocumentoUploadAnexo";
    private final List<ItemElementoAQT> itensElementoARCVigente = new ArrayList<ItemElementoAQT>();
    private final AnaliseQuantitativaAQT aqt;
    private final boolean exibirColunaInspetor;
    private final boolean exibirColunaVigente;
    private ItemElementoAQT itemElementoAQTVigente;

    public PainelItemDetalheAnef(String id, final List<ItemElementoAQT> itensElemento,
            final ElementoAQT elementoARCVigente, final AnaliseQuantitativaAQT aqt, boolean exibirColunaInspetor,
            boolean exibirColunaVigente) {
        super(id);
        this.aqt = aqt;
        this.exibirColunaInspetor = exibirColunaInspetor;
        this.exibirColunaVigente = exibirColunaVigente;

        if (elementoARCVigente != null) {
            itensElementoARCVigente.addAll(elementoARCVigente.getItensElemento());
        }
        ListView<ItemElementoAQT> listView = new ListView<ItemElementoAQT>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElementoAQT> item) {
                WebMarkupContainer tabelaDadosElemento = new WebMarkupContainer("tabelaDadosItem");
                final ItemElementoAQT itemElementoAQT = item.getModelObject();
                itemElementoAQTVigente =
                        ItemElementoAQTMediator.get().obterItemElementoCorrespondenteANEFVigente(
                                itensElementoARCVigente, itemElementoAQT);
                addDadosTabela(item, tabelaDadosElemento, itemElementoAQT);
            }
        };
        add(listView);
    }

    private void addDadosTabela(ListItem<ItemElementoAQT> item,
            WebMarkupContainer tabelaDadosElemento, final ItemElementoAQT itemElementoAQT) {
        addTituloItem(tabelaDadosElemento, itemElementoAQT);
        addPainelDocumento(item, tabelaDadosElemento, itemElementoAQT);
        item.add(tabelaDadosElemento);
    }

    private void addPainelDocumento(ListItem<ItemElementoAQT> item, WebMarkupContainer tabelaDadosElemento,
            final ItemElementoAQT itemElementoAQT) {
        tabelaDadosElemento.addOrReplace(new PainelDocumentoDetalheAnefNovo(
                ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO, itemElementoAQT, 
                itemElementoAQTVigente, aqt, exibirColunaInspetor, exibirColunaVigente));
        tabelaDadosElemento.setMarkupId(tabelaDadosElemento.getId() + item.getModelObject().getPk());
    }

    private void addTituloItem(WebMarkupContainer tabelaDadosElemento, final ItemElementoAQT itemElementoAQT) {
        tabelaDadosElemento.add(new Label("idTituloItem", new PropertyModel<String>(itemElementoAQT,
                "parametroItemElemento.descricao")));
    }

}
