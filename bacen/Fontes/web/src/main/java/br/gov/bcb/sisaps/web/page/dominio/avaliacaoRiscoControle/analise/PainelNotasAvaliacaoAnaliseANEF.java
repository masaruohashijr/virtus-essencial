package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AvaliacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ParametroNotaAQTMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.AnaliseAQT;

public class PainelNotasAvaliacaoAnaliseANEF extends PainelSisAps {
    private static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO = "idAlertaDadosNaoSalvosAvaliacaoANEF";
    private static final String ID_JUSTIFICATIVA_SUPERVISOR = "idJustificativaSupervisorANEF";
    private static final String ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR = "idNovaNotaANEFAjustadaSupervisor";
    private AvaliacaoAQT avaliacaoARCSupervisor;
    private Label labelNotaCalculada;
    private AnaliseQuantitativaAQT aqt;
    private AnaliseQuantitativaAQT aqtVigente;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativaAnef");
    private CustomDropDownChoice<ParametroNotaAQT> selectNota;
    private final List<String> idsAlertas;

    public PainelNotasAvaliacaoAnaliseANEF(String id, AnaliseQuantitativaAQT aqt, List<String> idsAlertas) {
        super(id);
        this.aqt = aqt;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        AnaliseQuantitativaAQT anefVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        aqtVigente = AnaliseQuantitativaAQTMediator.get().buscar(anefVigente.getPk());
        avaliacaoARCSupervisor = aqt.avaliacaoSupervisor();

        if (avaliacaoARCSupervisor == null) {
            avaliacaoARCSupervisor = new AvaliacaoAQT();
            avaliacaoARCSupervisor.setAnaliseQuantitativaAQT(aqt);
            avaliacaoARCSupervisor.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        }

        addOrReplace(new Label("idNomeParamentroANEF", aqt.getParametroAQT().getDescricao()));
        addOrReplace(new Label("idNotaVigenteANEF", aqtVigente == null ? "" : getNotaVigente()));
        addDadosInspetor();
        addDadosSupervisor();

    }

    private String getNotaVigente() {
        String retorno = "";
        String notaAjustadaFinal =
                AnaliseQuantitativaAQTMediator.get().notaAjustadaFinal(aqtVigente, PerfilAcessoEnum.INSPETOR, true);
        if (StringUtils.isBlank(notaAjustadaFinal)) {
            retorno = aqtVigente.getNotaVigenteDescricaoValor();
        } else {
            retorno = notaAjustadaFinal;
        }
        return retorno;
    }

    private void addDadosInspetor() {
        AvaliacaoAQT avaliacao = aqt.getAvaliacaoInspetor();
        addOrReplace(new Label("idNotaCalculadaANEFInspetor", new PropertyModel<String>(aqt, "notaCalculadaInspetor")));
        Label labelNotaAjustadaInspetor =
                new Label("idNotaANEFAjustadaInspetor", avaliacao == null || avaliacao.getJustificativa() == null ? ""
                        : avaliacao.getValorFormatado());
        labelNotaAjustadaInspetor.setVisibilityAllowed(avaliacao != null);
        addOrReplace(labelNotaAjustadaInspetor);
        addOrReplace(new LabelLinhas("idJustificativaInspetorANEF", avaliacao == null
                || avaliacao.getJustificativa() == null ? "" : avaliacao.getJustificativa())
                .setEscapeModelStrings(false));
    }

    private void addDadosSupervisor() {

        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO, "Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        addOrReplace(alertaDadosNaoSalvos);

        labelNotaCalculada =
                new Label("idNotaCalculadaANEFSupervisor", new PropertyModel<AvaliacaoRiscoControle>(aqt,
                        "notaCalculadaSupervisorOuInspetor"));
        addOrReplace(labelNotaCalculada);
        labelNotaCalculada.add(new AttributeAppender("style", corNotaSupervisor(compararNotaCalculada())));

        addComboNovaNotaAjustada(avaliacaoARCSupervisor);
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_JUSTIFICATIVA_SUPERVISOR, new PropertyModel<String>(
                        avaliacaoARCSupervisor, "justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlerta();
                    }

                };
        justificativa.setOutputMarkupId(true);
        justificativa.setMarkupId(ID_JUSTIFICATIVA_SUPERVISOR + avaliacaoARCSupervisor.getPk());
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(avaliacaoARCSupervisor.getParametroNota() != null);
        addOrReplace(wmcJustificativa);

        addOrReplace(botaoSalvar());

    }

    private String mostrarAlerta() {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO, true)
                + CKEditorUtils.jsAtualizarAlerta(AnaliseAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }
    private boolean compararNotaCalculada() {
        return AnaliseQuantitativaAQTMediator.get().possuiNotaElementosSupervisor(aqt)
                || !aqt.getNotaCalculadaInspetor().equals(
                aqt.getNotaCalculadaSupervisorOuInspetor());
    }

    private void addComboNovaNotaAjustada(AvaliacaoAQT avaliacaoArc) {
        ChoiceRenderer<ParametroNotaAQT> renderer =
                new ChoiceRenderer<ParametroNotaAQT>("descricaoValor", ParametroNotaAQT.PROP_ID);
        List<ParametroNotaAQT> listaChoices =
                ParametroNotaAQTMediator.get().buscarNotaANEFAjusteSupervisor(aqt,
                        aqt.getCiclo().getMetodologia().getPk());

        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(avaliacaoArc, "parametroNota");
        selectNota =
                new CustomDropDownChoice<ParametroNotaAQT>(ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR, "Selecione",
                        propertyModel, listaChoices, renderer);
        selectNota.setOutputMarkupId(true);
        selectNota.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!DefaultPage.isUsarHtmlUnit()) {
                    target.appendJavaScript(mostrarAlerta());
                }

                if ("".equals(selectNota.getModelValue())) {
                    wmcJustificativa.setVisible(false);
                } else {
                    wmcJustificativa.setVisible(true);
                }
                target.add(wmcJustificativa);
            }

        });

        selectNota.setMarkupId(ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR);
        addOrReplace(selectNota);
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        return new AjaxSubmitLinkSisAps("bttSalvarNovaNotaAjusteANEF") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (avaliacaoARCSupervisor.getParametroNota() == null) {
                    avaliacaoARCSupervisor.setJustificativa(null);
                }

                AvaliacaoAQTMediator.get().salvarAterarAvalicaoSupervisor(aqt, avaliacaoARCSupervisor);
                mensagemDeSucesso();
                ((AnaliseAQT) getPage()).atualizarNovaNotaAnef(target);
                ((AnaliseAQT) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
            }

        };
    }

    private void mensagemDeSucesso() {
        getPage().get("feedback").getFeedbackMessages()
                .success(getPage(), "Nova nota do ANEF (ajustada) salva com sucesso.");
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private IModel<String> corNotaSupervisor(final boolean possui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (possui) {
                    return "color:#000000";
                } else {
                    return "color:#999999";
                }
            }
        };
        return model;
    }

    public Label getLabelNotaCalculada() {
        return labelNotaCalculada;
    }

}
