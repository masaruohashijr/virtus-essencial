package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoARCMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelRiscoDeMercado extends PainelSisAps {
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO = "idAlertaDadosNaoSalvosAvaliacao";
    private static final String ID_JUSTIFICATIVA_SUPERVISOR = "idJustificativaSupervisor";
    private static final String ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR = "idNovaNotaArcAjustadaSupervisor";
    private AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;
    private AvaliacaoARC avaliacaoARCSupervisor;
    private Label labelNotaCalculada;
    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private final List<String> idsAlertas;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativa");

    public PainelRiscoDeMercado(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle avaliacao, Ciclo ciclo, List<String> idsAlertas) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new Label("idNomeDoGrupo", new PropertyModel<String>(parametroGrupoRiscoControle,
                "parametroGrupoRiscoControle.nome") {
            @Override
            public String getObject() {
                return parametroGrupoRiscoControle.getNome(avaliacao.getTipo());
            }
        }));
        add(new Label("idNotaVigenteArc", AvaliacaoRiscoControleMediator.get().notaArcDescricaoValor(avaliacao, ciclo,
                getPerfilPorPagina(), true, true)));
        addDadosInspetor();
        addDadosSupervisor();
        
    }
    
    private void addDadosInspetor() {
        add(new Label("idNotaArrastoArcInspetor", new PropertyModel<String>(avaliacao, AvaliacaoRiscoControleMediator
                .get().getDescricaoNotaCalculadaInspetor(avaliacao))));
        AvaliacaoARC avaliacaoArcInspetor = avaliacao.getAvaliacaoARCInspetor();
        add(new Label("idNotaArcAjustadaInspetor", avaliacaoArcInspetor == null
                || avaliacaoArcInspetor.getJustificativa() == null ? "" : avaliacaoArcInspetor.getValorFormatado()));
        add(new LabelLinhas("idJustificativaInspetor", avaliacaoArcInspetor == null
                || avaliacaoArcInspetor.getJustificativa() == null ? "" : avaliacaoArcInspetor.getJustificativa())
                .setEscapeModelStrings(false));
    }

    private void addDadosSupervisor() {

        Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO, "Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        addOrReplace(alertaDadosNaoSalvos);

        labelNotaCalculada =
                new Label("idNotaArrastoArcSupervisor", new PropertyModel<AvaliacaoRiscoControle>(getAvaliacao(),
                        AvaliacaoRiscoControleMediator.get().getDescricaoNotaCalculadaSupervisor(avaliacao))) {
                    @Override
                    protected void onConfigure() {
                        add(AttributeModifier.replace("color",
                                corNotaSupervisor(avaliacao.possuiNotaElementosSupervisor())));
                    }

                };
        addOrReplace(labelNotaCalculada);
        avaliacaoARCSupervisor = avaliacao.avaliacaoSupervisor();

        if (avaliacaoARCSupervisor == null) {
            avaliacaoARCSupervisor = new AvaliacaoARC();
            avaliacaoARCSupervisor.setAvaliacaoRiscoControle(avaliacao);
            avaliacaoARCSupervisor.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        }

        addComboNovaNotaAjustada(avaliacaoARCSupervisor, ciclo);
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_JUSTIFICATIVA_SUPERVISOR, new PropertyModel<String>(
                        avaliacaoARCSupervisor, "justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlertas();
                    }
                };
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(avaliacaoARCSupervisor.getParametroNota() != null);
        addOrReplace(wmcJustificativa);
        addOrReplace(botaoSalvar());

    }

    private void addComboNovaNotaAjustada(AvaliacaoARC avaliacaoArc, Ciclo ciclo) {
        ChoiceRenderer<ParametroNota> renderer =
                new ChoiceRenderer<ParametroNota>("descricaoValor", ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices = ciclo.getMetodologia().getNotasArc();
        listaChoices = SisapsUtil.removerParametroNotaInspetor(avaliacao, null, listaChoices);

        PropertyModel<ParametroNota> propertyModel = new PropertyModel<ParametroNota>(avaliacaoArc, "parametroNota");
        final CustomDropDownChoice<ParametroNota> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNota>(ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR, "Selecione",
                        propertyModel, listaChoices, renderer);
        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onUpdateSelectNotaInspetor(selectNotaInspetor, target);
            }
        });
        selectNotaInspetor.setMarkupId(ID_NOVA_NOTA_ARC_AJUSTADA_SUPERVISOR);
        addOrReplace(selectNotaInspetor);
    }

    private void onUpdateSelectNotaInspetor(final CustomDropDownChoice<ParametroNota> selectNotaInspetor,
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
                + CKEditorUtils.jsAtualizarAlerta(AnalisarArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        return new AjaxSubmitLinkSisAps("bttSalvarNovaNotaAjuste") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (avaliacaoARCSupervisor.getParametroNota() == null) {
                    avaliacaoARCSupervisor.setJustificativa(null);
                }
                AvaliacaoARCMediator.get().salvarAvaliacaoARCSupervisor(ciclo, avaliacao, avaliacaoARCSupervisor);
                AvaliacaoARCMediator.get().flush();
                mensagemDeSucesso();
                ((AnalisarArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
                ((AnalisarArcPage) getPage()).atualizarPainelResumo(target);
            }
        };
    }

    private void mensagemDeSucesso() {
        getPage().get("feedback").getFeedbackMessages()
                .success(getPage(), "Nova nota do ARC (ajustada) salva com sucesso.");
    }

    private IModel<String> corNotaSupervisor(final boolean possui) {
        IModel<String> model = new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                if (possui) {
                    return "#000000";
                } else {
                    return "#999999";
                }
            }
        };
        return model;
    }

    public Label getLabelNotaCalculada() {
        return labelNotaCalculada;
    }

    public AvaliacaoRiscoControle getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

}
