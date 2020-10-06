package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoDocumento;

public class PainelItemDetalheConsultaARC extends Panel {
    private static final String TABELA_DADOS_ITEM = "tabelaDadosItem";
    private List<ItemElemento> itensElemento = new ArrayList<ItemElemento>();
    private final Ciclo ciclo;

    public PainelItemDetalheConsultaARC(String id, Ciclo ciclo, List<ItemElemento> itensElemento) {
        super(id);
        this.ciclo = ciclo;
        this.itensElemento = itensElemento;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        ListView<ItemElemento> listView = new ListView<ItemElemento>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElemento> item) {
                WebMarkupContainer tabelaDadosElemento = new WebMarkupContainer(TABELA_DADOS_ITEM);
                final ItemElemento itemElemento = item.getModelObject();
                tabelaDadosElemento.setMarkupId(TABELA_DADOS_ITEM + itemElemento.getPk().toString());
                addDadosTabela(item, tabelaDadosElemento, itemElemento);
            }

        };
        add(listView);
    }

    private void addDadosTabela(ListItem<ItemElemento> item, WebMarkupContainer tabelaDadosElemento,
            final ItemElemento itemElemento) {

        tabelaDadosElemento.add(new Label("idTituloItem", new PropertyModel<String>(itemElemento,
                "parametroItemElemento.nome")));

        tabelaDadosElemento.add(new Label("idJustificativa", new PropertyModel<String>(itemElemento,
                "documento.justificativa")).setVisibilityAllowed(itemElemento.getDocumento() != null
                && !"".equals(itemElemento.getDocumento().getJustificativa())).setEscapeModelStrings(false));

        TabelaAnexoDocumento tabelaAnexoDocumento =
                new TabelaAnexoDocumento("idTabelaAnexoDocumento", itemElemento, 
                        ciclo, false);
        tabelaAnexoDocumento.setMarkupId(tabelaAnexoDocumento.getMarkupId() + item.getModelObject().getPk());
        tabelaAnexoDocumento.setOutputMarkupId(true);
        tabelaAnexoDocumento.setVisibilityAllowed(itemElemento.getDocumento() != null
                && !itemElemento.getDocumento().getAnexosItemElemento().isEmpty());
        tabelaDadosElemento.addOrReplace(tabelaAnexoDocumento);

        item.add(tabelaDadosElemento);
    }

}
