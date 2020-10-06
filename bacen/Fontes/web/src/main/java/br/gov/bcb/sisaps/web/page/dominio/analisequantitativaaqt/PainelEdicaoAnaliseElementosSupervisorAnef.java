package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelEdicaoAnaliseElementosSupervisorAnef extends PainelSisAps {
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO =
            "idAlertaDadosNaoSalvosAvaliacaoElemento";
    private static final String ID_ULTIMA_ALTERACAO = "idUltimaAlteracao";
    private static final String ID_TEXT_AREA_NOVA_AVALIACAO = "idTextAreaNovaAvaliacao";
    private static final String BTT_SALVAR_AVALIACAO_ELEMENTO = "bttSalvarAvaliacaoARC";
    private static final String T_DADOS_ELEMENTOS = "tDadosAnaliseElementoSupervisor";
    private static final String ID_TITULO_ELEMENTO = "idTituloElementoSupervisor";
    private final ElementoAQT elemento;
    private final ElementoAQT elementoARCVigente;
    private final WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
    private final AnaliseQuantitativaAQT aqt;
    private CKEditorTextArea<String> justificativa;
    private List<String> idsAlertas = new ArrayList<String>();
    private Label labelUltimaAtualizacao;

    public PainelEdicaoAnaliseElementosSupervisorAnef(String id, AnaliseQuantitativaAQT aqt, ElementoAQT elemento,
            ElementoAQT elementoARCVigente, List<String> idsAlertas) {
        super(id);
        this.aqt = aqt;
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes(elemento);
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    private void addComponentes(ElementoAQT elemento) {
        String titulo =
                "Análise do supervisor para o elemento \"" + elemento.getParametroElemento().getDescricao() + "\"";
        Label nome = new Label(ID_TITULO_ELEMENTO, titulo);
        nome.setMarkupId(ID_TITULO_ELEMENTO + elemento.getPk());
        nome.setOutputMarkupId(true);
        addOrReplaceLabelUltimaAlteracao(elemento);
        painelElemento.add(nome);
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + elemento.getPk());

        Label alertaDadosNaoSalvos =
                new Label(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO, "Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO + elemento.getPk());
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        painelElemento.addOrReplace(alertaDadosNaoSalvos);
        painelElemento.add(botaoSalvar());
        addTextArea(painelElemento);

        PainelDetalharAnaliseEmEdicaoAnef painel =
                new PainelDetalharAnaliseEmEdicaoAnef("idPainelJustificativaVigente", aqt, elemento, elementoARCVigente);
        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Análise vigente", false, new Component[] {painel});
        grupo.setVisible(elementoARCVigente != null && elementoARCVigente.getJustificativaSupervisor() != null
                && !elementoARCVigente.getJustificativaSupervisor().isEmpty());

        addOrReplace(new Label("idUltimaVigenteAlteracao", elementoARCVigente == null ? ""
                : montarAlertaAnaliseVigente(elementoARCVigente, elementoARCVigente.getNomeOperadorAlteracaoDataHora())));
        addOrReplace(painel);
        addOrReplace(grupo);
        add(painelElemento);

    }

    private void addOrReplaceLabelUltimaAlteracao(ElementoAQT elemento) {
        ElementoAQT element = ElementoAQTMediator.get().buscarPorPk(elemento.getPk());
        labelUltimaAtualizacao =
                new Label(ID_ULTIMA_ALTERACAO, new Model<String>(montarAlertaNovaAnalise(element,
                        element.getNomeOperadorAlteracaoDataHora())));

        painelElemento.addOrReplace(labelUltimaAtualizacao);
    }

    private String montarAlertaNovaAnalise(ElementoAQT elemento, String ultimaAlteracaoDocumento) {
        return elemento == null || StringUtil.isVazioOuNulo(elemento.getJustificativaAtualizada()) ? "Sem alterações."
                : "Última alteração salva " + ultimaAlteracaoDocumento;
    }

    private String montarAlertaAnaliseVigente(ElementoAQT elemento, String ultimaAlteracaoDocumento) {
        return elemento == null || StringUtil.isVazioOuNulo(elemento.getJustificativaAtualizada()) ? ""
                : "Analisado por " + ultimaAlteracaoDocumento;
    }

    private void addTextArea(WebMarkupContainer painelElemento) {
        justificativa =
                new CKEditorTextArea<String>(ID_TEXT_AREA_NOVA_AVALIACAO, new PropertyModel<String>(elemento,
                        "justificativaSupervisor")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(
                                ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO + elemento.getPk(), true)
                                + CKEditorUtils.jsAtualizarAlerta(AnaliseAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
                    }
                };
        justificativa.setMarkupId(ID_TEXT_AREA_NOVA_AVALIACAO + elemento.getPk());
        justificativa.setOutputMarkupId(true);
        painelElemento.add(justificativa);
    }

    private AjaxSubmitLinkSisAps botaoSalvar() {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(BTT_SALVAR_AVALIACAO_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                ElementoAQTMediator.get().salvarNovaNotaElementoAQTSupervisor(aqt, elemento, false, true);
                mensagemDeSucesso(elemento);
                target.appendJavaScript(CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO
                        + elemento.getPk(), false));
                atualizarPanel(target, elemento);

            }
        };
        botaoSalvar.setMarkupId(BTT_SALVAR_AVALIACAO_ELEMENTO + elemento.getPk());
        botaoSalvar.setOutputMarkupId(true);
        return botaoSalvar;
    }

    private void atualizarPanel(AjaxRequestTarget target, ElementoAQT elemento) {
        addOrReplaceLabelUltimaAlteracao(elemento);
        target.add(labelUltimaAtualizacao);
        ((AnaliseAQT) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO
                        + elemento.getPk());
    }

    private void mensagemDeSucesso(final ElementoAQT elemento) {
        getPaginaAtual().success(
                " Análise do supervisor para \"" + elemento.getParametroElemento().getDescricao()
                        + "\" salva com sucesso.");
    }
}