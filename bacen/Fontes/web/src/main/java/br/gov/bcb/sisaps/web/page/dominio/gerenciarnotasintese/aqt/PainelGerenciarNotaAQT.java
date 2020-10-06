package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelGerenciarNotaAQT extends PainelSisAps {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS_NOTA = "idAlertaDadosNaoSalvosNota";

    @SpringBean
    private NotaAjustadaAEFMediator notaAjustadaAEFMediator;

    private final Ciclo ciclo;
    private NotaAjustadaAEF notaVigente = new NotaAjustadaAEF();
    private NotaAjustadaAEF notaRascunho = new NotaAjustadaAEF();
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativaAnef");

    private final Metodologia metodologia;

    private CustomDropDownChoice<ParametroNotaAQT> selectNotaRascunho;

    public PainelGerenciarNotaAQT(String id, Ciclo ciclo, Metodologia metodologia) {
        super(id);
        this.ciclo = ciclo;
        this.metodologia = metodologia;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        notaRascunho = notaAjustadaAEFMediator.buscarNotaAjustadaRascunho(ciclo);
        notaVigente = notaAjustadaAEFMediator.buscarNotaAjustadaAEF(ciclo, perfilRisco);
        if (notaRascunho == null) {
            notaRascunho = new NotaAjustadaAEF();
            notaRascunho.setCiclo(ciclo);
        }
        if (notaVigente == null) {
            notaVigente = new NotaAjustadaAEF();
            notaVigente.setCiclo(ciclo);
        }
        addNotaAjustadaVigente();
        addNovaNotaAjustada();
        addBotaoSalvarNovaNotaAjustada();
        addBotaoConcluirNovaNotaAjustada();
    }

    private void addNotaAjustadaVigente() {
        addOrReplace(new Label("idDataNotaAjustadaVigente", notaVigente == null ? ""
                : notaAjustadaAEFMediator.dataHoraUsuarioAjusteVigenteCorec(notaVigente)));
        addOrReplace(new Label("idNotaAjustadaVigente", notaVigente == null ? ""
                : notaAjustadaAEFMediator.ajusteNotaFinal(notaVigente)));
        addOrReplace(new Label("idJustificativaNotaAjustadaVigente", notaVigente == null ? ""
                : notaVigente.getJustificativa()).setEscapeModelStrings(false));
    }

    private void addNovaNotaAjustada() {
        addOrReplace(new Label("idDataNovaNotaAjustada", notaAjustadaAEFMediator.dataHoraUsuarioAjusteRascunho(
                notaRascunho, notaVigente)));
        addComboNovaNotaAjustada();
        addTextAreaJustificativaNovaNota();
    }

    private void addComboNovaNotaAjustada() {
        ChoiceRenderer<ParametroNotaAQT> renderer =
                new ChoiceRenderer<ParametroNotaAQT>("descricao", ParametroNotaAQT.PROP_ID);
        List<ParametroNotaAQT> listaChoices = metodologia.getNotasAnefAjuste();
        PropertyModel<ParametroNotaAQT> propertyModel =
                new PropertyModel<ParametroNotaAQT>(notaRascunho, "paramentroNotaAQT");
        selectNotaRascunho =
                new CustomDropDownChoice<ParametroNotaAQT>("idNovaNotaAjustada", "Remover ajuste", propertyModel,
                        listaChoices, renderer);
        selectNotaRascunho.setOutputMarkupId(true);
        selectNotaRascunho.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (!DefaultPage.isUsarHtmlUnit()) {
                    mostrarAlerta(target);
                }
                exibirJustificativa(target);
            }

        });
        selectNotaRascunho.setMarkupId(selectNotaRascunho.getMarkupId());
        addOrReplace(selectNotaRascunho);

    }

    private void exibirJustificativa(AjaxRequestTarget target) {
        if ("".equals(selectNotaRascunho.getModelValue())) {
            wmcJustificativa.setVisible(false);
        } else {
            wmcJustificativa.setVisible(true);
        }
        target.add(wmcJustificativa);
    }

    private void addTextAreaJustificativaNovaNota() {
        PropertyModel<String> propertyModel = new PropertyModel<String>(notaRascunho, "justificativa");
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>("idJustificativaNovaNota", propertyModel) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_NOTA, true)
                                + atualizarBotoesVoltar();
                    }
                };
        justificativa.setEscapeModelStrings(true);
        justificativa.setOutputMarkupId(true);
        justificativa.setMarkupId("idNotaRascunho" + ciclo.getPk());
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(notaRascunho.getParamentroNotaAQT() != null);
        addOrReplace(wmcJustificativa);
    }

    private void addBotaoSalvarNovaNotaAjustada() {
        addOrReplace(new AjaxSubmitLinkSisAps("bttSalvarNovaNotaAjustada") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if ("".equals(selectNotaRascunho.getModelValue())) {
                    notaRascunho.setJustificativa(null);
                }
                String msg = notaAjustadaAEFMediator.salvarNotaAjustadaAEF(notaRascunho);
                mensagemDeSucessoSalvarConcluir(msg);
                atualizarPagina(target);
                target.add(PainelGerenciarNotaAQT.this);
            }
        });
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        GerenciarNotaSinteseAQTPage gerenciarNotaSintesePage = (GerenciarNotaSinteseAQTPage) getPage();
        gerenciarNotaSintesePage.atualizarBotoesAlerta(target, ID_ALERTA_DADOS_NAO_SALVOS_NOTA);
        gerenciarNotaSintesePage.atualizarPerfilRiscoAtual();
        gerenciarNotaSintesePage.atualizarPainelNovoQuadroAQT(target);
        gerenciarNotaSintesePage.atualizaPainelQuadroVigenteAQT(target);
    }

    private void addBotaoConcluirNovaNotaAjustada() {
        AjaxSubmitLinkSisAps botaoConcluirNovaNotaAjustada = new AjaxSubmitLinkSisAps("bttConcluirNovaNotaAjustada") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String msg = notaAjustadaAEFMediator.confirmarNotaAjustadaAEF(notaRascunho, getNotaCalculadaFinal());
                mensagemDeSucessoSalvarConcluir(msg);
                atualizarPagina(target);
                target.add(PainelGerenciarNotaAQT.this);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setEnabled(notaAjustadaAEFMediator.isHabilitarbotaoConfirmar(notaRascunho, notaVigente));
            }
        };
        botaoConcluirNovaNotaAjustada.setOutputMarkupId(true);
        addOrReplace(botaoConcluirNovaNotaAjustada);
    }

    private void mensagemDeSucessoSalvarConcluir(String msg) {
        success(msg);
    }

    private void mostrarAlerta(AjaxRequestTarget target) {
        target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_NOTA, true));
        target.appendJavaScript(atualizarBotoesVoltar());
    }

    private String atualizarBotoesVoltar() {
        return ((GerenciarNotaSinteseAQTPage) getPage()).jsAtualizarBotoesVoltar();
    }

    private String getNotaCalculadaFinal() {
        return ((GerenciarNotaSinteseAQTPage) getPage()).getNotaCalculadaFinal();
    }

}