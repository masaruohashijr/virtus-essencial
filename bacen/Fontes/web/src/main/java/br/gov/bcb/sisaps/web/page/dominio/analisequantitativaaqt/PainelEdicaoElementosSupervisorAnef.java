package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
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

public class PainelEdicaoElementosSupervisorAnef extends PainelSisAps {
    private static final String T_DADOS_ELEMENTOS = "tDadosElementos";
    private static final String ID_TITULO_ELEMENTOS = "idTituloElemento";
    private final List<ElementoAQT> elementoAQTsARCVigente = new ArrayList<ElementoAQT>();
    private AnaliseQuantitativaAQT aqt;
    private AnaliseQuantitativaAQT anefVigente = new AnaliseQuantitativaAQT();
    private final List<String> idsAlertas = new ArrayList<String>();
    private PainelEdicaoAnaliseElementosSupervisorAnef painelAnalise;

    public PainelEdicaoElementosSupervisorAnef(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setOutputMarkupId(true);
        aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        anefVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        if (anefVigente != null) {
            this.elementoAQTsARCVigente.addAll(ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(
                    anefVigente.getPk()));
        }
        this.addOrReplace(new ListView<ElementoAQT>("elementos", ElementoAQTMediator.get()
                .buscarElementosOrdenadosDoAnef(aqt.getPk())) {
            @Override
            protected void populateItem(ListItem<ElementoAQT> item) {
                addComponentesItem(item);

            }
        });
    }

    private void addComponentesItem(ListItem<ElementoAQT> item) {
        WebMarkupContainer painelElementoAQT = new WebMarkupContainer(T_DADOS_ELEMENTOS);
        ElementoAQT elementoAQT = item.getModelObject();
        Label nome =
                new Label(ID_TITULO_ELEMENTOS, new PropertyModel<String>(elementoAQT, "parametroElemento.descricao"));
        nome.setMarkupId(ID_TITULO_ELEMENTOS + elementoAQT.getPk());
        nome.setOutputMarkupId(true);
        painelElementoAQT.addOrReplace(nome);
        ElementoAQT elementoAQTVigente =
                ElementoAQTMediator.get().obterElementoANEFVigente(elementoAQTsARCVigente, elementoAQT);

        painelElementoAQT.addOrReplace(new PainelNotaSupervisorAnef("idPainelNotaSupervisor", elementoAQT,
                elementoAQTVigente, idsAlertas));

        painelElementoAQT.add(new PainelItemDetalheAnef("idPainelItem", ItemElementoAQTMediator.get()
                .buscarItensOrdenadosDoElemento(elementoAQT), elementoAQTVigente, aqt, true, true));

        painelAnalise =
                new PainelEdicaoAnaliseElementosSupervisorAnef("idPainelAnaliseSupervisorItem", aqt, elementoAQT,
                        elementoAQTVigente, idsAlertas);
        painelElementoAQT.add(painelAnalise);

        painelElementoAQT.setOutputMarkupPlaceholderTag(true);

        String pk = String.valueOf(elementoAQT.getPk());
        if (elementoAQT.getPk() == null) {
            pk = elementoAQT.getParametroElemento().getDescricao();
        }
        painelElementoAQT.setMarkupId(T_DADOS_ELEMENTOS + pk);
        item.addOrReplace(painelElementoAQT);

    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    public AnaliseQuantitativaAQT getAnefVigente() {
        return anefVigente;
    }

    public PainelEdicaoAnaliseElementosSupervisorAnef getPainelAnalise() {
        return painelAnalise;
    }

    public void setPainelAnalise(PainelEdicaoAnaliseElementosSupervisorAnef painelAnalise) {
        this.painelAnalise = painelAnalise;
    }

}