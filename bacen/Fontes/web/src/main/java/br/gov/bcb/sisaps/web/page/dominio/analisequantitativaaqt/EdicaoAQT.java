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
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaARCInspetorPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelAvaliacaoARCInspetor;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelTendencia;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.INSPETOR})
public class EdicaoAQT extends DefaultPage {

    public static final String ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos";
    public static final String ID_ALERTA_DADOS_NAO_SALVOS_INFERIOR = "idAlertaDadosNaoSalvosInferior";
    private static final String ANEF_CONCLUIDO_COM_SUCESSO = "ANEF concluído com sucesso.";
    private final List<String> idsAlertas = new ArrayList<String>();
    private PainelResumoCiclo painelResumoCiclo;
    private AnaliseQuantitativaAQT aqt;
    private PainelElementosEdicaoAnef painelElementos;
    private String idAlertaAAtualizar;
    private Label loadScriptAlerta;
    private Label loadScriptAlertaInferior;
    private Label hideScriptAlertaInferior;
    private Ciclo ciclo;
    private Form<?> form;
    private PainelInformacoesAQT painelInformacoes;
    private PainelResumoElementosAnefs painelResumoElementosAnefs;
    private PainelAvaliacaoANEFInspetor painelAvaliacaoANEFInspetor;
    private PainelAnexosAnef painelAnexosAnef;
    private final boolean islinkExterno;

    public EdicaoAQT(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
        this.islinkExterno = false;
    }

    public EdicaoAQT(PageParameters parameters) {
        String p1 = parameters.get("pkAnef").toString(null);
        Integer pkAqt = Integer.valueOf(p1);
        AnaliseQuantitativaAQT aqt = AnaliseQuantitativaAQTMediator.get().buscar(pkAqt);
        this.aqt = aqt;
        this.islinkExterno = true;
        if (AnaliseQuantitativaAQTMediator.get().estadosDesignacaoDelegacaoInspetor(aqt)) {
            setPaginaAnterior(new ConsultaARCInspetorPage());
        } else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
        
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setSubirTelaAoSalvar(false);
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        this.ciclo = aqt.getCiclo();
        
        addComponentes();
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

    private void addComponentes() {
        addLoadScriptAlertaInferior();
        hideLoadScriptAlertaInferior();

        form = new Form<Object>("formulario");
        form.add(addPainelResumoCiclo());
        addLoadScriptAlerta();
        addPainelResumo();
        painelElementos =
                new PainelElementosEdicaoAnef("idPainelElementos", aqt, ElementoAQTMediator.get()
                        .buscarElementosOrdenadosDoAnef(aqt.getPk()));
        addPainelInformacoesAQT();
        form.add(painelElementos);
        painelAnexosAnef = new PainelAnexosAnef("idPainelAnexoAnef", aqt.getCiclo(), aqt, true, false);
        form.addOrReplace(painelAnexosAnef);

        AjaxSubmitLinkSisAps botaoConcluir = botaoConcluir();

        painelAvaliacao();

        form.add(botaoConcluir);
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

    private void painelAvaliacao() {
        painelAvaliacaoANEFInspetor = new PainelAvaliacaoANEFInspetor("idPainelAvaliacaoANEFInspetor", aqt);
        painelAvaliacaoANEFInspetor.setMarkupId(painelAvaliacaoANEFInspetor.getId());
        painelAvaliacaoANEFInspetor.setMarkupId(painelAvaliacaoANEFInspetor.getId());
        painelAvaliacaoANEFInspetor.setOutputMarkupId(true);
        form.add(painelAvaliacaoANEFInspetor);
    }

    private PainelResumoCiclo addPainelResumoCiclo() {
        painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", ciclo.getPk(), null);
        painelResumoCiclo.setMarkupId(painelResumoCiclo.getId());
        return painelResumoCiclo;
    }

    private void addPainelInformacoesAQT() {
        painelInformacoes = new PainelInformacoesAQT("idPainelInformacoesAnef", aqt);
        painelInformacoes.setMarkupId(painelInformacoes.getId());
        painelInformacoes.setOutputMarkupId(true);
        form.addOrReplace(painelInformacoes);
    }

    private void addPainelResumo() {
        painelResumoElementosAnefs = new PainelResumoElementosAnefs("idPainelResumoElementosAnef", aqt, true);
        painelResumoElementosAnefs.setMarkupId(painelResumoElementosAnefs.getId());
        painelResumoElementosAnefs.setOutputMarkupId(true);
        form.addOrReplace(painelResumoElementosAnefs);
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0303";
    }

    @Override
    public String getTitulo() {
        return "Edição do ANEF";
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

    public void atualizarAlertaPrincipal(AjaxRequestTarget target, String idAlertaAAtualizar) {
        this.idAlertaAAtualizar = idAlertaAAtualizar;
        target.add(getLoadScriptAlerta());
        target.add(getHideScriptAlertaInferior());
        atualizarPainelInformacoesAnef(target);
    }

    public void atualizarNovaNotaArc(AjaxRequestTarget target) {
        painelResumoElementosAnefs.atualizarNotaCalculadaANEF(target);
        target.add(painelResumoElementosAnefs);
    }

    public void atualizarNotaCalculadaInspetor(AjaxRequestTarget target) {
        painelAvaliacaoANEFInspetor.atualizarNovaNotaARC(target);
    }

    public void atualizarPainelInformacoesAnef(AjaxRequestTarget target) {
        target.add(painelInformacoes.getLblEstado());
        target.add(painelInformacoes);
    }

    public void atualizarPaineis(AjaxRequestTarget target) {
        target.add(painelAvaliacaoANEFInspetor);
        target.add(painelResumoElementosAnefs);
        atualizarPainelInformacoesAnef(target);
    }

    private AjaxSubmitLinkSisAps botaoConcluir() {
        return new AjaxSubmitLinkSisAps("bttConcluir") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                target.add(getLoadScriptAlertaInferior());
                validarAnexos();
                AnaliseQuantitativaAQTMediator.get().concluirEdicaoANEFInspetor(aqt);
                avancarPagina();
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

    private void avancarPagina() {
        if (getPaginaAnterior() instanceof ConsultaARCInspetorPage || islinkExterno) {
            super.setResponsePage(new ConsultaARCInspetorPage(ANEF_CONCLUIDO_COM_SUCESSO));
        } else {
            avancarParaNovaPagina(new PerfilDeRiscoPage(ciclo.getPk(), ANEF_CONCLUIDO_COM_SUCESSO, false),
                    getPaginaAnterior().getPaginaAnterior());
        }
    }

    protected void validarAnexos() {
        painelElementos.validarAnexos();
        painelAnexosAnef.validarAnexos();
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

    private void obterIdsAlertas() {
        idsAlertas.add(PainelAvaliacaoARCInspetor.ID_ALERTA_DADOS_NAO_SALVOS_AVALIACAO);
        idsAlertas.add(PainelTendencia.ID_ALERTA_DADOS_NAO_SALVOS_TENDENCIA);
        idsAlertas.addAll(painelElementos.getIdsAlertas());
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
