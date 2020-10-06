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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoARCMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.drops.CustomDropDownChoice;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelAvaliacaoARCInspetor extends Panel {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO = "idAlertaDadosNaoSalvosAvaliacao";

    private static final String ID_JUSTIFICATIVA = "idJustificativa";

    private static final String ID_SELECT_NOVA_NOTA_ARC_AJUSTADA = "idSelectNovaNotaArcAjustada";

    @SpringBean
    private AvaliacaoARCMediator avaliacaoARCMediator;

    private final Ciclo ciclo;
    private final AvaliacaoRiscoControle arc;
    private AvaliacaoARC avaliacaoARC;
    private final Label novaNotaARC;
    private final WebMarkupContainer wmcJustificativa = new WebMarkupContainer("wmcJustificativa");

    public PainelAvaliacaoARCInspetor(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle arc, Ciclo ciclo) {
        super(id);
        setOutputMarkupId(true);
        this.ciclo = ciclo;
        this.arc = arc;
        avaliacaoARC =  AvaliacaoARCMediator.get().buscarPorIdArcEtipo(arc.getPk(), PerfisNotificacaoEnum.INSPETOR);

        if (avaliacaoARC == null) {
            avaliacaoARC = new AvaliacaoARC();
            avaliacaoARC.setAvaliacaoRiscoControle(arc);
            avaliacaoARC.setPerfil(PerfisNotificacaoEnum.INSPETOR);
        }

        add(new Label("idNomeGrupoRiscoControle", parametroGrupoRiscoControle.getNome(arc.getTipo())));

        String notaVigenteARC = AvaliacaoRiscoControleMediator.get().notaArcDescricaoValor(arc, ciclo,
                PerfilAcessoEnum.INSPETOR, true, true);
        add(new Label("idNotaVigenteArc", notaVigenteARC.equals(Constantes.ASTERISCO_A) ? "" : notaVigenteARC));

        novaNotaARC =
                new Label("idNovaNotaArc", new PropertyModel<String>(arc, AvaliacaoRiscoControleMediator.get()
                        .getDescricaoNotaCalculadaInspetor(arc)));
        add(novaNotaARC);
        addComboNovaNotaAjustada(avaliacaoARC, ciclo);
        addTextAreaJustificativa();
        add(botaoSalvar());
    }

    private void addTextAreaJustificativa() {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_JUSTIFICATIVA, new PropertyModel<String>(avaliacaoARC, "justificativa")) {
                    @Override
                    protected String onKeyUpJS() {
                        return mostrarAlertas();
                    }
                };
        wmcJustificativa.addOrReplace(justificativa);
        wmcJustificativa.setOutputMarkupId(true);
        wmcJustificativa.setMarkupId(wmcJustificativa.getMarkupId());
        wmcJustificativa.setOutputMarkupPlaceholderTag(true);
        wmcJustificativa.setVisible(avaliacaoARC.getParametroNota() != null);
        addOrReplace(wmcJustificativa);
    }

    private void addComboNovaNotaAjustada(AvaliacaoARC avaliacaoArc, Ciclo ciclo) {
        ChoiceRenderer<ParametroNota> renderer =
                new ChoiceRenderer<ParametroNota>("descricaoValor", ParametroNota.PROP_ID);
        List<ParametroNota> listaChoices = ciclo.getMetodologia().getNotasArc();
        listaChoices = SisapsUtil.removerParametroNotaNA(listaChoices);
        PropertyModel<ParametroNota> propertyModel = new PropertyModel<ParametroNota>(avaliacaoArc, "parametroNota");
        final CustomDropDownChoice<ParametroNota> selectNotaInspetor =
                new CustomDropDownChoice<ParametroNota>(ID_SELECT_NOVA_NOTA_ARC_AJUSTADA, "Selecione", propertyModel,
                        listaChoices, renderer);
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
                + CKEditorUtils.jsAtualizarAlerta(EdicaoArcPage.ID_ALERTA_DADOS_NAO_SALVOS, true);
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        return new AjaxSubmitLinkSisAps("bttSalvarAvaliacaoARC") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                if (avaliacaoARC.getParametroNota() == null) {
                    avaliacaoARC.setJustificativa(null);
                }
                avaliacaoARCMediator.salvarNovaNotaARC(ciclo, arc, avaliacaoARC);
                mensagemDeSucesso();
                ((EdicaoArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
                ((EdicaoArcPage) getPage()).atualizarNovaNotaArc(target);
            }
        };
    }

    private void mensagemDeSucesso() {
        success("Nova nota do ARC (ajustada) salva com sucesso.");
    }

    public void atualizarNovaNotaARC(AjaxRequestTarget target) {
        target.add(novaNotaARC);
    }

}
