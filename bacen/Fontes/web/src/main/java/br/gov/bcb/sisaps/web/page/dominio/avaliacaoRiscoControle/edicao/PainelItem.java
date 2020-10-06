package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class PainelItem extends Panel {

    @SpringBean
    private ItemElementoMediator itemElementoMediator;
    
    private List<ItemElemento> itensElementoARCVigente = new ArrayList<ItemElemento>();
    
    public PainelItem(String id, List<ItemElemento> itensElemento, final Elemento elementoARCVigente, final Ciclo ciclo, 
            final AvaliacaoRiscoControle arc, final List<String> idsAlertas) {
        super(id);
        if (elementoARCVigente != null) {
            itensElementoARCVigente.addAll(elementoARCVigente.getItensElemento());
        }
        ListView<ItemElemento> listView = new ListView<ItemElemento>("listaItens", itensElemento) {
            @Override
            protected void populateItem(ListItem<ItemElemento> item) {
                ItemElemento itemElemento = item.getModelObject();
                item.setMarkupId(SisapsUtil.criarMarkupId(itemElemento.getParametroItemElemento().getNome()));
                WebMarkupContainer tDadosItem = new WebMarkupContainer("tDadosItem");
                tDadosItem.setMarkupId(tDadosItem.getId() + itemElemento.getPk());
                item.addOrReplace(tDadosItem);
                tDadosItem.addOrReplace(new Label("idTituloItem",
                        new PropertyModel<String>(itemElemento, "parametroItemElemento.nome")));
                ItemElemento itemElementoARCVigente = itemElementoMediator.obterItemElementoCorrespondenteARCVigente(
                        itensElementoARCVigente, itemElemento);
                PainelDocumento painel = new PainelDocumento(
                        "idPainelDocumentoUploadAnexo", itemElemento, itemElementoARCVigente, ciclo, arc, idsAlertas);
                painel.setMarkupId("documento_" 
                        + SisapsUtil.criarMarkupId(itemElemento.getParametroItemElemento().getNome()));
                painel.setOutputMarkupId(true);
                tDadosItem.addOrReplace(painel);
            }
        };
        addOrReplace(listView);
    }

}
