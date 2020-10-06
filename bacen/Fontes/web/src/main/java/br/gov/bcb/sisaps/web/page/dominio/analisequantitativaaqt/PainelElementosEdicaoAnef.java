package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelElementosEdicaoAnef extends Panel {

    private static final String ID_TITULO_ELEMENTO = "idTituloElemento";

    private static final String ID_TITULO_ANALISE_VIGENTE = "idTituloAnaliseVigente";

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO = "idAlertaDadosNaoSalvosElemento";

    private static final String ID_SELECT_NOVA_NOTA_ELEMENTO = "idSelectNovaNotaElemento";

    private static final String ID_BTT_SALVAR_ELEMENTO = "bttSalvarElemento";

    private final List<ElementoAQT> elementosARCVigente = new ArrayList<ElementoAQT>();

    @SpringBean
    private ElementoAQTMediator elementoMediator;

    private final List<String> idsAlertas = new ArrayList<String>();

    private AnaliseQuantitativaAQT aqt;

    private final List<ElementoAQT> elementos;
    private AnaliseQuantitativaAQT anefVigente;
    private Ciclo ciclo;

    private List<PainelItemEdicaoAnef> listaPainelItemEdicaoAnef = new ArrayList<PainelItemEdicaoAnef>();

    public PainelElementosEdicaoAnef(String id, AnaliseQuantitativaAQT aqt, List<ElementoAQT> elementos) {
        super(id);
        this.elementos = elementos;
        this.aqt = aqt;
        buildListView(elementos, aqt);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        this.ciclo = CicloMediator.get().buscarCicloPorPK(aqt.getCiclo().getPk());
        this.anefVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        if (anefVigente != null) {
            this.elementosARCVigente.addAll(ElementoAQTMediator.get().buscarElementosOrdenadosDoAnef(
                    anefVigente.getPk()));
        }
    }

    private void buildListView(List<ElementoAQT> elementos, final AnaliseQuantitativaAQT aqt) {
        ListView<ElementoAQT> listView = new ListView<ElementoAQT>("listaElementos", elementos) {
            @Override
            protected void populateItem(ListItem<ElementoAQT> item) {
                addComponentesItem(item, aqt);
            }

        };
        add(listView);
    }

    private void addComponentesItem(ListItem<ElementoAQT> elemento, AnaliseQuantitativaAQT aqt) {
        ElementoAQT objElemento = elemento.getModelObject();
        WebMarkupContainer tDadosElemento = new WebMarkupContainer("tDadosElemento");
        tDadosElemento.setMarkupId(tDadosElemento.getId() + objElemento.getPk());
        elemento.setMarkupId(SisapsUtil.criarMarkupId(objElemento.getParametroElemento().getDescricao()));
        elemento.setOutputMarkupId(true);
        elemento.addOrReplace(tDadosElemento);

        Label nome =
                new Label(ID_TITULO_ELEMENTO, new PropertyModel<String>(objElemento, "parametroElemento.descricao"));
        nome.setMarkupId(ID_TITULO_ELEMENTO + objElemento.getPk());
        nome.setOutputMarkupId(true);
        tDadosElemento.addOrReplace(nome);
        addComboNovaNotaElemento(tDadosElemento, objElemento);
        tDadosElemento.addOrReplace(botaoSalvar(objElemento, aqt));
        addAlertaDadosNaoSalvos(tDadosElemento, objElemento);
        ElementoAQT elementoARCVigente = elementoMediator.obterElementoANEFVigente(elementosARCVigente, objElemento);
        tDadosElemento.addOrReplace(new LabelLinhas("idNotaVigenteElemento",
                elementoARCVigente == null ? Constantes.VAZIO : elementoARCVigente.getNotaSupervisor()));
        PainelItemEdicaoAnef painelItemEdicaoAnef =
                new PainelItemEdicaoAnef("idPainelItem", ItemElementoAQTMediator.get().buscarItensOrdenadosDoElemento(
                        objElemento), elementoARCVigente, aqt, idsAlertas, anefVigente);
        tDadosElemento.addOrReplace(painelItemEdicaoAnef);
        listaPainelItemEdicaoAnef.add(painelItemEdicaoAnef);
        addAnaliseVigente(elemento, objElemento, elementoARCVigente);
    }

    private void addAnaliseVigente(ListItem<ElementoAQT> elemento, ElementoAQT objElemento,
            ElementoAQT elementoARCVigente) {
        WebMarkupContainer tDadosAnaliseElemento = new WebMarkupContainer("tDadosAnaliseElemento");
        tDadosAnaliseElemento.setMarkupId(tDadosAnaliseElemento.getId() + objElemento.getPk());
        elemento.addOrReplace(tDadosAnaliseElemento);

        boolean existeElementoVigente =
                elementoARCVigente != null && elementoARCVigente.getJustificativaSupervisor() != null
                        && !elementoARCVigente.getJustificativaSupervisor().isEmpty();

        Label tituloAnaliseVigente =
                new Label(ID_TITULO_ANALISE_VIGENTE, "Análise do supervisor para o elemento \""
                        + objElemento.getParametroElemento().getDescricao() + "\"");
        addDataOperadorAvaliacaoVigente(elementoARCVigente, tDadosAnaliseElemento);
        PainelDetalharAnaliseEmEdicaoAnef painel =
                new PainelDetalharAnaliseEmEdicaoAnef("idPainelJustificativaVigente", aqt, objElemento,
                        elementoARCVigente);
        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Análise vigente", true, new Component[] {painel}) {
                    @Override
                    public boolean isControleVisivel() {
                        return false;
                    }
                };
        grupo.addStyleGrupo("100");
        tDadosAnaliseElemento.add(tituloAnaliseVigente.setVisible(existeElementoVigente));
        tDadosAnaliseElemento.addOrReplace(painel.setVisible(existeElementoVigente));
        tDadosAnaliseElemento.addOrReplace(grupo.setVisible(existeElementoVigente));
    }

    private void addDataOperadorAvaliacaoVigente(ElementoAQT objElemento, WebMarkupContainer tDadosAnaliseElemento) {
        AnaliseQuantitativaAQT aqtVigente = objElemento.getAnaliseQuantitativaAQT();
        String mensagemVigente =
                "Analisado por " + Util.nomeOperador(aqtVigente.getOperadorConclusao()) + Constantes.EM
                        + aqtVigente.getData(aqtVigente.getDataConclusao());

        Label dataAtualizacaoElemento = new Label("idDataAnaliseVigenteAnef", mensagemVigente + Constantes.PONTO);
        dataAtualizacaoElemento.setVisible(!objElemento.getJustificativaAtualizada().equals(Constantes.VAZIO));
        tDadosAnaliseElemento.addOrReplace(dataAtualizacaoElemento);
    }

    private void addAlertaDadosNaoSalvos(WebMarkupContainer tDadosElemento, ElementoAQT objElemento) {
        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO, "Atenção nota não salva.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + objElemento.getPk());
        tDadosElemento.addOrReplace(alertaDadosNaoSalvos);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private void addComboNovaNotaElemento(final WebMarkupContainer tDadosElemento, final ElementoAQT elemento) {
        ChoiceRenderer<ParametroNotaAQT> renderer =
                new ChoiceRenderer<ParametroNotaAQT>("descricaoValor", ParametroNotaAQT.PROP_ID);
        List<ParametroNotaAQT> listaChoices = ciclo.getMetodologia().getNotasAnef();
        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(elemento, "parametroNotaInspetor");
        CustomDropDownChoice<ParametroNotaAQT> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNotaAQT>(ID_SELECT_NOVA_NOTA_ELEMENTO, "Selecione", propertyModel,
                        listaChoices, renderer);
        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavaScript(mostrarAlertas(elemento));
            }
        });
        selectNotaInspetor.setMarkupId(ID_SELECT_NOVA_NOTA_ELEMENTO + elemento.getPk());
        tDadosElemento.addOrReplace(selectNotaInspetor);
    }

    private String mostrarAlertas(ElementoAQT elemento) {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk(), true)
                + CKEditorUtils.jsAtualizarAlerta(EdicaoAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private void atualizarPanel(AjaxRequestTarget target, ElementoAQT elemento) {
        ((EdicaoAQT) getPage())
                .atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
        ((EdicaoAQT) getPage()).atualizarNovaNotaArc(target);
        ((EdicaoAQT) getPage()).atualizarNotaCalculadaInspetor(target);
    }

    private AjaxSubmitLinkSisAps botaoSalvar(final ElementoAQT elemento, final AnaliseQuantitativaAQT aqt) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(ID_BTT_SALVAR_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                elementoMediator.salvarNovaNotaElementoARC(aqt, elemento);
                mensagemDeSucesso(elemento);
                atualizarPanel(target, elemento);
            }
        };
        botaoSalvar.setMarkupId(ID_BTT_SALVAR_ELEMENTO + elemento.getPk());
        return botaoSalvar;
    }

    private void mensagemDeSucesso(final ElementoAQT elemento) {
        success("Nota para " + elemento.getParametroElemento().getDescricao() + " salva com sucesso.");
    }

    public List<ElementoAQT> getElementos() {
        return elementos;
    }

    public void validarAnexos() throws NegocioException {
        for (PainelItemEdicaoAnef painelItemEdicaoAnef : listaPainelItemEdicaoAnef) {
            painelItemEdicaoAnef.validarAnexos();
        }
    }
}