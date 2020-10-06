package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.PainelEdicaoPercentualAnef;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelSinteseAQTPerfilRisco;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.painel.PainelListaSintesesAQT;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class GerenciarNotaSinteseAQTPage extends DefaultPage {

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    @SpringBean
    private MetodologiaMediator metodologiaMediator;

    @SpringBean
    private LogOperacaoMediator logOperacaoMediator;

    private PerfilRisco perfilRiscoAtual;

    private Label loadScriptAlerta;

    @SuppressWarnings("rawtypes")
    private Link linkVoltar;

    @SuppressWarnings("rawtypes")
    private Link linkVoltarSemSalvar;

    private final List<String> idsAlertas = new ArrayList<String>();

    private String idAlertaAAtualizar;

    private PainelListaSintesesAQT painelGerenciarSinteses;

    private PainelNovoQuadroAQT painelNovoQuadroAQT;

    private PainelSinteseAQTPerfilRisco painelSinteseAQTVigente;

    private PainelAnefAQT painelAnefAQT;

    private PainelEdicaoPercentualAnef painelEdicaoPercentualAnef;

    private final String idFocusSintense;

    public GerenciarNotaSinteseAQTPage(PerfilRisco perfilRiscoAtual) {
        this(perfilRiscoAtual, null);
    }

    public GerenciarNotaSinteseAQTPage(PerfilRisco perfilRiscoAtual, String idFocusSintense) {
        this.idFocusSintense = idFocusSintense;
        setSubirTelaAoSalvar(false);
        this.perfilRiscoAtual = perfilRiscoAtual;
    }
    
    public GerenciarNotaSinteseAQTPage(PageParameters parameters) {
        String p1 = parameters.get("pkAnef").toString(null);
        Integer pkAnef = Integer.valueOf(p1);
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(pkAnef);
        this.perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(anef.getCiclo().getPk());
        this.idFocusSintense = obterNomePainelFocus(anef);
        setSubirTelaAoSalvar(false);
    }
    
    private String obterNomePainelFocus(AnaliseQuantitativaAQT anef) {
        return "idTituloSintese" + SisapsUtil.criarMarkupId(anef.getParametroAQT().getDescricao());
    }

    
    @Override
    protected void onRender() {
        super.onRender();
        if (!Util.isNuloOuVazio(idFocusSintense)) {
            Response response = getResponse();
            JavaScriptUtils.writeJavaScript(response, CKEditorUtils.setarFocus(idFocusSintense));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addLoadScriptAlerta();

        setSubirTelaAoSalvar(false);
        Form<?> form = new Form<Object>("formulario");
        FileUploadField fileUploadFieldArquivo = new FileUploadField("idFieldUploadAnexo");
        form.addOrReplace(fileUploadFieldArquivo);
        form.addOrReplace(new PainelResumoCiclo("resumoCicloPanel", perfilRiscoAtual.getCiclo().getPk(),
                perfilRiscoAtual));

        addPainelSinteseAQT(form);

        Metodologia metodologia =
                metodologiaMediator.buscarMetodologiaPorPK(perfilRiscoAtual.getCiclo().getMetodologia().getPk());

        painelGerenciarSinteses = new PainelListaSintesesAQT("gerenciarSintesesPanel", perfilRiscoAtual, metodologia);
        painelGerenciarSinteses.setOutputMarkupId(true);
        form.addOrReplace(painelGerenciarSinteses);
        linkVoltar = new LinkVoltarPerfilRisco(perfilRiscoAtual.getCiclo().getPk());
        form.add(linkVoltar);
        linkVoltarSemSalvar = new LinkVoltarPerfilRisco("bttVoltarSemSalvar", perfilRiscoAtual.getCiclo().getPk());
        form.add(linkVoltarSemSalvar);

        form.addOrReplace(new PainelGerenciarNotaAQT("gerenciarNotaPanel", perfilRiscoAtual.getCiclo(), metodologia));

        painelEdicaoPercentualAnef = new PainelEdicaoPercentualAnef("idPainelPercentual", perfilRiscoAtual.getCiclo());
        painelEdicaoPercentualAnef.setOutputMarkupId(true);
        form.addOrReplace(painelEdicaoPercentualAnef);

        logOperacaoMediator.gerarLogDetalhamento(perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel(),
                perfilRiscoAtual, atualizarDadosPagina(getPaginaAtual()));

        painelAnefAQT = new PainelAnefAQT("painelAnefsResp", perfilRiscoAtual.getCiclo());
        form.addOrReplace(painelAnefAQT);

        addOrReplace(form);
    }

    private void addPainelSinteseAQT(Form<?> form) {
        painelNovoQuadroAQT = new PainelNovoQuadroAQT("painelNovoQuadroAQT", perfilRiscoAtual) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
            }
        };
        painelNovoQuadroAQT.setOutputMarkupPlaceholderTag(true);
        painelNovoQuadroAQT.setOutputMarkupId(true);
        painelNovoQuadroAQT.setMarkupId(painelNovoQuadroAQT.getId());

        form.addOrReplace(painelNovoQuadroAQT);

        painelSinteseAQTVigente =
                new PainelSinteseAQTPerfilRisco("painelQuadroVigenteAQT", perfilRiscoAtual, getPerfilPorPagina(),
                        "Quadro vigente", false, true, false) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!isPerfilRiscoCicloMigrado());
                    }
                };

        painelSinteseAQTVigente.setOutputMarkupPlaceholderTag(true);
        painelSinteseAQTVigente.setOutputMarkupId(true);
        painelSinteseAQTVigente.setMarkupId(painelSinteseAQTVigente.getId());

        form.addOrReplace(painelSinteseAQTVigente);

    }

    private void addLoadScriptAlerta() {
        loadScriptAlerta = new Label("loadScriptAlerta") {
            @Override
            protected void onAfterRender() {
                super.onAfterRender();
                if (!DefaultPage.isUsarHtmlUnit()) {
                    Response response = getResponse();
                    JavaScriptUtils.writeJavaScript(
                            response,
                            CKEditorUtils.jsVisibilidadeBotaoVoltarPrincipal(linkVoltar.getId(),
                                    linkVoltarSemSalvar.getId(), idAlertaAAtualizar, idsAlertas));
                }
            }
        };
        addOrReplace(loadScriptAlerta);
    }

    private void obterIdsAlertas() {
        idsAlertas.addAll(painelGerenciarSinteses.getIdsAlertas());
    }

    @Override
    public String getTitulo() {
        return "Gestão da Análise Econômico-Financeira";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0211";
    }

    public void atualizarBotoesAlerta(AjaxRequestTarget target, String idAlertaAAtualizar) {
        obterIdsAlertas();
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(loadScriptAlerta);
    }

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    public void atualizarPerfilRiscoAtual() {
        this.perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(perfilRiscoAtual.getCiclo().getPk());
    }

    private boolean isPerfilRiscoCicloMigrado() {
        return SisapsUtil.isCicloMigrado(perfilRiscoAtual.getCiclo());
    }

    public void atualizarPainelNovoQuadroAQT(AjaxRequestTarget target) {
        painelNovoQuadroAQT.setPerfilRisco(perfilRiscoAtual);
        target.add(painelNovoQuadroAQT);
    }

    public void atualizaPainelQuadroVigenteAQT(AjaxRequestTarget target) {
        painelSinteseAQTVigente.setPerfilRisco(perfilRiscoAtual);
        target.add(painelSinteseAQTVigente);
    }

    public void atualizaPainelGerenciarSinteses(AjaxRequestTarget target) {
        painelGerenciarSinteses.setPerfilRisco(perfilRiscoAtual);
        target.add(painelGerenciarSinteses);
    }
    
    public void atualizaPainelListaAnefs(AjaxRequestTarget target) {
        target.add(painelAnefAQT);
    }

    public PainelEdicaoPercentualAnef getPainelEdicaoPercentualAnef() {
        return painelEdicaoPercentualAnef;
    }

    public void setPainelEdicaoPercentualAnef(PainelEdicaoPercentualAnef painelEdicaoPercentualAnef) {
        this.painelEdicaoPercentualAnef = painelEdicaoPercentualAnef;
    }

    public PainelListaSintesesAQT getPainelGerenciarSinteses() {
        return painelGerenciarSinteses;
    }

    public void setPainelGerenciarSinteses(PainelListaSintesesAQT painelGerenciarSinteses) {
        this.painelGerenciarSinteses = painelGerenciarSinteses;
    }

    public String getNotaCalculadaFinal() {
        return painelSinteseAQTVigente.getNotaCalculadaFinal();
}
}
