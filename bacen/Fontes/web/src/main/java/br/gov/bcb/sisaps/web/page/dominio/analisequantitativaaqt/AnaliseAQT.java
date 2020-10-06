package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelNotasAvaliacaoAnaliseANEF;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPage;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.INSPETOR})
public class AnaliseAQT extends DefaultPage {
    public static final String ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos";
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR = "idAlertaDadosNaoSalvosInferior";
    private final Form<AvaliacaoRiscoControle> form = new Form<AvaliacaoRiscoControle>("formulario");
    private AnaliseQuantitativaAQT aqt;
    private LinkVoltar linkVoltar;
    private List<FileUpload> filesUpload;
    private String idAlertaAAtualizar;

    private LinkVoltar linkVoltarSemSalvar;
    private PainelResumoElementosAnefs painelResumoElementosAnefs;
    private PainelInformacoesAQT painelInformacoesAQT;
    private PainelEdicaoElementosSupervisorAnef painelEdicaoElementosSupervisorAnef;
    private PainelNotasAvaliacaoAnaliseANEF painelAvaliacao;
    private Label loadScriptAlerta;
    private Label loadScriptAlertaInferior;
    private Label hideScriptAlertaInferior;
    private final List<String> idsAlertas = new ArrayList<String>();

    public AnaliseAQT(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }
    
    public AnaliseAQT(PageParameters parameters) {
        String p1 = parameters.get("pkAnef").toString(null);
        Integer pkAqt = Integer.valueOf(p1);
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(pkAqt);
        this.aqt = aqt;
        if (AnaliseQuantitativaAQTMediator.get().estadosDesignacaoDelegacaoInspetor(aqt)
                || (RegraPerfilAcessoMediator.perfilSupervisor()  && AnaliseQuantitativaAQTMediator.get().estadoPreenchido(aqt.getEstado()))) {
            setPaginaAnterior(new ConsultaCicloPage());
        }  else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        setSubirTelaAoSalvar(false);
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(aqt.getCiclo().getPk());
        LogOperacaoMediator.get().gerarLogDetalhamento(aqt.getCiclo().getEntidadeSupervisionavel(), perfilRiscoAtual,
                atualizarDadosPagina(getPaginaAtual()));
        addOrReplace(form);
        addComponentes();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (!isUsarHtmlUnit()) {
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DefaultPage.class,
                    "ckEditorUtils.js")));
        }
    }

    private void addComponentes() {
        addLoadScriptAlertaInferior();
        hideLoadScriptAlertaInferior();
        final FileUploadField fileUploadFieldArquivo =
                new FileUploadField("idFieldUploadAnexo", new PropertyModel<List<FileUpload>>(this, "filesUpload"));
        form.addOrReplace(fileUploadFieldArquivo);
        linkVoltar = new LinkVoltar();
        form.addOrReplace(linkVoltar);
        linkVoltarSemSalvar = new LinkVoltar("bttVoltarSemSalvar");
        form.addOrReplace(linkVoltarSemSalvar);
        addLoadScriptAlerta();
        addPainelCiclo();
        addPainelInformacao();
        addPainelResumo();
        addPainelAcoes();
        addPainelElementos();
        addPainelAnaliseANEF();
        addPainelAnexos();
        form.addOrReplace(botaoConcluirAnalise());
    }

    private AjaxSubmitLinkSisAps botaoConcluirAnalise() {
        return new AjaxSubmitLinkSisAps("bttConcluir") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                target.add(getLoadScriptAlertaInferior());
                String msg =
                        AnaliseQuantitativaAQTMediator.get().concluirAnaliseANEFSupervisor(aqt, getPerfilPorPagina());
                AnaliseQuantitativaAQT aqtBase = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
                setCriarLinkTrilha(false);
                avancarParaNovaPagina(new DetalharAQT(aqtBase, true, true, msg));
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new AjaxCallListener() {

                    @Override
                    public CharSequence getPrecondition(Component component) {
                        return "return verificaDadosNaoSalvos();";
                    }
                });
            }
        };
    }

    private void addLoadScriptAlerta() {
        loadScriptAlerta = new Label("loadScriptAlerta") {
            @Override
            protected void onAfterRender() {
                super.onAfterRender();
                if (!DefaultPage.isUsarHtmlUnit()) {
                    Response response = getResponse();
                    JavaScriptUtils.writeJavaScript(response, CKEditorUtils.jsVisibilidadeAlertaPrincipal(
                            ID_ALERTA_DADOS_NAO_SALVOS, idAlertaAAtualizar, idsAlertas));
                }
            }
        };
        form.addOrReplace(loadScriptAlerta);

    }

    private void addLoadScriptAlertaInferior() {
        loadScriptAlertaInferior = new Label("loadScriptAlertaInferior") {
            @Override
            protected void onAfterRender() {
                super.onAfterRender();
                if (!DefaultPage.isUsarHtmlUnit()) {
                    Response response = getResponse();
                    JavaScriptUtils.writeJavaScript(response, CKEditorUtils.jsVisibilidadeAlertaPrincipal(
                            ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR, idAlertaAAtualizar, idsAlertas));
                }
            }
        };
        addOrReplace(loadScriptAlertaInferior);

    }

    private void hideLoadScriptAlertaInferior() {
        hideScriptAlertaInferior = new Label("hideScriptAlertaInferior") {
            @Override
            protected void onAfterRender() {
                super.onAfterRender();

                if (!DefaultPage.isUsarHtmlUnit()) {
                    Response response = getResponse();
                    JavaScriptUtils.writeJavaScript(response,
                            CKEditorUtils.jsVisibilidadeAlerta(ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR, false));
                }
            }
        };
        addOrReplace(hideScriptAlertaInferior);

    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        obterIdsAlertas();
    }

    public void atualizarAlertaPrincipal(AjaxRequestTarget target, String idAlertaAAtualizar) {
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(getLoadScriptAlerta());
        target.add(getHideScriptAlertaInferior());
        target.add(painelInformacoesAQT);
    }

    private void obterIdsAlertas() {
        idsAlertas.add(PainelEdicaoAnaliseElementosSupervisorAnef.ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO_ELEMENTO);
        idsAlertas.addAll(painelEdicaoElementosSupervisorAnef.getIdsAlertas());
        idsAlertas.addAll(painelEdicaoElementosSupervisorAnef.getPainelAnalise().getIdsAlertas());
        idsAlertas.addAll(painelAvaliacao.getIdsAlertas());

    }

    private void addPainelAnaliseANEF() {
        painelAvaliacao = new PainelNotasAvaliacaoAnaliseANEF("painelAvaliacaoSupervisorANEF", aqt, idsAlertas);
        painelAvaliacao.setOutputMarkupId(true);
        form.addOrReplace(painelAvaliacao);
    }

    private void addPainelInformacao() {
        painelInformacoesAQT = new PainelInformacoesAQT("idPainelResumoAqt", aqt);
        painelInformacoesAQT.setMarkupId(painelInformacoesAQT.getId());
        painelInformacoesAQT.setOutputMarkupId(true);
        painelInformacoesAQT.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelInformacoesAQT);
    }

    private void addPainelCiclo() {
        PainelResumoCiclo painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", aqt.getCiclo().getPk(), null);
        painelResumoCiclo.setOutputMarkupId(true);
        form.addOrReplace(painelResumoCiclo);
    }

    private void addPainelResumo() {
        painelResumoElementosAnefs = new PainelResumoElementosAnefs("idPainelResumoElementosAnef", aqt, true);
        painelResumoElementosAnefs.setOutputMarkupId(true);
        painelResumoElementosAnefs.setMarkupId(painelResumoElementosAnefs.getId());
        painelResumoElementosAnefs.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelResumoElementosAnefs);
    }

    private void addPainelElementos() {
        painelEdicaoElementosSupervisorAnef = new PainelEdicaoElementosSupervisorAnef("painelElementos", aqt);
        form.addOrReplace(painelEdicaoElementosSupervisorAnef);
    }

    private void addPainelAcoes() {
        PainelAcoesAnef painelAcoesArc = new PainelAcoesAnef("painelAcoesAnef", aqt);
        painelAcoesArc.setOutputMarkupId(true);
        form.addOrReplace(painelAcoesArc);
    }

    private void addPainelAnexos() {
        PainelAnexosAnef painelAcoesArc = new PainelAnexosAnef("painelAnexosAnef", aqt.getCiclo(), aqt, false);
        painelAcoesArc.setOutputMarkupId(true);
        form.addOrReplace(painelAcoesArc);
    }

    public void atualizarNovaNotaAnef(AjaxRequestTarget target) {
        target.add(painelResumoElementosAnefs);
        target.add(painelInformacoesAQT);
        target.add(painelAvaliacao);
    }

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0213";
    }

    @Override
    public String getTitulo() {
        return "Análise do ANEF";
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public List<FileUpload> getFilesUpload() {
        return filesUpload;
    }

    public void setFilesUpload(List<FileUpload> filesUpload) {
        this.filesUpload = filesUpload;
    }

    public Label getLoadScriptAlertaInferior() {
        return loadScriptAlertaInferior;
    }

    public Label getLoadScriptAlerta() {
        return loadScriptAlerta;
    }

    public Label getHideScriptAlertaInferior() {
        return hideScriptAlertaInferior;
    }

}
