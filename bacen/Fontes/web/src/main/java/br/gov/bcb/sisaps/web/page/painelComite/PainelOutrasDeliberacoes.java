package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public class PainelOutrasDeliberacoes extends PainelSisAps {
    private static final String BTT_SALVAR_INFORMACOES = "bttSalvarInformacoes";
    private static final String BTT_SALVAR_INFORMACOES_DESABILITADO = "bttSalvarInformacoesDesabilitado";
    private static final String ID_TEXT_AREA_OUTRAS_DELIBERACOES = "idTextAreaOutrasDeliberacoes";
    private static final String T_DADOS_OUTRAS_DELIBERACOES = "tDadosOutrasDeliberacoes";
    private static final String ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos";

    private final List<String> idsAlertas;

    private final WebMarkupContainer painelOutrasDeliberacoes = new WebMarkupContainer(T_DADOS_OUTRAS_DELIBERACOES);

    private AjusteCorec ajusteCorec;
    private Label alertaDadosNaoSalvos;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoes;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoesDesabilitado;
    private String markupIdAlerta;
    private CKEditorTextArea<String> outrasDeliberacoes;
    private final PerfilRisco perfilRiscoAtual;

    public PainelOutrasDeliberacoes(String id, PerfilRisco perfilRiscoAtual) {
        super(id);
        setOutputMarkupId(true);
        this.idsAlertas = new ArrayList<String>();
        this.perfilRiscoAtual = perfilRiscoAtual;
        criarAjuste(perfilRiscoAtual);
        addComponentes();
    }

    private void criarAjuste(PerfilRisco perfilRiscoAtual) {
        this.ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(perfilRiscoAtual.getCiclo());
    }

    private void addComponentes() {
        painelOutrasDeliberacoes.setMarkupId(T_DADOS_OUTRAS_DELIBERACOES);

        alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS, "*Atenção informações não salvas.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS);

        setMarkupIdAlerta(ajusteCorec + alertaDadosNaoSalvos.getId());
        idsAlertas.add(alertaDadosNaoSalvos.getMarkupId());
        alertaDadosNaoSalvos.setMarkupId(getMarkupIdAlerta());
        painelOutrasDeliberacoes.addOrReplace(alertaDadosNaoSalvos);

        addTextArea();
        addBotaoSalvarInformacoes();

        addOrReplace(painelOutrasDeliberacoes);
    }

    private void addTextArea() {
        outrasDeliberacoes =
                new CKEditorTextArea<String>(ID_TEXT_AREA_OUTRAS_DELIBERACOES, new PropertyModel<String>(ajusteCorec,
                        "outrasDeliberacoes")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(alertaDadosNaoSalvos.getMarkupId(), true)
                                + atualizarBotoesVoltar() + atualizarBotoesSalvar(true);
                    }
                };

        outrasDeliberacoes.setMarkupId(ID_TEXT_AREA_OUTRAS_DELIBERACOES);
        painelOutrasDeliberacoes.addOrReplace(outrasDeliberacoes);
    }

    private void addBotaoSalvarInformacoes() {
        botaoSalvarInformacoesDesabilitado = new AjaxSubmitLinkSisAps(BTT_SALVAR_INFORMACOES_DESABILITADO) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {

            }

        };

        botaoSalvarInformacoesDesabilitado.setEnabled(false);
        botaoSalvarInformacoesDesabilitado.setOutputMarkupPlaceholderTag(true);
        painelOutrasDeliberacoes.addOrReplace(botaoSalvarInformacoesDesabilitado);

        botaoSalvarInformacoes = new AjaxSubmitLinkSisAps(BTT_SALVAR_INFORMACOES) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                AjusteCorecMediator.get().salvarAjusteCorec(atualizarAjuste());
                getPaginaAtual().success("Campo 'Outras deliberações' salvo com sucesso.");
                atualizarBotoesSalvar(false);
                alertaDadosNaoSalvos.setEnabled(false);
                target.add(alertaDadosNaoSalvos);
                target.add(botaoSalvarInformacoes);
                target.add(botaoSalvarInformacoesDesabilitado);
                ((GestaoCorecPage) getPage()).atualizarBotaoEncerrarCorec(target);
                ((GestaoCorecPage) getPage()).exibirBotaoVoltarAoSalvar();
            }

        };

        botaoSalvarInformacoes.setOutputMarkupPlaceholderTag(true);
        painelOutrasDeliberacoes.addOrReplace(botaoSalvarInformacoes);
    }

    private AjusteCorec atualizarAjuste() {
        AjusteCorec ajusteCorecBase = AjusteCorecMediator.get().buscarPorCiclo(perfilRiscoAtual.getCiclo());
        ajusteCorecBase.setOutrasDeliberacoes(ajusteCorec.getOutrasDeliberacoes());
        return ajusteCorecBase;
    }

    private String atualizarBotoesVoltar() {
        return ((GestaoCorecPage) getPage()).jsAtualizarBotoesVoltar();
    }

    private String atualizarBotoesSalvar(boolean mostrarBotaoSalvar) {
        return ((GestaoCorecPage) getPage()).jsAtualizarBotoesSalvar(mostrarBotaoSalvar);
    }

    public AjaxSubmitLinkSisAps getBotaoSalvarInformacoes() {
        return botaoSalvarInformacoes;
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    public String getMarkupIdAlerta() {
        return markupIdAlerta;
    }

    public void setMarkupIdAlerta(String markupIdAlerta) {
        this.markupIdAlerta = markupIdAlerta;
    }

    public AjaxSubmitLinkSisAps getBotaoSalvarInformacoesDesabilitado() {
        return botaoSalvarInformacoesDesabilitado;
    }

    public void setBotaoSalvarInformacoesDesabilitado(AjaxSubmitLinkSisAps botaoSalvarInformacoesDesabilitado) {
        this.botaoSalvarInformacoesDesabilitado = botaoSalvarInformacoesDesabilitado;
    }

    public void setBotaoSalvarInformacoes(AjaxSubmitLinkSisAps botaoSalvarInformacoes) {
        this.botaoSalvarInformacoes = botaoSalvarInformacoes;
    }

    public CKEditorTextArea<String> getOutrasDeliberacoes() {
        return outrasDeliberacoes;
    }

    public void setOutrasDeliberacoes(CKEditorTextArea<String> outrasDeliberacoes) {
        this.outrasDeliberacoes = outrasDeliberacoes;
    }

    public boolean isMudanca() {
        AjusteCorec ajusteBase = AjusteCorecMediator.get().buscarPorCiclo(perfilRiscoAtual.getCiclo());
        String outrasBase =
                ajusteBase == null || (ajusteBase != null && ajusteBase.getOutrasDeliberacoes() == null) ? ""
                        : ajusteBase.getOutrasDeliberacoes();
        String outrasModel =
                getOutrasDeliberacoes().getModelObject() == null ? "" : getOutrasDeliberacoes().getModelObject();
        return !outrasBase.equals(outrasModel);
    }

}
