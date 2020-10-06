package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroTendencia;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroTendenciaMediator;
import br.gov.bcb.sisaps.src.mediator.TendenciaMediator;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.AnaliseAQT;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.AnalisarArcPage;

public class PainelTendenciaRiscoMercadoAnalise extends Panel {

    private static final String ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA = "idAlertaDadosNaoSalvosTendencia";
    private static final String ID_TEXT_AREA_NOVA_TENDENCIA = "idTextAreaNovaTendencia";

    private static final String ID_SELECT_NOVA_TENDENCIA = "idSelectNovaTendencia";

    private static final String PROP_JUSTIFICATIVA = "justificativa";
    private TendenciaARC tendenciaSupervisor;
    private WebMarkupContainer wmcExibirTendenciaSupervisor;
    private AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;
    private final boolean isTendenciaSupervisor;
    private final WebMarkupContainer wmcExibirTendenciaInspetor = new WebMarkupContainer("exibirTendenciaInspetor");
    private final boolean isTendenciaInspetor;
    private final boolean isConsulta;
    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private final List<String> idsAlertas;
    private TendenciaARC tendenciaVigente;

    public PainelTendenciaRiscoMercadoAnalise(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle,
            AvaliacaoRiscoControle avaliacao, Ciclo ciclo, boolean isTendenciaSupervisor, boolean isTendenciaInspetor,
            boolean isConsulta, List<String> idsAlertas) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.ciclo = ciclo;
        this.avaliacao = avaliacao;
        this.isTendenciaSupervisor = isTendenciaSupervisor;
        this.isTendenciaInspetor = isTendenciaInspetor;
        this.isConsulta = isConsulta;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addOrReplace(new FileUploadField("idFieldUploadAnexo"));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        avaliacao = AvaliacaoRiscoControleMediator.get().loadPK(avaliacao.getPk());
        tendenciaSupervisor = avaliacao.getTendenciaARCSupervisor();
        if (tendenciaSupervisor == null) {
            criarTendenciaVazia(avaliacao);
        }
        exibirTendenciaVigente();
        exibirTendenciaInspetor();
        exibirTendenciaSupervisor(avaliacao, ciclo);
    }

    private void criarTendenciaVazia(AvaliacaoRiscoControle arc) {
        tendenciaSupervisor = new TendenciaARC();
        tendenciaSupervisor.setAvaliacaoRiscoControle(arc);
        tendenciaSupervisor.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
    }

    private void exibirTendenciaVigente() {
        tendenciaVigente = avaliacao.getTendenciaARCVigente();
        if (isConsulta && tendenciaVigente == null) {
            tendenciaVigente = avaliacao.getTendenciaARCInspetorOuSupervisor();
        }

        String tituloTendenciaVigente = "Tendência vigente";
        if (isTendenciaInspetor) {
            tituloTendenciaVigente = "Tendência";
        }
        addNomeOperadorDataHora("idDataVigente", tendenciaVigente, null, null);
        Label idTituloTendenciaVigente = new Label("idTituloTendenciaVigente", tituloTendenciaVigente);
        addOrReplace(idTituloTendenciaVigente);
        addOrReplace(new Label("idNomeGrupoRiscoControle", parametroGrupoRiscoControle.getNome(avaliacao.getTipo())));
        addOrReplace(new Label("idTipoTendenciaVigente", tendenciaVigente == null ? 
                "" : tendenciaVigente.getParametroTendencia() == null ? 
                        "" : tendenciaVigente.getParametroTendencia().getNome()));

        addOrReplace(new LabelLinhas("idTendenciaVigente", (tendenciaVigente == null ? ""
                : tendenciaVigente.getJustificativa() == null ? "" : tendenciaVigente.getJustificativa()))
                .setEscapeModelStrings(false));
    }

    private void exibirTendenciaInspetor() {
        TendenciaARC tendenciaInspetor = avaliacao.getTendenciaARCInspetor();
        addNomeOperadorDataHora("idDataInspetor", avaliacao.getTendenciaARCInspetor(), tendenciaVigente,
                wmcExibirTendenciaInspetor);
        wmcExibirTendenciaInspetor.addOrReplace(new Label("idTipoTendenciaInspetor", tendenciaInspetor == null ? ""
                : tendenciaInspetor.getParametroTendencia() == null ? "" : tendenciaInspetor.getParametroTendencia()
                        .getNome()));
        wmcExibirTendenciaInspetor.addOrReplace(new LabelLinhas("idTendenciaInspetor", tendenciaInspetor == null ? ""
                : tendenciaInspetor.getJustificativa() == null ? "" : tendenciaInspetor.getJustificativa())
                .setEscapeModelStrings(false));
        wmcExibirTendenciaInspetor.setVisible(!isTendenciaInspetor);
        addOrReplace(wmcExibirTendenciaInspetor);
    }

    private void addNomeOperadorDataHora(String id, TendenciaARC tendencia, TendenciaARC tendenciaVigente,
            WebMarkupContainer conteiner) {
        String data = "";
        if (isExibirMensagemSemAlteracoes(tendencia, tendenciaVigente)) {
            data = "Sem alterações salvas.";
        } else
        if (tendencia != null && tendencia.getOperadorAtualizacao() != null) {
            data = "Atualizado por " + tendencia.getNomeOperadorDataHora() + ".";
        }
        Label dataAtualizacaoInspetor = new Label(id, data) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isConsulta);
            }
        };
        if (conteiner == null) {
            addOrReplace(dataAtualizacaoInspetor);
        } else {
            conteiner.addOrReplace(dataAtualizacaoInspetor);
        }

    }

    private boolean isExibirMensagemSemAlteracoes(TendenciaARC tendencia, TendenciaARC tendenciaVigente) {
        return isDadosTendenciaNaoNulos(tendencia, tendenciaVigente)
                && tendencia.getJustificativa().equals(tendenciaVigente.getJustificativa())
                && isParametroTendenciaAtualIgualAVigente(tendencia, tendenciaVigente);
    }

    private boolean isParametroTendenciaAtualIgualAVigente(TendenciaARC tendencia, TendenciaARC tendenciaVigente) {
        return (tendencia.getParametroTendencia() == null && tendenciaVigente.getParametroTendencia() != null) 
                || (tendenciaVigente.getParametroTendencia() != null 
                && tendenciaVigente.getParametroTendencia().equals(tendencia.getParametroTendencia()));
    }

    private boolean isDadosTendenciaNaoNulos(TendenciaARC tendencia, TendenciaARC tendenciaVigente) {
        return tendenciaVigente != null && tendencia != null && tendencia.getJustificativa() != null;
    }

    private void exibirTendenciaSupervisor(AvaliacaoRiscoControle avaliacao, Ciclo ciclo) {
        wmcExibirTendenciaSupervisor = new WebMarkupContainer("wmcTendenciaSupervisor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!isTendenciaSupervisor);
            }
        };

        addComboTendencia(ciclo);
        addTextArea();
        addNomeOperadorDataHora("idDataSupervisor", tendenciaSupervisor, tendenciaVigente, wmcExibirTendenciaSupervisor);

        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA, "Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
        wmcExibirTendenciaSupervisor.addOrReplace(alertaDadosNaoSalvos);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        wmcExibirTendenciaSupervisor.addOrReplace(botaoSalvar(ciclo, avaliacao));
        addOrReplace(wmcExibirTendenciaSupervisor);

    }

    private void addComboTendencia(Ciclo ciclo) {
        ChoiceRenderer<ParametroTendencia> renderer =
                new ChoiceRenderer<ParametroTendencia>("nome", ParametroTendencia.PROP_ID);
        List<ParametroTendencia> listaChoices =
                ParametroTendenciaMediator.get().buscarParametros(ciclo.getMetodologia().getPk());
        PropertyModel<ParametroTendencia> propertyModel =
                new PropertyModel<ParametroTendencia>(tendenciaSupervisor, "parametroTendencia");
        CustomDropDownChoice<ParametroTendencia> selectNotaInspetor =
                new CustomDropDownChoice<ParametroTendencia>(ID_SELECT_NOVA_TENDENCIA, "Selecione", propertyModel,
                        listaChoices, renderer);
        selectNotaInspetor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavaScript(mostrarAlerta());
            }
        });
        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.setMarkupId(ID_SELECT_NOVA_TENDENCIA);
        wmcExibirTendenciaSupervisor.addOrReplace(selectNotaInspetor);
    }

    private void addTextArea() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_TEXT_AREA_NOVA_TENDENCIA, new PropertyModel<String>(
                        tendenciaSupervisor, PROP_JUSTIFICATIVA)) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlerta();
                    }
                };
        wmcExibirTendenciaSupervisor.addOrReplace(justificativa);
    }

    private AjaxSubmitLinkSisAps botaoSalvar(final Ciclo ciclo, final AvaliacaoRiscoControle arc) {
        return new AjaxSubmitLinkSisAps("bttSalvarTendenciaARC") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                TendenciaMediator.get().salvarTendenciaARCEmAnalise(ciclo, arc, tendenciaSupervisor,
                        PerfisNotificacaoEnum.SUPERVISOR);
                if (tendenciaSupervisor.getParametroTendencia() != null
                        || tendenciaSupervisor.getJustificativa() != null) {
                    mensagemDeSucesso();
                }
                ((AnalisarArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
                ((AnalisarArcPage) getPage()).atualizarTendencia(target);
            }
        };
    }

    private String mostrarAlerta() {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA, true)
                + CKEditorUtils.jsAtualizarAlerta(AnaliseAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private void mensagemDeSucesso() {
        getPage().get("feedback").getFeedbackMessages().success(getPage(), "Tendência salva com sucesso.");
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

}
