package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class PainelItemEdicaoAnef extends Panel {

    private static final String DOCUMENTO = "documento_";

    private static final String ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO = "idPainelDocumentoUploadAnexo";

    @SpringBean
    private ItemElementoAQTMediator itemElementoMediator;

    private final List<ItemElementoAQT> itensElementoARCVigente = new ArrayList<ItemElementoAQT>();

    private List<PainelDocumentoEdicaoAnef> listPainel = new ArrayList<PainelDocumentoEdicaoAnef>();

    private List<PainelItemEdicaoSemGrupoExpansivel> listPainelSemGrupo =
            new ArrayList<PainelItemEdicaoSemGrupoExpansivel>();

    public PainelItemEdicaoAnef(String id, List<ItemElementoAQT> itensElemento, ElementoAQT elementoARCVigente,
            final AnaliseQuantitativaAQT aqt, final List<String> idsAlertas, final AnaliseQuantitativaAQT anefVigente) {
        super(id);
        if (elementoARCVigente != null) {
            itensElementoARCVigente.addAll(elementoARCVigente.getItensElemento());
        }
        ListView<ItemElementoAQT> listView = new ListView<ItemElementoAQT>("listaItens", itensElemento) {

            @Override
            protected void populateItem(ListItem<ItemElementoAQT> item) {
                addPainel(aqt, idsAlertas, anefVigente, item);
            }
        };
        addOrReplace(listView);
    }
    
    private void addPainel(final AnaliseQuantitativaAQT aqt, final List<String> idsAlertas,
            final AnaliseQuantitativaAQT anefVigente, ListItem<ItemElementoAQT> item) {
        ItemElementoAQT itemElemento = item.getModelObject();
        item.setMarkupId(SisapsUtil.criarMarkupId(itemElemento.getParametroItemElemento().getNome()));
        WebMarkupContainer tDadosItem = new WebMarkupContainer("tDadosItem");
        tDadosItem.setMarkupId(tDadosItem.getId() + itemElemento.getPk());
        item.addOrReplace(tDadosItem);
        tDadosItem.addOrReplace(new Label("idTituloItem", new PropertyModel<String>(itemElemento,
                "parametroItemElemento.descricao")));
        ItemElementoAQT itemElementoARCVigente =
                itemElementoMediator.obterItemElementoCorrespondenteANEFVigente(itensElementoARCVigente,
                        itemElemento);
        
        if (anefVigente != null
                && AnaliseQuantitativaAQTMediator.get().estadoConcluido(anefVigente.getEstado())
                && itemElementoARCVigente.getDocumento() == null) {
            PainelItemEdicaoSemGrupoExpansivel painelSemGrupo =
                    new PainelItemEdicaoSemGrupoExpansivel(ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO, itemElemento,
                            itemElementoARCVigente, aqt, idsAlertas, anefVigente);
            painelSemGrupo.setMarkupId(DOCUMENTO
                    + SisapsUtil.criarMarkupId(itemElemento.getParametroItemElemento().getNome()));
            painelSemGrupo.setOutputMarkupId(true);
            tDadosItem.addOrReplace(painelSemGrupo);
            listPainelSemGrupo.add(painelSemGrupo);
        } else {
            PainelDocumentoEdicaoAnef painel =
                    new PainelDocumentoEdicaoAnef(ID_PAINEL_DOCUMENTO_UPLOAD_ANEXO, itemElemento,
                            itemElementoARCVigente, aqt, idsAlertas);
            painel.setMarkupId(DOCUMENTO
                    + SisapsUtil.criarMarkupId(itemElemento.getParametroItemElemento().getNome()));
            painel.setOutputMarkupId(true);
            tDadosItem.addOrReplace(painel);
            listPainel.add(painel);
        }
    }

    public void validarAnexos() throws NegocioException {
        for (PainelDocumentoEdicaoAnef painel : listPainel) {
            painel.validarAnexos();
        }
        for (PainelItemEdicaoSemGrupoExpansivel painelSemGrupo : listPainelSemGrupo) {
            painelSemGrupo.validarAnexos();
        }
    }

}
