package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroTendencia;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.TendenciaMediator;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class PainelTendencia extends Panel {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA = "idAlertaDadosNaoSalvosTendencia";

    private static final String ID_TEXT_AREA_NOVA_TENDENCIA = "idTextAreaNovaTendencia";

    private static final String ID_SELECT_NOVA_TENDENCIA = "idSelectNovaTendencia";

    private static final String PROP_JUSTIFICATIVA = "justificativa";

    private final WebMarkupContainer wmcExibirTendenciaVigente = new WebMarkupContainer("wmcExibirTendenciaVigente");

    @SpringBean
    private TendenciaMediator tendenciaMediator;

    private TendenciaARC tendencia = new TendenciaARC();

    private AvaliacaoRiscoControle arc;

    private final Ciclo ciclo;

    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    
    private static final BCLogger LOG = BCLogFactory.getLogger("PainelTendencia");

    public PainelTendencia(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle arc, Ciclo ciclo) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.arc = arc;
        this.ciclo = ciclo;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        arc = AvaliacaoRiscoControleMediator.get().buscar(arc.getPk());
        tendencia = arc.getTendenciaARCInspetor();

        if (tendencia == null) {
            criarTendenciaVazia(arc);
        }

        addOrReplace(new Label("idNomeGrupoRiscoControle", parametroGrupoRiscoControle.getNome(arc.getTipo())));

        TendenciaARC tendenciaVigente = arc.getTendenciaARCVigente();
        addComboTendencia(ciclo);
        addTextArea();
        addNomeOperadorDataHora("idDataInspetor", tendencia, tendenciaVigente, null);
        addOrReplace(botaoSalvar(ciclo, arc));

        addNomeOperadorDataHora("idDataVigente", tendenciaVigente, null, wmcExibirTendenciaVigente);
        wmcExibirTendenciaVigente.addOrReplace(new Label("idNomeTendencia", tendenciaVigente == null ? ""
                : tendenciaVigente.getParametroTendencia() == null ? "" : tendenciaVigente.getParametroTendencia()
                        .getNome()));
        wmcExibirTendenciaVigente.addOrReplace(new Label("idJustificativaTendencia", tendenciaVigente == null ? ""
                : tendenciaVigente.getJustificativa())
                .setEscapeModelStrings(false));
        wmcExibirTendenciaVigente.setOutputMarkupId(true);
        wmcExibirTendenciaVigente.setVisible(tendenciaVigente != null);
        setOutputMarkupId(true);
        add(wmcExibirTendenciaVigente);
    }

    private void addNomeOperadorDataHora(String id, TendenciaARC tendencia, TendenciaARC tendenciaVigente,
            WebMarkupContainer wmcExibirTendencia) {
        String data = "";
        
        if (id.equals("idDataInspetor") && (tendencia == null || tendenciaVigente == null)) {
            LOG.info("##BUGTENDENCIA arc: " + arc.getPk());
            System.out.println("##BUGTENDENCIA arc: " + arc.getPk());
        }
        
        if (tendenciaVigente != null
                && tendencia != null
                && tendencia.getJustificativa() != null
                && tendencia.getJustificativa().equals(tendenciaVigente.getJustificativa())
                && ((tendencia.getParametroTendencia() == null && tendenciaVigente.getParametroTendencia() != null)
                || (tendenciaVigente.getParametroTendencia() != null && tendenciaVigente.getParametroTendencia().equals(
                        tendencia.getParametroTendencia())))) {
            data = "Sem alterações salvas.";
        } else

        if (tendencia != null && tendencia.getOperadorAtualizacao() != null) {
            data = "Atualizado por " + tendencia.getNomeOperadorDataHora() + ".";
        }

        Label dataAtualizacaoInspetor = new Label(id, data);
        if (wmcExibirTendencia == null) {
            addOrReplace(dataAtualizacaoInspetor);
        } else {
            wmcExibirTendencia.addOrReplace(dataAtualizacaoInspetor);
        }

    }


    private void criarTendenciaVazia(AvaliacaoRiscoControle arc) {
        tendencia = new TendenciaARC();
        tendencia.setAvaliacaoRiscoControle(arc);
        tendencia.setPerfil(PerfisNotificacaoEnum.INSPETOR);
    }

    private void addComboTendencia(Ciclo ciclo) {
        ChoiceRenderer<ParametroTendencia> renderer =
                new ChoiceRenderer<ParametroTendencia>("nome", ParametroTendencia.PROP_ID);
        List<ParametroTendencia> listaChoices = ciclo.getMetodologia().getParametrosTendencia();
        PropertyModel<ParametroTendencia> propertyModel =
                new PropertyModel<ParametroTendencia>(tendencia, "parametroTendencia");
        CustomDropDownChoice<ParametroTendencia> selectNotaInspetor =
                new CustomDropDownChoice<ParametroTendencia>(ID_SELECT_NOVA_TENDENCIA, "Selecione", propertyModel,
                        listaChoices, renderer);
        selectNotaInspetor.setOutputMarkupId(true);
        selectNotaInspetor.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.appendJavaScript(mostrarAlertas());
            }
        });
        selectNotaInspetor.setMarkupId(ID_SELECT_NOVA_TENDENCIA);
        addOrReplace(selectNotaInspetor);
    }

    private void addTextArea() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_TEXT_AREA_NOVA_TENDENCIA, new PropertyModel<String>(tendencia,
                        PROP_JUSTIFICATIVA)) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlertas();
                    }
                };
        addOrReplace(justificativa);
    }

    private String mostrarAlertas() {
        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA, true)
                + CKEditorUtils.jsAtualizarAlerta(EdicaoArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private AjaxSubmitLinkSisAps botaoSalvar(final Ciclo ciclo, final AvaliacaoRiscoControle arc) {
        return new AjaxSubmitLinkSisAps("bttSalvarTendenciaARC") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                tendenciaMediator.salvarTendenciaARC(ciclo, arc, tendencia, PerfisNotificacaoEnum.INSPETOR);
                if (tendencia.getParametroTendencia() != null || tendencia.getJustificativa() != null) {
                    mensagemDeSucesso();
                }
                ((EdicaoArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
                ((EdicaoArcPage) getPage()).atualizarNovaNotaArc(target);
            }
        };
    }

    private void mensagemDeSucesso() {
        success("Tendência salva com sucesso.");
    }

}
