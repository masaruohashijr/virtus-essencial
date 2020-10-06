package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.math.BigDecimal;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelNotaSupervisor extends PainelSisAps {

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO = "idAlertaDadosNaoSalvosElemento";
    private static final String BTT_SALVAR_ELEMENTO = "bttSalvarSupervisorElemento";
    private static final String ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO = "idSelectaNotaSupervisorElemento";
    private final WebMarkupContainer wmcExibirNotaEditavel = new WebMarkupContainer("wmcExibirNotaEditavel");
    private final List<String> idsAlertas;

    public PainelNotaSupervisor(String id, Elemento elemento, Elemento elementoARCVigente, Ciclo ciclo,
            AvaliacaoRiscoControle avaliacao, List<String> idsAlertas) {
        super(id);
        this.idsAlertas = idsAlertas;
        notaSupervisorEditavel(elemento, ciclo, avaliacao);
        notaInspetorLabel(elemento);
        notaVigenteLabel(elementoARCVigente);
        wmcExibirNotaEditavel.setVisibilityAllowed(exibirCampoEditavel(elemento));
        addOrReplace(wmcExibirNotaEditavel);
    }

    private boolean exibirCampoEditavel(Elemento elemento) {
        return elemento != null && elemento.getParametroNotaInspetor() != null
                && elemento.getParametroNotaInspetor().getValor().compareTo(BigDecimal.ZERO) == 1;
    }

    private void notaVigenteLabel(Elemento elementoARCVigente) {
        addOrReplace(new LabelLinhas("idNotaVigenteElemento", elementoARCVigente == null ? ""
                : elementoARCVigente.getNotaSupervisor()));
    }

    private void notaInspetorLabel(Elemento elemento) {
        addOrReplace(new Label("idNotaInspetorElemento", new PropertyModel<String>(elemento,
                "parametroNotaInspetor.descricaoValor")));
    }

    private void notaSupervisorEditavel(Elemento elemento, Ciclo ciclo, AvaliacaoRiscoControle avaliacao) {
        addComboNovaNotaElemento(elemento, ciclo);
        wmcExibirNotaEditavel.addOrReplace(botaoSalvar(elemento, ciclo, avaliacao));
        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO, "Atenção nota não salva.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        wmcExibirNotaEditavel.addOrReplace(alertaDadosNaoSalvos);
    }

    private void addComboNovaNotaElemento(final Elemento elemento, Ciclo ciclo) {
        ChoiceRenderer<ParametroNota> renderer =
                new ChoiceRenderer<ParametroNota>("descricaoValor", ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices = ciclo.getMetodologia().getNotasArc();
        listaChoices = SisapsUtil.removerParametroNotaInspetor(elemento, listaChoices);
        PropertyModel<ParametroNota> propertyModel =
                new PropertyModel<ParametroNota>(elemento, "parametroNotaSupervisor");
        CustomDropDownChoice<ParametroNota> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNota>(ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO, "Selecione",
                        propertyModel, listaChoices, renderer);

        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(
                        ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk(), true)
                        + CKEditorUtils.jsAtualizarAlerta(AnalisarArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true));
            }
        });
        selectNotaInspetor.setMarkupId(ID_SELECTA_NOTA_SUPERVISOR_ELEMENTO + elemento.getPk());
        wmcExibirNotaEditavel.addOrReplace(selectNotaInspetor);

    }

    private AjaxSubmitLinkSisAps botaoSalvar(final Elemento elemento, final Ciclo ciclo,
            final AvaliacaoRiscoControle arc) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(BTT_SALVAR_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                ElementoMediator.get().salvarNovaNotaElementoARCSupervisor(ciclo, ciclo.getMatriz(), arc, elemento,
                        ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula(), true, false);
                mensagemDeSucesso(elemento);
                atualizarPanel(target, elemento);
            }
        };
        botaoSalvar.setMarkupId(BTT_SALVAR_ELEMENTO + elemento.getPk());
        return botaoSalvar;
    }

    private void atualizarPanel(AjaxRequestTarget target, Elemento elemento) {
        ((AnalisarArcPage) getPage()).atualizarAlertaPrincipal(target,
                ID_ALERTA_DADOS_NAO_SALVOS_ELEMENTO + elemento.getPk());
        ((AnalisarArcPage) getPage()).atualizarPainelResumo(target);
        ((AnalisarArcPage) getPage()).atualizarNovaNotaArc(target);
    }

    private void mensagemDeSucesso(final Elemento elemento) {
        getPage()
                .get("feedback")
                .getFeedbackMessages()
                .success(getPage(),
                        "Nota para \"" + elemento.getParametroElemento().getNome() + "\" salva com sucesso.");
    }

}
