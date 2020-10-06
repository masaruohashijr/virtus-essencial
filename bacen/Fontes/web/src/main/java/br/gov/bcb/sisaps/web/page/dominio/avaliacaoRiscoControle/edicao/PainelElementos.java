package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelDetalharAnaliseEmEdicao;

public class PainelElementos extends Panel {

    private static final String ID_TITULO_ELEMENTO = "idTituloElemento";

    private static final String ID_TITULO_ANALISE_VIGENTE = "idTituloAnaliseVigente";

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO = "idAlertaDadosNaoSalvosElemento";

    private static final String ID_SELECT_NOVA_NOTA_ELEMENTO = "idSelectNovaNotaElemento";

    private static final String ID_BTT_SALVAR_ELEMENTO = "bttSalvarElemento";

    private final List<Elemento> elementosARCVigente = new ArrayList<Elemento>();

    @SpringBean
    private ElementoMediator elementoMediator;

    private final List<String> idsAlertas = new ArrayList<String>();

    public PainelElementos(String id, AvaliacaoRiscoControle arc, final List<Elemento> elementos, Ciclo ciclo) {
        super(id);
        if (arc.getAvaliacaoRiscoControleVigente() != null) {
            elementosARCVigente.addAll(ElementoMediator.get().buscarElementosOrdenadosDoArc(
                    arc.getAvaliacaoRiscoControleVigente().getPk()));
        }
        buildListView(elementos, ciclo, arc);
    }

    private void buildListView(List<Elemento> elementos, final Ciclo ciclo, final AvaliacaoRiscoControle arc) {
        ListView<Elemento> listView = new ListView<Elemento>("listaElementos", elementos) {
            @Override
            protected void populateItem(ListItem<Elemento> item) {
                addComponentesItem(item, ciclo, arc);
            }

        };
        add(listView);
    }

    private void addComponentesItem(ListItem<Elemento> elemento, Ciclo ciclo, AvaliacaoRiscoControle arc) {
        Elemento objElemento = elemento.getModelObject();
        WebMarkupContainer tDadosElemento = new WebMarkupContainer("tDadosElemento");
        tDadosElemento.setMarkupId(tDadosElemento.getId() + objElemento.getPk());
        elemento.setMarkupId(SisapsUtil.criarMarkupId(objElemento.getParametroElemento().getNome()));
        elemento.setOutputMarkupId(true);
        elemento.addOrReplace(tDadosElemento);

        Label nome = new Label(ID_TITULO_ELEMENTO, new PropertyModel<String>(objElemento, "parametroElemento.nome"));
        nome.setMarkupId(ID_TITULO_ELEMENTO + objElemento.getPk());
        nome.setOutputMarkupId(true);
        tDadosElemento.addOrReplace(nome);
        addComboNovaNotaElemento(tDadosElemento, objElemento, ciclo);
        tDadosElemento.addOrReplace(botaoSalvar(objElemento, ciclo, arc));
        addAlertaDadosNaoSalvos(tDadosElemento, objElemento);
        Elemento elementoARCVigente =
                elementoMediator.obterElementoCorrespondenteARCVigente(elementosARCVigente, objElemento);
        tDadosElemento.addOrReplace(new LabelLinhas("idNotaVigenteElemento", elementoARCVigente == null ? ""
                : elementoARCVigente.getNotaSupervisor()));
        tDadosElemento.addOrReplace(new PainelItem("idPainelItem", ItemElementoMediator.get()
                .buscarItensOrdenadosDoElemento(objElemento), elementoARCVigente, ciclo, arc, idsAlertas));
        addAnaliseVigente(elemento, objElemento, elementoARCVigente, arc, ciclo);
    }

    private void addAnaliseVigente(ListItem<Elemento> elemento, Elemento objElemento, Elemento elementoARCVigente,
            AvaliacaoRiscoControle arc, Ciclo ciclo) {
        WebMarkupContainer tDadosAnaliseElemento = new WebMarkupContainer("tDadosAnaliseElemento");
        tDadosAnaliseElemento.setMarkupId(tDadosAnaliseElemento.getId() + objElemento.getPk());
        elemento.addOrReplace(tDadosAnaliseElemento);

        boolean existeAnaliseElementoVigente =
                elementoARCVigente != null && elementoARCVigente.getJustificativaSupervisor() != null
                        && !elementoARCVigente.getJustificativaSupervisor().isEmpty();

        Label dataAnaliseElemento =
                new Label("idDataAvaliacao", existeAnaliseElementoVigente ? obterUsuarioUltimaAtualizacao(
                        existeAnaliseElementoVigente, arc) : "");
        dataAnaliseElemento.setVisible(existeAnaliseElementoVigente);
        tDadosAnaliseElemento.add(dataAnaliseElemento);

        Label tituloAnaliseVigente =
                new Label(ID_TITULO_ANALISE_VIGENTE, "Análise do supervisor para o elemento \""
                        + objElemento.getParametroElemento().getNome() + "\"");
        PainelDetalharAnaliseEmEdicao painel =
                new PainelDetalharAnaliseEmEdicao("idPainelJustificativaVigente", arc, ciclo, objElemento,
                        elementoARCVigente);
        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Análise vigente", true, new Component[] {painel});
        grupo.addStyleGrupo("100");
        tDadosAnaliseElemento.add(tituloAnaliseVigente.setVisible(existeAnaliseElementoVigente));
        tDadosAnaliseElemento.addOrReplace(painel.setVisible(existeAnaliseElementoVigente));
        tDadosAnaliseElemento.addOrReplace(grupo.setVisible(existeAnaliseElementoVigente));
    }

    private String obterUsuarioUltimaAtualizacao(boolean existeAnaliseElementoVigente, AvaliacaoRiscoControle arc) {
        return "Analisado por " + Util.nomeOperador(arc.getAvaliacaoRiscoControleVigente().getOperadorAnalise())
                + Constantes.EM + arc.getData(arc.getAvaliacaoRiscoControleVigente().getDataAnalise())
                + Constantes.PONTO;
    }

    private void addAlertaDadosNaoSalvos(WebMarkupContainer tDadosElemento, Elemento objElemento) {
        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO, "Atenção nota não salva.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + objElemento.getPk());
        tDadosElemento.addOrReplace(alertaDadosNaoSalvos);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private void addComboNovaNotaElemento(final WebMarkupContainer tDadosElemento, final Elemento elemento, Ciclo ciclo) {
        ChoiceRenderer<ParametroNota> renderer =
                new ChoiceRenderer<ParametroNota>("descricaoValor", ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices = ciclo.getMetodologia().getNotasArc();
        PropertyModel<ParametroNota> propertyModel =
                new PropertyModel<ParametroNota>(elemento, "parametroNotaInspetor");
        CustomDropDownChoice<ParametroNota> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNota>(ID_SELECT_NOVA_NOTA_ELEMENTO, "Selecione", propertyModel,
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

    private String mostrarAlertas(Elemento elemento) {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk(), true)
                + CKEditorUtils.jsAtualizarAlerta(EdicaoArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private void atualizarPanel(AjaxRequestTarget target, Elemento elemento) {
        ((EdicaoArcPage) getPage()).atualizarAlertaPrincipal(target,
                ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
        ((EdicaoArcPage) getPage()).atualizarNovaNotaArc(target);
        ((EdicaoArcPage) getPage()).atualizarPainelInformacoesArc(target);
    }

    private AjaxSubmitLinkSisAps botaoSalvar(final Elemento elemento, final Ciclo ciclo,
            final AvaliacaoRiscoControle arc) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(ID_BTT_SALVAR_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                elementoMediator.salvarNovaNotaElementoARC(ciclo, arc, elemento);
                mensagemDeSucesso(elemento);
                atualizarPanel(target, elemento);
            }
        };
        botaoSalvar.setMarkupId(ID_BTT_SALVAR_ELEMENTO + elemento.getPk());
        return botaoSalvar;
    }

    private void mensagemDeSucesso(final Elemento elemento) {
        success("Nota para " + elemento.getParametroElemento().getNome() + " salva com sucesso.");
    }

}
