package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

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

public class PainelElementosConsulta extends PainelSisAps {

    private static final String T_INFORMACOES_ELEMENTO_ITENS = "tInformacoesElementoItens";
    private static final String T_DADOS_ELEMENTOS = "tDadosElementos";
    private static final String ID_TITULO_ELEMENTOS = "idTituloElemento";
    private AvaliacaoRiscoControle arc;
    private final Ciclo ciclo;

    public PainelElementosConsulta(String id, AvaliacaoRiscoControle arc, Ciclo ciclo) {
        super(id);
        this.arc = arc;
        this.ciclo = ciclo;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        buildListView();
    }

    private void buildListView() {
        ListView<Elemento> listView =
                new ListView<Elemento>("listaElementos", ElementoMediator.get().buscarElementosOrdenadosDoArc(
                        arc.getPk())) {
                    @Override
                    protected void populateItem(ListItem<Elemento> item) {
                        addComponentesItem(item);
                    }

                };
        addOrReplace(listView);
    }

    private void addComponentesItem(ListItem<Elemento> item) {
        WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
        WebMarkupContainer painelTabelaElementos = new WebMarkupContainer(T_INFORMACOES_ELEMENTO_ITENS);
        Elemento elemento = item.getModelObject();
        painelTabelaElementos.setMarkupId(T_INFORMACOES_ELEMENTO_ITENS + elemento.getPk().toString());
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + elemento.getPk().toString());
        Label nome = new Label(ID_TITULO_ELEMENTOS, new PropertyModel<String>(elemento, "parametroElemento.nome"));
        nome.setOutputMarkupId(true);
        nome.setMarkupId(nome.getId() + elemento.getPk());
        painelTabelaElementos.addOrReplace(nome);

        PropertyModel<String> modelNotaElemento = new PropertyModel<String>(elemento, "notaSupervisor");

        painelTabelaElementos.add(new Label("idNovaNotaElemento", modelNotaElemento));
        painelElemento.addOrReplace(painelTabelaElementos);
        notaElementoVigente(item, painelElemento, elemento);
        montarJustificativaAnalise(item, elemento);

    }

    private void notaElementoVigente(ListItem<Elemento> item, WebMarkupContainer painelElemento, Elemento elemento) {

        painelElemento.addOrReplace(new PainelItemDetalheConsultaARC("idPainelItem", ciclo, ItemElementoMediator.get()
                .buscarItensOrdenadosDoElemento(elemento)));

        item.addOrReplace(painelElemento);
    }

    private void montarJustificativaAnalise(ListItem<Elemento> item, Elemento elemento) {

        WebMarkupContainer wmcExibirAnaliseSupervisor = new WebMarkupContainer("wmcExibirAnaliseSupervisor");
        wmcExibirAnaliseSupervisor.setMarkupId("tabelaAnaliseDados"
                + (elemento == null || elemento.getPk() == null ? "" : elemento.getPk()));

        String titulo = "";
        if (elemento != null && elemento.getParametroElemento() != null) {
            titulo = "Análise do supervisor para o elemento \"" + elemento.getParametroElemento().getNome() + "\"";
        }

        Label nome = new Label("idTituloElementoSupervisor", titulo);
        nome.setOutputMarkupId(true);
        wmcExibirAnaliseSupervisor.addOrReplace(nome);

        wmcExibirAnaliseSupervisor.add(new Label("idAvaliacaoVigente", elemento == null
                || elemento.getJustificativaSupervisor() == null ? "" : elemento.getJustificativaSupervisor())
                .setEscapeModelStrings(false));
        wmcExibirAnaliseSupervisor.setVisibilityAllowed(!"".equals(elemento.getJustificativaAtualizada()));

        item.addOrReplace(wmcExibirAnaliseSupervisor);

    }

    public AvaliacaoRiscoControle getArc() {
        return arc;
    }

    public void setArc(AvaliacaoRiscoControle arc) {
        this.arc = arc;
    }

}
