package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;

public class PainelItemDetalheConsultaAnef extends Panel {
    private static final String TABELA_DADOS_ITEM = "tabelaDadosItem";
    private List<ItemElementoAQT> itensElemento = new ArrayList<ItemElementoAQT>();

    public PainelItemDetalheConsultaAnef(String id, List<ItemElementoAQT> itensElemento) {
        super(id);
        this.itensElemento = itensElemento;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        ListView<ItemElementoAQT> listView = new ListView<ItemElementoAQT>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElementoAQT> item) {
                WebMarkupContainer tabelaDadosElemento = new WebMarkupContainer(TABELA_DADOS_ITEM);
                final ItemElementoAQT itemElemento = item.getModelObject();
                tabelaDadosElemento.setMarkupId(TABELA_DADOS_ITEM + itemElemento.getPk().toString());
                addDadosTabela(item, tabelaDadosElemento, itemElemento);
            }

        };
        add(listView);
    }

    private void addDadosTabela(ListItem<ItemElementoAQT> item, WebMarkupContainer tabelaDadosElemento,
            final ItemElementoAQT itemElemento) {

        tabelaDadosElemento.add(new Label("idTituloItem", new PropertyModel<String>(itemElemento,
                "parametroItemElemento.descricao")));

        tabelaDadosElemento.add(new Label("idJustificativa", new PropertyModel<String>(itemElemento,
                "documento.justificativa")).setEscapeModelStrings(false)
                .setVisibilityAllowed(itemElemento.getDocumento() != null
                && !"".equals(itemElemento.getDocumento().getJustificativa())));

        TabelaAnexoDocumentoAQT tabelaAnexoDocumentoAQT =
                new TabelaAnexoDocumentoAQT("idTabelaAnexoDocumento", itemElemento, 
                        itemElemento.getElemento().getAnaliseQuantitativaAQT().getCiclo(), false);
        tabelaAnexoDocumentoAQT.setMarkupId(tabelaAnexoDocumentoAQT.getMarkupId() + item.getModelObject().getPk());
        tabelaAnexoDocumentoAQT.setOutputMarkupId(true);
        tabelaAnexoDocumentoAQT.setVisibilityAllowed(itemElemento.getDocumento() != null
                && !itemElemento.getDocumento().getAnexosItemElemento().isEmpty());
        tabelaDadosElemento.addOrReplace(tabelaAnexoDocumentoAQT);

        item.add(tabelaDadosElemento);
    }

}
