package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AvaliacaoAQTMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.EdicaoArcPage;

public class PainelAvaliacaoANEFInspetor extends PainelSisAps {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO = "idAlertaDadosNaoSalvosAvaliacaoAnef";

    private static final String ID_JUSTIFICATIVA = "idJustificativaAnef";

    private static final String ID_SELECT_NOVA_NOTA_ARC_AJUSTADA = "idSelectNovaNotaANEFAjustada";

    private AvaliacaoAQT avaliacaoInspetorANEF;
    private final Label novaNotaARC;

    private final AnaliseQuantitativaAQT aqt;
    private final AnaliseQuantitativaAQT aqtVigente;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativaAnef");

    public PainelAvaliacaoANEFInspetor(String id, AnaliseQuantitativaAQT aqt) {
        super(id);
        this.aqt = aqt;
        setOutputMarkupId(true);
        avaliacaoInspetorANEF = aqt.getAvaliacaoInspetor();

        if (avaliacaoInspetorANEF == null) {
            avaliacaoInspetorANEF = new AvaliacaoAQT();
            avaliacaoInspetorANEF.setAnaliseQuantitativaAQT(aqt);
            avaliacaoInspetorANEF.setPerfil(PerfisNotificacaoEnum.INSPETOR);
        }
        aqtVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(aqt);
        add(new Label("idNomeParamentroANEF", aqt.getParametroAQT().getDescricao()));
        add(new Label("idNotaVigenteANEF", aqtVigente == null ? "" : getNotaVigente()));
        novaNotaARC = new Label("idNovaNotaANEF", new PropertyModel<String>(aqt, "notaCalculadaInspetor"));
        add(novaNotaARC);
        addComboNovaNotaAjustada(avaliacaoInspetorANEF, aqt.getCiclo());
        addTextAreaJustificativa();
        add(botaoSalvar());

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

    private void addTextAreaJustificativa() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_JUSTIFICATIVA, new PropertyModel<String>(avaliacaoInspetorANEF,
                        "justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlertas();
                    }
                };
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(avaliacaoInspetorANEF.getParametroNota() != null);
        addOrReplace(wmcJustificativa);
    }

    private void addComboNovaNotaAjustada(AvaliacaoAQT avaliacaoAqt, Ciclo ciclo) {
        ChoiceRenderer<ParametroNotaAQT> renderer =
                new ChoiceRenderer<ParametroNotaAQT>("descricaoValor", ParametroNotaAQT.PROP_ID);
        List<ParametroNotaAQT> listaChoices = ciclo.getMetodologia().getNotasAnefSemNA();
        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(avaliacaoAqt, "parametroNota");
        final CustomDropDownChoice<ParametroNotaAQT> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNotaAQT>(ID_SELECT_NOVA_NOTA_ARC_AJUSTADA, "Selecione",
                        propertyModel, listaChoices, renderer);
        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onUpdateSelectNotaInspetor(selectNotaInspetor, target);
            }

        });
        selectNotaInspetor.setMarkupId(ID_SELECT_NOVA_NOTA_ARC_AJUSTADA);
        addOrReplace(selectNotaInspetor);
    }

    private void onUpdateSelectNotaInspetor(final CustomDropDownChoice<ParametroNotaAQT> selectNotaInspetor,
            AjaxRequestTarget target) {
        boolean repintar = true;
        if (!DefaultPage.isUsarHtmlUnit()) {
            target.appendJavaScript(mostrarAlertas());
        }
        if (wmcJustificativa.isVisible()) {
            repintar = false;
        }

        if ("".equals(selectNotaInspetor.getModelValue())) {
            repintar = true;
            wmcJustificativa.setVisible(false);
        } else {
            wmcJustificativa.setVisible(true);
        }

        if (repintar) {
            target.add(wmcJustificativa);
        }
    }

    private String mostrarAlertas() {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO, true)
                + CKEditorUtils.jsAtualizarAlerta(EdicaoArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        return new AjaxSubmitLinkSisAps("bttSalvarAvaliacaoANEF") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (avaliacaoInspetorANEF.getParametroNota() == null) {
                    avaliacaoInspetorANEF.setJustificativa(null);
                }
                AvaliacaoAQTMediator.get().salvarAterarAvalicaoInspetor(aqt, avaliacaoInspetorANEF);
                mensagemDeSucesso();
                ((EdicaoAQT) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
                ((EdicaoAQT) getPage()).atualizarNovaNotaArc(target);
                ((EdicaoAQT) getPage()).atualizarPaineis(target);
            }
        };
    }

    private void mensagemDeSucesso() {
        success("Nova nota do ANEF (ajustada) salva com sucesso.");
    }

    public void atualizarNovaNotaARC(AjaxRequestTarget target) {
        target.add(novaNotaARC);
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

}
