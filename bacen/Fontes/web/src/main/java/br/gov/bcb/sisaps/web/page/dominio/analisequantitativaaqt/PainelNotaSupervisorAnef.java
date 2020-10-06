package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelNotaSupervisorAnef extends PainelSisAps {

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO = "idAlertaDadosNaoSalvosElemento";
    private static final String BTT_SALVAR_ELEMENTO = "bttSalvarSupervisorElemento";
    private static final String ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO = "idSelectaNotaSupervisorElemento";
    private final WebMarkupContainer wmcExibirNotaEditavel = new WebMarkupContainer("wmcExibirNotaEditavel");
    private final List<String> idsAlertas;

    public PainelNotaSupervisorAnef(String id, ElementoAQT elemento, ElementoAQT elementoAQTVigente,
            List<String> idsAlertas) {
        super(id);
        this.idsAlertas = idsAlertas;
        notaSupervisorEditavel(elemento, elemento.getAnaliseQuantitativaAQT().getCiclo(),
                elemento.getAnaliseQuantitativaAQT());
        notaInspetorLabel(elemento);
        notaVigenteLabel(elementoAQTVigente);
        wmcExibirNotaEditavel.setVisibilityAllowed(exibirCampoEditavel(elemento));
        addOrReplace(wmcExibirNotaEditavel);
    }

    private boolean exibirCampoEditavel(ElementoAQT elemento) {
        return elemento == null || (elemento != null && elemento.getParametroNotaInspetor() == null) ? false : elemento
                .getParametroNotaInspetor().getValor().compareTo(BigDecimal.ZERO) == 1;
    }

    private void notaVigenteLabel(ElementoAQT elementoARCVigente) {
        addOrReplace(new LabelLinhas("idNotaVigenteElemento", elementoARCVigente == null ? ""
                : elementoARCVigente.getNotaSupervisor()));
    }

    private void notaInspetorLabel(ElementoAQT elemento) {
        addOrReplace(new Label("idNotaInspetorElemento", new PropertyModel<String>(elemento,
                "parametroNotaInspetor.descricaoValor")));
    }

    private void notaSupervisorEditavel(ElementoAQT elemento, Ciclo ciclo, AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        addComboNovaNotaElemento(elemento);
        wmcExibirNotaEditavel.addOrReplace(botaoSalvar(elemento, analiseQuantitativaAQT));
        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO, "Atenção nota não salva.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        wmcExibirNotaEditavel.addOrReplace(alertaDadosNaoSalvos);
    }

    private void addComboNovaNotaElemento(final ElementoAQT elemento) {
        ChoiceRenderer<ParametroNotaAQT> renderer =
                new ChoiceRenderer<ParametroNotaAQT>("descricaoValor", ParametroNotaAQT.PROP_ID);
        List<ParametroNotaAQT> listaChoices =
                ParametroNotaAQTMediator.get().buscarNotaANEFElementoSupervisor(elemento,
                        elemento.getAnaliseQuantitativaAQT().getCiclo().getMetodologia().getPk());

        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(elemento, "parametroNotaSupervisor");
        CustomDropDownChoice<ParametroNotaAQT> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNotaAQT>(ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO, "Selecione",
                        propertyModel, listaChoices, renderer);

        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(
                        ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk(), true)
                        + CKEditorUtils.jsAtualizarAlerta(AnaliseAQT.ID_ALERTA_DADOS_NAO_SALVOS, true));
            }
        });
        selectNotaInspetor.setMarkupId(ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO + elemento.getPk());
        wmcExibirNotaEditavel.addOrReplace(selectNotaInspetor);

    }

    private AjaxSubmitLinkSisAps botaoSalvar(final ElementoAQT elemento,
            final AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(BTT_SALVAR_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                ElementoAQTMediator.get().salvarNovaNotaElementoAQTSupervisor(analiseQuantitativaAQT, elemento, true,
                        false);

                mensagemDeSucesso(elemento);
                atualizarPanel(target, elemento);
            }
        };
        botaoSalvar.setMarkupId(BTT_SALVAR_ELEMENTO + elemento.getPk());
        return botaoSalvar;
    }

    private void atualizarPanel(AjaxRequestTarget target, ElementoAQT elemento) {
        ((AnaliseAQT) getPage()).atualizarNovaNotaAnef(target);
        ((AnaliseAQT) getPage()).atualizarAlertaPrincipal(target,
                ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
    }

    private void mensagemDeSucesso(final ElementoAQT elemento) {
        getPage()
                .get("feedback")
                .getFeedbackMessages()
                .success(getPage(),
                        "Nota para \"" + elemento.getParametroElemento().getDescricao() + "\" salva com sucesso.");
    }

}
