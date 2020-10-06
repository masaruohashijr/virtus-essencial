package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPage;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel.PainelGerenciarNota;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel.PainelListaSinteses;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class GerenciarNotaSintesePage extends DefaultPage {

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    @SpringBean
    private MetodologiaMediator metodologiaMediator;

    private PerfilRisco perfilRiscoAtual;

    private Label loadScriptAlerta;

    private LinkVoltarPerfilRisco linkVoltar;

    private LinkVoltarPerfilRisco linkVoltarSemSalvar;

    private final List<String> idsAlertas = new ArrayList<String>();

    private String idAlertaAAtualizar;

    private PainelListaSinteses painelGerenciarSinteses;

    private PainelDadosMatrizPercentual painelDadosMatrizVigente;

    private PainelDadosMatrizPercentual painelDadosMatrizEmAnalise;

    private final String idFocusSintense;

    public GerenciarNotaSintesePage(PerfilRisco perfilRiscoAtual) {
        this(perfilRiscoAtual, null);
    }
        
    public GerenciarNotaSintesePage(PerfilRisco perfilRiscoAtual, String idFocusSintense) {
        setSubirTelaAoSalvar(false);
        this.perfilRiscoAtual = perfilRiscoAtual;
        this.idFocusSintense = idFocusSintense;
    }
    
    public GerenciarNotaSintesePage(PageParameters parameters) {
        setSubirTelaAoSalvar(false);
        String p1 = parameters.get("pkEntidade").toString(null);
        String p2 = parameters.get("pkParametro").toString(null);
        Integer pkEntidadeSupervisionavel = Integer.valueOf(p1);
        Integer pkParametroGrupo = Integer.valueOf(p2);
        
        Ciclo ciclo =
                CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(pkEntidadeSupervisionavel);
        
        ParametroGrupoRiscoControle parametro =
                ParametroGrupoRiscoControleMediator.get().buscarParametroGrupoRisco(pkParametroGrupo);

        this.perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        this.idFocusSintense = parametro.getNomeAbreviado();
        setPaginaAnterior(new ConsultaCicloPage());
    }

    @Override
    protected void onRender() {
        super.onRender();
        if (!Util.isNuloOuVazio(idFocusSintense)) {
            Response response = getResponse();
            JavaScriptUtils.writeJavaScript(response,
                    CKEditorUtils.setarFocus("idTituloSintese" + SisapsUtil.criarMarkupId(idFocusSintense)));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addLoadScriptAlerta();

        Form<?> form = new Form<Object>("formulario");
        FileUploadField fileUploadFieldArquivo = new FileUploadField("idFieldUploadAnexo");
        form.addOrReplace(fileUploadFieldArquivo);
        form.addOrReplace(new PainelResumoCiclo("resumoCicloPanel", perfilRiscoAtual.getCiclo().getPk(),
                perfilRiscoAtual));
        painelDadosMatrizVigente =
                new PainelDadosMatrizPercentual("matrizVigentePanel", perfilRiscoAtual, "Matriz vigente", true, true,
                        true);
        painelDadosMatrizVigente.setMarkupId(painelDadosMatrizVigente.getId());
        form.addOrReplace(painelDadosMatrizVigente);
        painelDadosMatrizEmAnalise =
                new PainelDadosMatrizPercentual("matrizVigenteRecalculadaPanel", perfilRiscoAtual,
                        "Matriz recalculada (ARCs analisados)", true, true, false, painelDadosMatrizVigente);
        painelDadosMatrizEmAnalise.setMarkupId(painelDadosMatrizEmAnalise.getId());
        form.addOrReplace(painelDadosMatrizEmAnalise);
        Matriz matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRiscoAtual);

        Metodologia metodologia =
                metodologiaMediator.buscarMetodologiaPorPK(perfilRiscoAtual.getCiclo().getMetodologia().getPk());

        painelGerenciarSinteses =
                new PainelListaSinteses("gerenciarSintesesPanel", perfilRiscoAtual, metodologia, matriz);
        painelGerenciarSinteses.setOutputMarkupId(true);
        form.addOrReplace(painelGerenciarSinteses);
        linkVoltar = new LinkVoltarPerfilRisco(perfilRiscoAtual.getCiclo().getPk());
        form.add(linkVoltar);
        linkVoltarSemSalvar = new LinkVoltarPerfilRisco("bttVoltarSemSalvar", perfilRiscoAtual.getCiclo().getPk());
        form.add(linkVoltarSemSalvar);
        LogOperacaoMediator.get().gerarLogDetalhamento(perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel(),
                perfilRiscoAtual, atualizarDadosPagina(getPaginaAtual()));
        addOrReplace(form);
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
        add(loadScriptAlerta);
    }

    private void obterIdsAlertas() {
        idsAlertas.add(PainelGerenciarNota.ID_ALERTA_DADOS_NAO_SALVOS_NOTA_MATRIZ);
        idsAlertas.addAll(painelGerenciarSinteses.getIdsAlertas());
    }

    @Override
    public String getTitulo() {
        return "Gestão de Sínteses";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0209";
    }

    public void atualizarBotoesAlerta(AjaxRequestTarget target, String idAlertaAAtualizar) {
        obterIdsAlertas();
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(loadScriptAlerta);
    }

    public void atualizarPainelMatrizVigente(AjaxRequestTarget target) {
        painelDadosMatrizVigente.setPerfilRisco(perfilRiscoAtual);
        target.add(painelDadosMatrizVigente);
    }

    public void atualizarPainelMatrizEmAnalise(AjaxRequestTarget target) {
        painelDadosMatrizEmAnalise.setPerfilRisco(perfilRiscoAtual);
        target.add(painelDadosMatrizEmAnalise);
    }

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    public void atualizarPerfilRiscoAtual() {
        this.perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRiscoAtual.getCiclo().getPk());
    }

}
