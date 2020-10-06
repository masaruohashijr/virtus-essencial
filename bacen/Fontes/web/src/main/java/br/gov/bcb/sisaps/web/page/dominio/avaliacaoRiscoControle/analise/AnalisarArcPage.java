package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise;

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
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.DetalharArcAnalisado;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelAcoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe.PainelTendenciaRiscoMercadoAnalise;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelAnexoArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelTendencia;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.INSPETOR})
public class AnalisarArcPage extends DefaultPage {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos";
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR = "idAlertaDadosNaoSalvosInferior";
    private static final String NULL = "null";
    private static final String SPACE = "";
    private AvaliacaoRiscoControle avaliacao;

    @SpringBean
    private MatrizCicloMediator matrizCicloMediator;

    private Matriz matriz;
    private final int pkAvaliacao;
    private final int pkMatriz;
    private final Integer pkAtividade;

    private PainelResumoElementosArcs painelResumo;
    private String idAlertaAAtualizar;
    private Label loadScriptAlerta;
    private Label loadScriptAlertaInferior;
    private Label hideScriptAlertaInferior;
    private final List<String> idsAlertas = new ArrayList<String>();

    private PainelRiscoDeMercado painelMercado;
    private final Form<?> form = new Form<Object>("formulario");

    private PainelInformacoesArc painelInformacoesArc;

    private PainelTendenciaRiscoMercadoAnalise painelTendencia;
    private PainelEdicaoElementosSupervisor painelEdicaoElementos;

    public AnalisarArcPage(AvaliacaoRiscoControle avaliacao, Matriz matriz, Atividade atividade) {
        this(avaliacao.getPk(), matriz.getPk(), atividade == null ? null : atividade.getPk());
    }

    public AnalisarArcPage(int pkAvaliacao, int pkMatriz, Integer pkAtividade) {
        this.pkAvaliacao = pkAvaliacao;
        this.pkAtividade = pkAtividade;
        this.pkMatriz = pkMatriz;
    }

    public AnalisarArcPage(PageParameters parameters) {
        String p1 = parameters.get("pkArc").toString(null);
        String p2 = parameters.get("pkMatriz").toString(null);
        String p3 = parameters.get("pkAtividade").toString(null);
        this.pkAvaliacao = Integer.valueOf(p1);
        this.pkMatriz = Integer.valueOf(p2);
        if (p3.equals(NULL) || p3.equals(SPACE)) {
            this.pkAtividade = null;
        } else {
            this.pkAtividade = Integer.valueOf(p3);
        }
        
        ArcNotasVO arc = AvaliacaoRiscoControleMediator.get().consultarNotasArc(pkAvaliacao);
        if(AvaliacaoRiscoControleMediator.get().isResponsavelARC(arc, ((UsuarioAplicacao) UsuarioCorrente.get())) 
                || (RegraPerfilAcessoMediator.perfilSupervisor() && AvaliacaoRiscoControleMediator.get().estadoPreenchido(arc.getEstado()))){
            setPaginaAnterior(new ConsultaCicloPage());
        } else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
    }
    
    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        obterIdsAlertas();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (!isUsarHtmlUnit()) {
            response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(DefaultPage.class,
                    "ckEditorUtils.js")));
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addLoadScriptAlertaInferior();
        hideLoadScriptAlertaInferior();
        setSubirTelaAoSalvar(false);
        addLoadScriptAlerta();
        ParametroGrupoRiscoControle parametroGrupoRiscoControle = null;
        this.avaliacao = AvaliacaoRiscoControleMediator.get().loadPK(pkAvaliacao);
        Atividade atividade = pkAtividade == null ? null : AtividadeMediator.get().loadPK(pkAtividade);
        if (atividade == null) {
            parametroGrupoRiscoControle = 
                    AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(avaliacao.getPk())
                    .getParametroGrupoRiscoControle();
        } else {
            parametroGrupoRiscoControle = 
                    CelulaRiscoControleMediator.get().buscarCelularPorAvaliacaoEAtividade(atividade, avaliacao)
                    .getParametroGrupoRiscoControle();
        }
        this.matriz = matrizCicloMediator.loadPK(pkMatriz);
        LogOperacaoMediator.get().gerarLogDetalhamento(matriz.getCiclo().getEntidadeSupervisionavel(),
                PerfilRiscoMediator.get().obterPerfilRiscoAtual(matriz.getCiclo().getPk()), 
                atualizarDadosPagina(getPaginaAtual()));

        form.addOrReplace(new PainelResumoCiclo("painelCiclo", matriz.getCiclo().getPk()));

        addPainelInformacoesArc(parametroGrupoRiscoControle, atividade);

        addPainelResumo();
        addPainelAcoes(atividade);

        painelMercado =
                new PainelRiscoDeMercado("painelRiscoMercado", parametroGrupoRiscoControle, avaliacao, matriz.getCiclo(),
                        idsAlertas);
        painelMercado.setOutputMarkupId(true);
        painelMercado.setMarkupId(painelMercado.getId());
        painelMercado.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelMercado);

        painelEdicaoElementos = new PainelEdicaoElementosSupervisor("painelElementos", avaliacao, matriz.getCiclo());
        form.addOrReplace(painelEdicaoElementos);
        addPainelTendencia(parametroGrupoRiscoControle);

        form.addOrReplace(new PainelAnexoArc("idPainelAnexoArc", matriz.getCiclo(), avaliacao, false));
        addBotoes();
        addOrReplace(form);
    }

    private void addPainelInformacoesArc(ParametroGrupoRiscoControle parametroGrupoRiscoControle, Atividade atividade) {
        painelInformacoesArc =
                new PainelInformacoesArc("painelInformacoesArc", avaliacao, matriz.getCiclo(), atividade,
                        parametroGrupoRiscoControle);
        painelInformacoesArc.setOutputMarkupId(true);
        painelInformacoesArc.setMarkupId(painelInformacoesArc.getId());
        form.addOrReplace(painelInformacoesArc);
    }

    private void addBotoes() {
        form.addOrReplace(botaoConcluirAnalise());
        form.addOrReplace(new LinkVoltar());
    }

    private void addPainelTendencia(ParametroGrupoRiscoControle parametroGrupoRiscoControle) {
        painelTendencia =
                new PainelTendenciaRiscoMercadoAnalise("painelTendencia", parametroGrupoRiscoControle, avaliacao,
                        matriz.getCiclo(), false, false, false, idsAlertas);
        painelTendencia.setMarkupId(painelTendencia.getId());
        painelTendencia.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelTendencia);
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

    private void addPainelResumo() {
        painelResumo = new PainelResumoElementosArcs("painelResumoElementosArc", avaliacao, matriz, avaliacao, true);
        painelResumo.setOutputMarkupId(true);
        painelResumo.setMarkupId(painelResumo.getId());
        painelResumo.setOutputMarkupPlaceholderTag(true);
        form.addOrReplace(painelResumo);
    }

    private void addPainelAcoes(Atividade atividade) {
        PainelAcoesArc painelAcoesArc = new PainelAcoesArc("painelAcoesArc", avaliacao, atividade, matriz, true);
        painelAcoesArc.setOutputMarkupId(true);
        form.addOrReplace(painelAcoesArc);
    }

    private AjaxSubmitLinkSisAps botaoConcluirAnalise() {
        return new AjaxSubmitLinkSisAps("bttConcluir") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                AvaliacaoRiscoControleMediator.get().concluirAnaliseARCSupervisor(matriz.getCiclo(), avaliacao.getPk());
                AvaliacaoRiscoControle avaliacaoBase = AvaliacaoRiscoControleMediator.get().buscar(avaliacao.getPk());
                setCriarLinkTrilha(false);
                avancarParaNovaPagina(new DetalharArcAnalisado(avaliacaoBase, matriz, pkAtividade, true, true));
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

    public void atualizarNovaNotaArc(AjaxRequestTarget target) {
        painelInformacoesArc.setAvaliacao(avaliacao);
        target.add(painelInformacoesArc.getLblEstado());
        target.add(painelInformacoesArc.getLabelUltimaAtualizacao());
        painelResumo.atualizarNotaCalculada(target);
        painelMercado.setAvaliacao(avaliacao);
        target.add(painelMercado.getLabelNotaCalculada());
        target.add(painelMercado);

        target.add(painelResumo);
        target.add(painelTendencia);
    }

    public void atualizarPainelResumo(AjaxRequestTarget target) {
        painelInformacoesArc.setAvaliacao(avaliacao);
        target.add(painelInformacoesArc.getLblEstado());
        target.add(painelInformacoesArc.getLabelUltimaAtualizacao());
        painelMercado.setAvaliacao(avaliacao);
        painelResumo.atualizarNotaCalculada(target);
        target.add(painelResumo);
    }

    public void atualizarAlertaPrincipal(AjaxRequestTarget target, String idAlertaAAtualizar) {
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(getLoadScriptAlerta());
        target.add(getHideScriptAlertaInferior());
        target.add(painelInformacoesArc.getLabelUltimaAtualizacao());
    }

    public void atualizarTendencia(AjaxRequestTarget target) {
        target.add(painelTendencia);
        target.add(painelInformacoesArc.getLabelUltimaAtualizacao());
        atualizarPainelResumo(target);
    }

    private void obterIdsAlertas() {
        idsAlertas.add(PainelRiscoDeMercado.ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
        idsAlertas.add(PainelTendencia.ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
        idsAlertas.addAll(painelEdicaoElementos.getIdsAlertas());
    }

    @Override
    public String getTitulo() {
        return "Análise do ARC";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0203";
    }

    public PainelInformacoesArc getPainelInformacoesArc() {
        return painelInformacoesArc;
    }

    public void setPainelInformacoesArc(PainelInformacoesArc painelInformacoesArc) {
        this.painelInformacoesArc = painelInformacoesArc;
    }

    public Label getLoadScriptAlerta() {
        return loadScriptAlerta;
    }

    public Label getLoadScriptAlertaInferior() {
        return loadScriptAlertaInferior;
    }

    public Label getHideScriptAlertaInferior() {
        return hideScriptAlertaInferior;
    }

}