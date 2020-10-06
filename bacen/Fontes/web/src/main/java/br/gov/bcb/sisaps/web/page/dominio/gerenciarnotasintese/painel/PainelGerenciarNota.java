package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.ajustenotafinal.AjusteNotaFinal;

public class PainelGerenciarNota extends PainelSisAps {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS_NOTA_MATRIZ = "idAlertaDadosNaoSalvosNotaMatriz";

    @SpringBean
    private NotaMatrizMediator notaMatrizMediator;
    private final Matriz matriz;
    private NotaMatriz notaMatrizVigente = new NotaMatriz();
    private NotaMatriz notaMatrizRascunho = new NotaMatriz();
    private final Metodologia metodologia;
    private CustomDropDownChoice<ParametroNota> selectNotaRascunho;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativaArc");
    private PerfilRisco perfilAtual;

    public PainelGerenciarNota(String id, Metodologia metodologia, Matriz matriz) {
        super(id);
        this.metodologia = metodologia;
        this.matriz = matriz;
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(matriz.getCiclo().getPk());
        notaMatrizVigente = notaMatrizMediator.buscarPorPerfilRisco(perfilAtual.getPk());
        notaMatrizRascunho = notaMatrizMediator.buscarNotaMatrizRascunho(matriz);

        if (notaMatrizVigente == null) {
            notaMatrizVigente = new NotaMatriz();
            notaMatrizVigente.setMatriz(matriz);
        }
        if (notaMatrizRascunho == null) {
            notaMatrizRascunho = new NotaMatriz();
            notaMatrizRascunho.setMatriz(matriz);
            notaMatrizRascunho.setNotaMatrizAnterior(notaMatrizVigente.getPk() == null ? null : notaMatrizVigente);
        }
        addNotaAjustadaVigente();
        addNovaNotaMatrizAjustada();
        addBotaoSalvarNovaNotaMatrizAjustada();
        addBotaoConcluirNovaNotaMatrizAjustada();
    }

    private void addNotaAjustadaVigente() {
        addOrReplace(new Label("idDataNotaAjustadaVigente", notaMatrizVigente == null ? ""
                : notaMatrizMediator.dataHoraUsuarioAjusteVigenteCorec(notaMatrizVigente)));
        addOrReplace(new Label("idNotaAjustadaVigente", notaMatrizVigente == null ? ""
                : notaMatrizMediator.ajusteNotaFinal(notaMatrizVigente)));
        addOrReplace(new Label("idJustificativaNotaAjustadaVigente", notaMatrizVigente == null ? ""
                : notaMatrizVigente.getJustificativaNota()).setEscapeModelStrings(false));
    }

    private void addNovaNotaMatrizAjustada() {
        addOrReplace(new Label("idDataNovaNotaMatrizAjustada", notaMatrizMediator.dataHoraUsuarioAjusteRascunho(
                notaMatrizRascunho, notaMatrizVigente)));
        addComboNovaNotaMatrizAjustada();
        addTextAreaJustificativaNovaNotaMatriz();
    }

    private void addComboNovaNotaMatrizAjustada() {
        ChoiceRenderer<ParametroNota> renderer = new ChoiceRenderer<ParametroNota>("descricao", ParametroNota.PROP_ID);

        List<ParametroNota> listaChoices = metodologia.getNotasArcAjuste();

        PropertyModel<ParametroNota> propertyModel =
                new PropertyModel<ParametroNota>(notaMatrizRascunho, "notaFinalMatriz");

        selectNotaRascunho =
                new CustomDropDownChoice<ParametroNota>("idNovaNotaMatrizAjustada", "Remover ajuste", propertyModel,
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
        selectNotaRascunho.setMarkupId(selectNotaRascunho.getId());
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

    private void addTextAreaJustificativaNovaNotaMatriz() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>("idJustificativaNovaNotaMatriz", new PropertyModel<String>(
                        notaMatrizRascunho, "justificativaNota")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_NOTA_MATRIZ, true)
                                + atualizarBotoesVoltar();
                    }
                };
        justificativa.setEscapeModelStrings(true);
        justificativa.setOutputMarkupId(true);
        justificativa.setMarkupId("idNotaRascunho" + matriz.getPk());
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(notaMatrizRascunho.getNotaFinalMatriz() != null);
        addOrReplace(wmcJustificativa);
    }

    private void addBotaoSalvarNovaNotaMatrizAjustada() {
        addOrReplace(new AjaxSubmitLinkSisAps("bttSalvarNovaNotaMatrizAjustada") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if ("".equals(selectNotaRascunho.getModelValue())) {
                    notaMatrizRascunho.setJustificativaNota(null);
                }
                String msg = notaMatrizMediator.salvarNovaNotaMatrizAjustada(notaMatrizRascunho);
                mensagemDeSucessoSalvarConcluir(msg);
                atualizarPagina(target);
                target.add(PainelGerenciarNota.this);
            }
        });
    }

    private void atualizarPagina(AjaxRequestTarget target) {
        AjusteNotaFinal ajusteNotaFinal = (AjusteNotaFinal) getPage();
        ajusteNotaFinal.atualizarBotoesAlerta(target, ID_ALERTA_DADOS_NAO_SALVOS_NOTA_MATRIZ);
        ajusteNotaFinal.atualizarPerfilRiscoAtual();
        ajusteNotaFinal.atualizarPainelMatrizVigente(target);
    }

    private void mensagemDeSucessoSalvarConcluir(String msg) {
        success(msg);
    }

    private void addBotaoConcluirNovaNotaMatrizAjustada() {
        AjaxSubmitLinkSisAps botaoConcluirNovaNotaMatrizAjustada =
                new AjaxSubmitLinkSisAps("bttConcluirNovaNotaMatrizAjustada") {
                    @Override
                    public void executeSubmit(AjaxRequestTarget target) {
                        String msg =
                                notaMatrizMediator.confirmarNotaMatriz(notaMatrizRascunho, getNotaCalculadaFinal());
                        mensagemDeSucessoSalvarConcluir(msg);
                        atualizarPagina(target);
                        target.add(PainelGerenciarNota.this);
                    }

                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setEnabled(notaMatrizMediator.isHabilitarbotaoConfirmar(notaMatrizRascunho, notaMatrizVigente));
                    }
                };
        botaoConcluirNovaNotaMatrizAjustada.setOutputMarkupId(true);
        addOrReplace(botaoConcluirNovaNotaMatrizAjustada);
    }

    private void mostrarAlerta(AjaxRequestTarget target) {
        target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_NOTA_MATRIZ, true));
        target.appendJavaScript(atualizarBotoesVoltar());
    }

    private String atualizarBotoesVoltar() {
        return ((AjusteNotaFinal) getPage()).jsAtualizarBotoesVoltar();
    }

    private String getNotaCalculadaFinal() {
        return ((AjusteNotaFinal) getPage()).getNotaCalculadaFinal();
    }


}