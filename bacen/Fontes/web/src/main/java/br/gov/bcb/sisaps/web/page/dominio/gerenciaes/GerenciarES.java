package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.painelGerente.PerfilGerentePage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.CONSULTA_TUDO,
        PerfisAcesso.GERENTE})
public class GerenciarES extends DefaultPage {

    private static final String BTT_VOLTAR_SEM_SALVAR = "bttVoltarSemSalvar";
    private Integer ciclo;
    private PerfilRisco perfilRisco;
    private final Form<Ciclo> form = new Form<Ciclo>("formulario");
    @SuppressWarnings("rawtypes")
    private Link linkVoltar;
    @SuppressWarnings("rawtypes")
    private Link linkVoltarSemSalvar;
    private PainelPerspectivaSituacao painelSituacao;
    private PainelPerspectivaSituacao painelPespectiva;
    private Label loadScriptAlerta;
    private String idAlertaAAtualizar;
    private final List<String> idsAlertas = new ArrayList<String>();
    private PainelPerfilAtuacaoConclusao painelPerfilAtuacao;
    private PainelPerfilAtuacaoConclusao painelConclusao;
    private PainelNotaFinalEdicao painelNotaFinalEdicao;
    private PainelNotaFinalVigente painelNotaFinalVigente;

    public GerenciarES(Integer ciclo, PerfilRisco perfilRisco) {
        this.ciclo = ciclo;
        this.perfilRisco = perfilRisco;
        setSubirTelaAoSalvar(false);
    }

    public GerenciarES(PageParameters parameters) {
        String p1 = parameters.get("pkCiclo").toString(null);
        if (p1 != null) {
            Integer pkCiclo = Integer.valueOf(p1);
            this.perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(pkCiclo);
            this.ciclo = pkCiclo;
            setSubirTelaAoSalvar(false);
            if (RegraPerfilAcessoMediator.perfilGerente()) {
                setPaginaAnterior(new PerfilGerentePage());
            } else {
                throw new UnauthorizedActionException(this, ENABLE);
            }
        }
        
        
    }

    public GerenciarES(Integer ciclo, PerfilRisco perfilRisco, String sucess) {
        success(sucess);
        this.ciclo = ciclo;
        this.perfilRisco = perfilRisco;
        setSubirTelaAoSalvar(false);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addLoadScriptAlerta();
        perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo);
        addPaineis();
        addBotoes();
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
        addOrReplace(loadScriptAlerta);
    }

    private void addPaineis() {
        addPainelResumoCiclo();
        addPainelGrauPreocupacao();
        addPainelPerfilAtuacao();
        addPainelConclusao();
        addPainelPerspectiva();
        addPainelSituacao();
    }

    private void addPainelResumoCiclo() {
        PainelResumoCiclo painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo, perfilRisco);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        form.addOrReplace(painelResumoCiclo);
    }

    private void addPainelGrauPreocupacao() {
        
        // Painel usada somente para metodologia antiga.
        PainelGrauPreocupacao painelGrauPreocupacao =
                new PainelGrauPreocupacao("idPainelGrauPreocupacao", ciclo, perfilRisco);
        form.addOrReplace(painelGrauPreocupacao);
        
        painelNotaFinalVigente =
                new PainelNotaFinalVigente("idPainelNotaFinalVigente", ciclo, perfilRisco);
        painelNotaFinalEdicao =
                new PainelNotaFinalEdicao("idPainelNotaFinalEdicao", ciclo, perfilRisco);
        form.addOrReplace(painelNotaFinalVigente);
        form.addOrReplace(painelNotaFinalEdicao);
    }

    private void addPainelPerfilAtuacao() {
        painelPerfilAtuacao = new PainelPerfilAtuacaoConclusao("idPainelPerfilAtuacao", ciclo, true, perfilRisco);
        form.addOrReplace(painelPerfilAtuacao);
    }

    private void addPainelConclusao() {
        painelConclusao = new PainelPerfilAtuacaoConclusao("idPainelConclusao", ciclo, false, perfilRisco);
        form.addOrReplace(painelConclusao);
    }

    private void addPainelPerspectiva() {
        painelPespectiva = new PainelPerspectivaSituacao("idPainelPerspectiva", ciclo, true, perfilRisco);
        form.addOrReplace(painelPespectiva);
    }

    private void addPainelSituacao() {
        painelSituacao = new PainelPerspectivaSituacao("idPainelSituacao", ciclo, false, perfilRisco);
        form.addOrReplace(painelSituacao);
    }

    private void addBotoes() {
        if (getPaginaAnterior() instanceof PerfilGerentePage) {
            linkVoltar = new LinkVoltar();
            linkVoltarSemSalvar = new LinkVoltar(BTT_VOLTAR_SEM_SALVAR);
        } else {
            linkVoltar = new LinkVoltarPerfilRisco(ciclo);
            linkVoltarSemSalvar = new LinkVoltarPerfilRisco(BTT_VOLTAR_SEM_SALVAR, ciclo);
        }
        form.addOrReplace(linkVoltar);
        form.addOrReplace(linkVoltarSemSalvar);
    }

    @Override
    public String getTitulo() {
        return "Gestão de detalhes da ES";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0204";
    }

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    public void atualizarBotoesAlerta(AjaxRequestTarget target, String idAlertaAAtualizar) {
        obterIdsAlertas();
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(loadScriptAlerta);
    }

    private void obterIdsAlertas() {
        idsAlertas.add(painelPerfilAtuacao.getMarkupIdAlerta());
        idsAlertas.add(painelConclusao.getMarkupIdAlerta());
        idsAlertas.add(painelPespectiva.getMarkupIdAlerta());
        idsAlertas.add(painelSituacao.getMarkupIdAlerta());
    }
    
    public void atualizarPainelNotaFinal(AjaxRequestTarget target) {
        target.add(painelNotaFinalEdicao);
}
    
}
