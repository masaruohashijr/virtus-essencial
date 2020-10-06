package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelElementosConsultaAQT extends PainelSisAps {
    private static final String T_INFORMACOES_ELEMENTO_ITENS_ANEF = "tInformacoesElementoItensANEF";
    private static final String T_DADOS_ELEMENTOS_ANEFS = "tDadosElementoANEFs";
    private static final String ID_TITULO_ELEMENTOS_ANEF = "idTituloElemento";
    private AnaliseQuantitativaAQT aqt;

    public PainelElementosConsultaAQT(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        buildListView();
    }

    private void buildListView() {
        ListView<ElementoAQT> listView =
                new ListView<ElementoAQT>("listaElementoAQTs", ElementoAQTMediator.get()
                        .buscarElementosOrdenadosDoAnef(aqt.getPk())) {
                    @Override
                    protected void populateItem(ListItem<ElementoAQT> item) {
                        addComponentesItem(item);
                    }

                };
        addOrReplace(listView);
    }

    private void addComponentesItem(ListItem<ElementoAQT> item) {
        WebMarkupContainer painelElementoAQT = new WebMarkupContainer(T_DADOS_ELEMENTOS_ANEFS);
        WebMarkupContainer painelTabelaElementos = new WebMarkupContainer(T_INFORMACOES_ELEMENTO_ITENS_ANEF);
        ElementoAQT elementoAQT = item.getModelObject();
        painelTabelaElementos.setMarkupId(T_INFORMACOES_ELEMENTO_ITENS_ANEF + elementoAQT.getPk().toString());
        painelElementoAQT.setMarkupId(T_DADOS_ELEMENTOS_ANEFS + elementoAQT.getPk().toString());
        Label nome =
                new Label(ID_TITULO_ELEMENTOS_ANEF, new PropertyModel<String>(elementoAQT,
                        "parametroElemento.descricao"));
        nome.setOutputMarkupId(true);
        nome.setMarkupId(nome.getId() + elementoAQT.getPk());
        painelTabelaElementos.addOrReplace(nome);

        PropertyModel<String> modelNotaElementoAQT = new PropertyModel<String>(elementoAQT, "notaSupervisor");

        painelTabelaElementos.add(new Label("idNovaNotaElementoAQT", modelNotaElementoAQT));
        painelElementoAQT.addOrReplace(painelTabelaElementos);
        notaElementoAQTVigente(item, painelElementoAQT, elementoAQT);
        montarJustificativaAnalise(item, elementoAQT);

    }

    private void notaElementoAQTVigente(ListItem<ElementoAQT> item, WebMarkupContainer painelElementoAQT,
            ElementoAQT elementoAQT) {

        painelElementoAQT.addOrReplace(new PainelItemDetalheConsultaAnef("idPainelItem", ItemElementoAQTMediator.get()
                .buscarItensOrdenadosDoElemento(elementoAQT)));

        item.addOrReplace(painelElementoAQT);
    }

    private void montarJustificativaAnalise(ListItem<ElementoAQT> item, ElementoAQT elementoAQT) {

        WebMarkupContainer wmcExibirAnaliseSupervisor = new WebMarkupContainer("wmcExibirAnaliseSupervisor");
        wmcExibirAnaliseSupervisor.setMarkupId("tabelaAnaliseDados"
                + (elementoAQT == null || elementoAQT.getPk() == null ? "" : elementoAQT.getPk()));

        String titulo = "";
        if (elementoAQT != null && elementoAQT.getParametroElemento() != null) {
            titulo =
                    "Análise do supervisor para o elemento \"" + elementoAQT.getParametroElemento().getDescricao()
                            + "\"";
        }

        Label nome = new Label("idTituloElementoSupervisor", titulo);
        nome.setOutputMarkupId(true);
        wmcExibirAnaliseSupervisor.addOrReplace(nome);

        String justificativaSupervisorVigente =
                elementoAQT == null || elementoAQT.getJustificativaSupervisor() == null ? "" : elementoAQT
                        .getJustificativaSupervisor();

        wmcExibirAnaliseSupervisor.add(new Label("idAvaliacaoVigente", justificativaSupervisorVigente)
                .setEscapeModelStrings(false));
        wmcExibirAnaliseSupervisor.setVisibilityAllowed(!"".equals(justificativaSupervisorVigente));

        item.addOrReplace(wmcExibirAnaliseSupervisor);

    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public void setAqt(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

}
