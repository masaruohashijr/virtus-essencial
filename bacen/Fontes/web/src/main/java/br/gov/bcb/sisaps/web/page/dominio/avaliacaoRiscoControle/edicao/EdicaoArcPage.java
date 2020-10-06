package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

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
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.PainelInformacoesArc;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.analise.PainelResumoElementosArcs;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaARCInspetorPage;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.INSPETOR})
public class EdicaoArcPage extends DefaultPage {

    private static final String SPACE = "";
    private static final String NULL = "null";
    public static final String ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos";
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR = "idAlertaDadosNaoSalvosInferior";

    @SpringBean
    private CicloMediator cicloMediator;

    private AvaliacaoRiscoControle arc;
    private Ciclo ciclo;
    private Atividade atividade;

    private PainelElementos painelElementos;
    private PainelAvaliacaoARCInspetor painelAvaliacaoARCInspetor;
    private Label loadScriptAlerta;
    private Label loadScriptAlertaInferior;
    private Label hideScriptAlertaInferior;
    private String idAlertaAAtualizar;
    private final List<String> idsAlertas = new ArrayList<String>();
    private final Integer pkArc;
    private final Integer pkCiclo;
    private final Integer pkAtividade;
    private PainelInformacoesArc painelInformacoesArc;
    private PainelTendencia painelTendencia;
    private PainelResumoElementosArcs painelResumo;
    private final boolean islinkExterno;

    public EdicaoArcPage(Integer pkArc, Integer pkCiclo, Integer pkAtividade) {
        this.pkArc = pkArc;
        this.pkCiclo = pkCiclo;
        this.pkAtividade = pkAtividade;
        this.islinkExterno = false;
    }
    
    public EdicaoArcPage(PageParameters parameters) {
        String p1 = parameters.get("pkArc").toString(null);
        String p2 = parameters.get("pkCiclo").toString(null);
        String p3 = parameters.get("pkAtividade").toString(null);
        this.pkArc = Integer.valueOf(p1);
        this.pkCiclo = Integer.valueOf(p2);
        if (p3.equals(NULL) || p3.equals(SPACE)) {
            this.pkAtividade = null;
        } else {
            this.pkAtividade = Integer.valueOf(p3);
        }
        this.islinkExterno = true;
        
        ArcNotasVO arc = AvaliacaoRiscoControleMediator.get().consultarNotasArc(pkArc);
        if (AvaliacaoRiscoControleMediator.get().isResponsavelARC(arc, ((UsuarioAplicacao) UsuarioCorrente.get()))) {
            setPaginaAnterior(new ConsultaARCInspetorPage());
        } else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
        
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
    protected void onAfterRender() {
        super.onAfterRender();
        obterIdsAlertas();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setSubirTelaAoSalvar(false);
        this.arc = AvaliacaoRiscoControleMediator.get().buscarPorPk(pkArc);
        this.ciclo = cicloMediator.loadPK(pkCiclo);
        this.atividade = pkAtividade == null ? null : AtividadeMediator.get().loadPK(pkAtividade);
        buildSecoes();
    }

    private void buildSecoes() {
        ParametroGrupoRiscoControle parametroGrupoRiscoControle = null;
        if (atividade == null) {
            parametroGrupoRiscoControle = 
                    AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(arc.getPk())
                            .getParametroGrupoRiscoControle();
        } else {
            parametroGrupoRiscoControle = 
                    CelulaRiscoControleMediator.get().buscarCelularPorAvaliacaoEAtividade(atividade, arc)
                    .getParametroGrupoRiscoControle();
        }
        addLoadScriptAlerta();
        addLoadScriptAlertaInferior();
        hideLoadScriptAlertaInferior();
        Form<?> form = new Form<Object>("form_edicao");
        form.add(new PainelResumoCiclo("idPainelCicloResumo", ciclo.getPk()));
        painelInformacoesArc =
                new PainelInformacoesArc("idPainelInformacaoArc", arc, ciclo, atividade, parametroGrupoRiscoControle);
        form.add(painelInformacoesArc);
        painelResumo = new PainelResumoElementosArcs("idPainelResumoElementosArc", arc, ciclo.getMatriz(), arc, true);
        painelResumo.setOutputMarkupId(true);
        form.addOrReplace(painelResumo);
        painelAvaliacaoARCInspetor =
                new PainelAvaliacaoARCInspetor("idPainelAvaliacaoARCInspetor", parametroGrupoRiscoControle, arc, ciclo);
        painelAvaliacaoARCInspetor.setOutputMarkupId(true);
        form.add(painelAvaliacaoARCInspetor);
        painelElementos =
                new PainelElementos("idPainelElementos", arc, ElementoMediator.get().buscarElementosOrdenadosDoArc(
                        arc.getPk()), ciclo);
        form.add(painelElementos);
        painelTendencia = new PainelTendencia("idPainelTendenciaRiscoMercado", parametroGrupoRiscoControle, arc, ciclo);
        painelTendencia.setOutputMarkupId(true);
        form.addOrReplace(painelTendencia);
        form.add(new PainelAnexoArc("idPainelAnexoArc", ciclo, arc, true));
        form.add(botaoConcluir());
        form.add(new LinkVoltar() {
            @Override
            public void onClick() {
                if (getPaginaAnterior() instanceof ConsultaARCInspetorPage) {
                    super.setResponsePage(new ConsultaARCInspetorPage());
                } else {
                    super.setResponsePage(getPaginaAnterior());
                }
            }
        });
        addOrReplace(form);
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        LogOperacaoMediator.get().gerarLogDetalhamento(ciclo.getEntidadeSupervisionavel(), perfilRiscoAtual,
                atualizarDadosPagina(getPaginaAtual()));
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
        addOrReplace(loadScriptAlerta);

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

    private void obterIdsAlertas() {
        idsAlertas.add(PainelAvaliacaoARCInspetor.ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
        idsAlertas.add(PainelTendencia.ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
        idsAlertas.addAll(painelElementos.getIdsAlertas());
    }

    @Override
    public String getTitulo() {
        return "Edição do ARC";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0302";
    }

    private AjaxSubmitLinkSisAps botaoConcluir() {
        return new AjaxSubmitLinkSisAps("bttConcluir") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                concluir();
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
    
    private void concluir() {
        String msgSucesso = AvaliacaoRiscoControleMediator.get().concluirEdicaoARCInspetor(ciclo, arc.getPk());
        if (getPaginaAnterior() instanceof ConsultaARCInspetorPage || islinkExterno) {
            super.setResponsePage(new ConsultaARCInspetorPage(msgSucesso));
        } else {
            avancarParaNovaPagina(new PerfilDeRiscoPage(ciclo.getPk(), msgSucesso, false),
                    getPaginaAnterior().getPaginaAnterior());
        }
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

    public void atualizarAlertaPrincipal(AjaxRequestTarget target, String idAlertaAAtualizar) {
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(getLoadScriptAlerta());
        target.add(getHideScriptAlertaInferior());
        atualizarPainelInformacoesArc(target);
    }

    public void atualizarNovaNotaArc(AjaxRequestTarget target) {
        painelAvaliacaoARCInspetor.atualizarNovaNotaARC(target);
        painelResumo.atualizarNotaCalculada(target);
        target.add(painelResumo);
        target.add(painelTendencia);
    }

    public void atualizarPainelInformacoesArc(AjaxRequestTarget target) {
        target.add(painelInformacoesArc);
    }

}
