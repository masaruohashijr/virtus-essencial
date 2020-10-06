/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.request.Response;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.botoes.BotaoConfirmacao;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel.PainelLinkApresentacao;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.COMITE})
public class GestaoCorecPage extends DefaultPage {
    private static final String VISIBILITY_HIDDEN = "visibility: hidden;";
    private static final String VISIBILITY_VISIBLE = "visibility: visible;";
    private static final String STYLE = "style";
    private static final String BTT_VOLTAR_SEM_SALVAR = "bttVoltarSemSalvar";
    private PainelResumoCiclo painelResumoCiclo;
    private PainelAjusteArcCorec painelAjusteCorec;
    private final Ciclo ciclo;
    private PerfilRisco perfilRiscoAtual;
    private PainelAjusteANEFCorec painelAjusteANEFCorec;
    private PainelOutrasDeliberacoes painelOutrasDeliberacoes;
    private final List<String> idsAlertas = new ArrayList<String>();
    private PainelAjusteCorec painelAjuste;
    private BotaoConfirmacao botaoEncerrarCorec;
    private PainelParticipantesComite painelParticipantesComite;
    private LinkVoltar linkVoltar;
    private LinkVoltar linkVoltarSemSalvar;
    private Label loadScriptAlerta;
    private String idAlertaAAtualizar;

    public GestaoCorecPage(CicloVO cicloVO) {
        this.ciclo = CicloMediator.get().buscarCicloPorPK(cicloVO.getPk());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        final Form<?> form = new Form<Object>("formulario");

        FileUploadField fileUploadFieldArquivo = new FileUploadField("idFieldUploadAnexo");
        form.addOrReplace(fileUploadFieldArquivo);

        perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        addLoadScriptAlerta();
        addPainelResumoCiclo(form);
        addPainelAjuste(form);
        addPainelAjusteArc(form);
        addPainelAjusteAnef(form);
        addPainelOutrasDeliberacoes(form);
        addPainelParticipantesComite(form);
        addBotaoAlterarStatusCiclo(form);
        addPainelApresentacao(form);
        addBotaoEncerrarCorec(form);
        addBotaoVoltar(form);
        add(form);
    }

    private void addLoadScriptAlerta() {
        loadScriptAlerta = new Label("loadScriptAlerta") {
            @Override
            protected void onAfterRender() {
                super.onAfterRender();
                if (!DefaultPage.isUsarHtmlUnit()) {
                    Response response = getResponse();
                    JavaScriptUtils.writeJavaScript(response, CKEditorUtils.jsVisibilidadeBotaoVoltarPrincipal(
                            linkVoltar.getId(), linkVoltarSemSalvar.getId(), idAlertaAAtualizar, idsAlertas),
                            CKEditorUtils.jsVisibilidadeBotaoVoltarPrincipal(painelOutrasDeliberacoes
                                    .getBotaoSalvarInformacoes().getId(), painelOutrasDeliberacoes
                                    .getBotaoSalvarInformacoesDesabilitado().getId(), idAlertaAAtualizar, idsAlertas));
                }
            }
        };
        add(loadScriptAlerta);
    }

    private void addPainelParticipantesComite(Form<?> form) {
        painelParticipantesComite = new PainelParticipantesComite("participantesComitePanel", ciclo) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!AnexoPosCorecMediator.get().isPossivelAnexarAta(ciclo));
            }
        };
        form.add(painelParticipantesComite);
    }

    private void addPainelAjuste(Form<?> form) {
        painelAjuste = new PainelAjusteCorec("ajusteCorecPanel", ciclo);
        painelAjuste.setMarkupId(painelAjuste.getId());
        form.addOrReplace(painelAjuste);
    }

    private void addBotaoAlterarStatusCiclo(Form<?> form) {
        AjaxSubmitLinkSisAps botaoAlterarStatusCiclo = botaoAlterarStatusCiclo();
        form.addOrReplace(botaoAlterarStatusCiclo);
    }

    private void addBotaoEncerrarCorec(Form<?> form) {
        botaoEncerrarCorec = new BotaoConfirmacao("bttEncerrarCorec", "Deseja realmente concluir o Corec?", true) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                String mensagemSucesso = EncerrarCorecMediator.get().agendarEncerramentoCorec(ciclo);
                getPaginaAnterior().success(mensagemSucesso);
                setResponsePage(getPaginaAnterior());
            }
        };
        form.addOrReplace(botaoEncerrarCorec);
    }

    private AjaxSubmitLinkSisAps botaoAlterarStatusCiclo() {
        return new AjaxSubmitLinkSisAps("bttAlterarStatusCiclo") {

            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                CicloMediator.get().alterarStatusCiclo(ciclo);
                setResponsePage(getPaginaAnterior());
            }

        };
    }

    private void addPainelApresentacao(Form<?> form) {
        PainelLinkApresentacao painelLinkApresentacao =
                new PainelLinkApresentacao("idPainelApresentacao", perfilRiscoAtual) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                    }
                };
        painelLinkApresentacao.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelLinkApresentacao);
    }

    private void addPainelResumoCiclo(Form<?> form) {
        painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo.getPk(), perfilRiscoAtual);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        form.addOrReplace(painelResumoCiclo);
    }

    private void addPainelAjusteArc(Form<?> form) {
        painelAjusteCorec = new PainelAjusteArcCorec("ajusteArcPanel", perfilRiscoAtual);
        painelAjusteCorec.setMarkupId(painelAjusteCorec.getId());
        form.addOrReplace(painelAjusteCorec);
    }

    private void addPainelAjusteAnef(Form<?> form) {
        painelAjusteANEFCorec = new PainelAjusteANEFCorec("ajusteAnefPanel", perfilRiscoAtual);
        painelAjusteANEFCorec.setMarkupId(painelAjusteANEFCorec.getId());
        form.addOrReplace(painelAjusteANEFCorec);
    }

    private void addPainelOutrasDeliberacoes(Form<?> form) {
        painelOutrasDeliberacoes = new PainelOutrasDeliberacoes("outrasDeliberacoesPanel", perfilRiscoAtual);
        painelOutrasDeliberacoes.setMarkupId(painelOutrasDeliberacoes.getId());
        form.addOrReplace(painelOutrasDeliberacoes);
    }

    private void addBotaoVoltar(Form<?> form) {

        linkVoltar = new LinkVoltar();
        linkVoltarSemSalvar = new LinkVoltar(BTT_VOLTAR_SEM_SALVAR);

        form.addOrReplace(linkVoltar);
        form.addOrReplace(linkVoltarSemSalvar);
    }

    public void atualizarBotaoEncerrarCorec(AjaxRequestTarget target) {
        botaoEncerrarCorec.setEnabled(isBotaoEncerrarHabilitado());
        target.add(botaoEncerrarCorec);
        target.add(linkVoltar);
        target.add(linkVoltarSemSalvar);
    }

    private boolean isBotaoEncerrarHabilitado() {
        return botoesSalvarHabilitados() && !painelParticipantesComite.getBotaoSalvarInformacoes().isEnabled();
    }

    private boolean botoesSalvarHabilitados() {
        return !painelAjuste.getBotaoSalvarInformacoes().isEnabled()
                && !painelAjusteCorec.getBotaoSalvarInformacoes().isEnabled()
                && !painelAjusteANEFCorec.getBotaoSalvarInformacoes().isEnabled()
                && !painelOutrasDeliberacoes.isMudanca();
    }

    public void atualizarBotaoEAlertaPainelParticipantesVisiveis() {
        painelParticipantesComite.setarBotaoEAlertaVisiveis();
    }

    public void atualizarTabelasParticipantes(AjaxRequestTarget target) {
        painelParticipantesComite.atualizarTabelasParticipantes(target);
        atualizarBotaoEncerrarCorec(target);
    }

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    public String jsAtualizarBotoesSalvar(boolean mostrarBotaoSalvar) {
        return CKEditorUtils.jsAtualizarBotoesSalvarInformacoes(painelOutrasDeliberacoes.getBotaoSalvarInformacoes()
                .getId(), painelOutrasDeliberacoes.getBotaoSalvarInformacoesDesabilitado().getId(), mostrarBotaoSalvar);
    }

    public void exibirBotaoVoltarSemSalvar() {
        linkVoltarSemSalvar.add(new AttributeAppender(STYLE, VISIBILITY_VISIBLE));
        linkVoltar.add(new AttributeAppender(STYLE, VISIBILITY_HIDDEN));
    }

    public void exibirBotaoVoltar() {
        linkVoltar.add(new AttributeAppender(STYLE, VISIBILITY_VISIBLE));
        linkVoltarSemSalvar.add(new AttributeAppender(STYLE, VISIBILITY_HIDDEN));
    }

    public void exibirBotaoVoltarAoSalvar() {
        if (botoesSalvarHabilitados()) {
            exibirBotaoVoltar();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Gestão Corec";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0602";
    }
}
