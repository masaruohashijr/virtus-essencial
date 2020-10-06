package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.AnaliseAQT;

public class PainelEdicaoAnaliseElementosSupervisor extends PainelSisAps {
    private static final String ID_ULTIMA_ALTERACAO = "idUltimaAlteracao";
    private static final String ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO =
            "idAlertaDadosNaoSalvosAvaliacaoElemento";
    private static final String ID_TEXT_AREA_NOVA_AVALIACAO = "idTextAreaNovaAvaliacao";
    private static final String BTT_SALVAR_AVALIACAO_ELEMENTO = "bttSalvarAvaliacaoARC";
    private static final String T_DADOS_ELEMENTOS = "tDadosAnaliseElementoSupervisor";
    private static final String ID_TITULO_ELEMENTO = "idTituloElementoSupervisor";
    private final AvaliacaoRiscoControle avaliacao;
    private final Ciclo ciclo;
    private Elemento elemento;
    private final Elemento elementoARCVigente;
    private final WebMarkupContainer painelElemento = new WebMarkupContainer(T_DADOS_ELEMENTOS);
    private Label labelUltimaAteracao;
    private final List<String> idsAlertas;

    public PainelEdicaoAnaliseElementosSupervisor(String id, AvaliacaoRiscoControle avaliacao, Ciclo ciclo,
            Elemento elemento, Elemento elementoARCVigente, List<String> idsAlertas) {
        super(id);
        this.avaliacao = avaliacao;
        this.ciclo = ciclo;
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        this.idsAlertas = idsAlertas;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes(elemento);
    }

    private void addComponentes(Elemento elemento) {
        String titulo = "Análise do supervisor para o elemento \"" + elemento.getParametroElemento().getNome() + "\"";
        Label nome = new Label(ID_TITULO_ELEMENTO, titulo);
        nome.setMarkupId(ID_TITULO_ELEMENTO + elemento.getPk());
        nome.setOutputMarkupId(true);
        addOrReplaceLabelUltimaAlteracao(null);
        painelElemento.add(nome);
        painelElemento.setMarkupId(T_DADOS_ELEMENTOS + elemento.getPk());

        Label alertaDadosNaoSalvos =
                new Label(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO, "Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO + elemento.getPk());
        painelElemento.add(alertaDadosNaoSalvos);
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        painelElemento.add(botaoSalvar(elemento, ciclo, avaliacao));
        addTextArea(painelElemento);

        PainelDetalharAnaliseEmEdicao painel =
                new PainelDetalharAnaliseEmEdicao("idPainelJustificativaVigente", avaliacao, ciclo, elemento,
                        elementoARCVigente);
        GrupoExpansivel grupo =
                new GrupoExpansivel("GrupoEspansivelAvaliacao", "Avaliação vigente", false, new Component[] {painel});
        grupo.setVisible(elementoARCVigente != null && elementoARCVigente.getJustificativaSupervisor() != null
                && !elementoARCVigente.getJustificativaSupervisor().isEmpty());
        addOrReplace(painel);
        addOrReplace(grupo);
        add(painelElemento);

    }

    private void addOrReplaceLabelUltimaAlteracao(AjaxRequestTarget target) {
        labelUltimaAteracao =
                new Label(ID_ULTIMA_ALTERACAO, montarAlertaNovaAnalise(elemento.getNomeOperadorAlteracaoDataHora()));
        labelUltimaAteracao.setOutputMarkupId(true);
        labelUltimaAteracao.setMarkupId(ID_ULTIMA_ALTERACAO + elemento.getPk());
        painelElemento.addOrReplace(labelUltimaAteracao);
        if (target != null) {
            labelUltimaAteracao.setDefaultModelObject(montarAlertaNovaAnalise(getElemento()
                    .getNomeOperadorAlteracaoDataHora()));
            target.add(labelUltimaAteracao);
        }

    }

    private String montarAlertaNovaAnalise(String ultimaAlteracaoDocumento) {
        return elemento == null || StringUtil.isVazioOuNulo(elemento.getJustificativaAtualizada()) ? "Sem alterações."
                : "Última alteração salva " + ultimaAlteracaoDocumento;
    }

    private void addTextArea(WebMarkupContainer painelElemento) {
        CKEditorTextArea<String> justificativa =
                new CKEditorTextArea<String>(ID_TEXT_AREA_NOVA_AVALIACAO, new PropertyModel<String>(this,
                        "elemento.justificativaSupervisor")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(
                                ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO + elemento.getPk(), true)
                                + CKEditorUtils.jsAtualizarAlerta(AnaliseAQT.ID_ALERTA_DADOS_NAO_SALVOS, true);
                    }
                };
        justificativa.setMarkupId(ID_TEXT_AREA_NOVA_AVALIACAO + elemento.getPk());
        painelElemento.add(justificativa);
    }

    private AjaxSubmitLinkSisAps botaoSalvar(Elemento elemento, final Ciclo ciclo, final AvaliacaoRiscoControle arc) {
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(BTT_SALVAR_AVALIACAO_ELEMENTO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                submitSalvar(ciclo, arc, target);

            }

        };
        botaoSalvar.setMarkupId(BTT_SALVAR_AVALIACAO_ELEMENTO + elemento.getPk());
        return botaoSalvar;
    }

    private void submitSalvar(final Ciclo ciclo, final AvaliacaoRiscoControle arc, AjaxRequestTarget target) {
        ElementoMediator.get().salvarNovaNotaElementoARCSupervisor(ciclo, ciclo.getMatriz(), arc, elemento,
                ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula(), false, true);
        setElemento(ElementoMediator.get().buscarPorPk(elemento.getPk()));
        mensagemDeSucesso(elemento);
        addOrReplaceLabelUltimaAlteracao(target);
        target.add(painelElemento);
        ((AnalisarArcPage) getPage()).atualizarAlertaPrincipal(target, ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO
                + elemento.getPk());

        ((AnalisarArcPage) getPage()).atualizarPainelResumo(target);
    }

    private void mensagemDeSucesso(final Elemento elemento) {
        getPaginaAtual().success(
                " Análise do supervisor para o \"" + elemento.getParametroElemento().getNome()
                        + "\" salva com sucesso.");
    }

    public Elemento getElemento() {
        return elemento;
    }

    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }

}