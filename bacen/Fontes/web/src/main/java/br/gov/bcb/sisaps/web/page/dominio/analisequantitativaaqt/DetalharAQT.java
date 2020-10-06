package br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.INSPETOR,
        PerfisAcesso.GERENTE, PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_TUDO, PerfisAcesso.ADMINISTRADOR})
public class DetalharAQT extends DefaultPage {
    private final WebMarkupContainer wmcExibirMensagem = new WebMarkupContainer("wmcExibirMensagem");

    private final Form<AvaliacaoRiscoControle> form = new Form<AvaliacaoRiscoControle>("formulario");

    private final AnaliseQuantitativaAQT aqt;

    private final List<String> idsAlertas = new ArrayList<String>();

    private Label loadScriptAlerta;

    private String idAlertaAAtualizar;

    private LinkVoltar linkVoltar;

    private LinkVoltarPerfilRisco linkVoltarSemSalvar;
    private boolean mostrarMensagem;

    private String msg;

    private boolean isPerfilRiscoAtual;

    public DetalharAQT(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual) {
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
    }

    public DetalharAQT(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual, String msg) {
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        success(msg);
    }

    public DetalharAQT(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual, boolean mostrarMensagem) {
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        this.mostrarMensagem = mostrarMensagem;
    }

    public DetalharAQT(AnaliseQuantitativaAQT aqt, boolean isPerfilRiscoAtual, boolean mostrarMensagem, String msg) {
        this.isPerfilRiscoAtual = isPerfilRiscoAtual;
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(aqt.getPk());
        this.mostrarMensagem = mostrarMensagem;
        this.msg = msg;
    }
    
    public DetalharAQT(PageParameters parameters) {
        String p1 = parameters.get("pkAnef").toString(null);
        Integer pkAqt = Integer.valueOf(p1);
        this.aqt = AnaliseQuantitativaAQTMediator.get().buscar(pkAqt);
        this.isPerfilRiscoAtual = true;
        if (RegraPerfilAcessoMediator.perfilInspetor() || RegraPerfilAcessoMediator.perfilSupervisor()) {
			setPaginaAnterior(new ConsultaHistoricoPage());
		}else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
            
    }
     

    private void addMensagem() {
        Model<String> model = new Model<String>("");
        if (msg != null) {
            model = new Model<String>(msg.replaceAll(".*<", "").replaceAll(">.*", ""));
        }

        LabelLink labelLink = new LabelLink("linkPaginaGerenciarNotasSinteses", model) {
            @Override
            public void onClick() {
                avancarParaNovaPagina(new GerenciarNotaSinteseAQTPage(PerfilRiscoMediator.get().obterPerfilRiscoAtual(
                        aqt.getCiclo().getPk()), obterNomePainelFocus()));
            };

            private String obterNomePainelFocus() {
                return "idTituloSintese" + SisapsUtil.criarMarkupId(aqt.getParametroAQT().getDescricao());
            }
        };
        labelLink.setVisibilityAllowed(mostrarMensagem && perfilSupervisor(getPaginaAtual()));
        labelLink.setMarkupId(labelLink.getId());
        labelLink.setOutputMarkupId(true);
        wmcExibirMensagem.add(labelLink);

        String inicio = "";
        String fim = "";

        if (mostrarMensagem && msg != null) {
            try {
                inicio = msg.substring(0, msg.indexOf("<")).toString();
                fim = msg.substring(msg.indexOf(">") + 1).toString();
            } catch (IndexOutOfBoundsException e) {
                inicio = msg;
            }
        }
        Label label = new Label("lblMsgInicial", inicio);
        label.setEscapeModelStrings(false);
        wmcExibirMensagem.add(label);
        wmcExibirMensagem.add(new Label("lblMsgFinal", fim));
        wmcExibirMensagem.setVisibilityAllowed(mostrarMensagem
                && getPaginaAtual().getPaginaAnterior() instanceof AnaliseAQT);
        form.add(wmcExibirMensagem);

    }

    @SuppressWarnings("rawtypes")
    public abstract class LabelLink extends Link {
        private final IModel linkModel;

        @SuppressWarnings("unchecked")
        public LabelLink(String id, IModel linkModel) {
            super(id, linkModel);
            this.linkModel = linkModel;
        }

        @Override
        public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
            replaceComponentTagBody(markupStream, openTag, linkModel.getObject().toString());
        }
    }

    @Override
    protected void onInitialize() {

        addMensagem();

        super.onInitialize();
        linkVoltar = new LinkVoltar() {
            @Override
            public void onClick() {
                DefaultPage paginaOrigem = getPaginaAnterior().getPaginaAnterior();
                if (getPaginaAnterior() instanceof AnaliseAQT || getPaginaAnterior() instanceof EdicaoAQT) {
                    setResponsePage(paginaOrigem);
                } else {
                    setResponsePage(getPaginaAnterior());
                }

            }
        };
        form.add(linkVoltar);
        linkVoltarSemSalvar = new LinkVoltarPerfilRisco("bttVoltarSemSalvar", aqt.getCiclo().getPk());
        form.add(linkVoltarSemSalvar);
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(aqt.getCiclo().getPk());
        LogOperacaoMediator.get().gerarLogDetalhamento(perfilRiscoAtual.getCiclo().getEntidadeSupervisionavel(),
                perfilRiscoAtual, atualizarDadosPagina(getPaginaAtual()));
    }

    private void addPainelResumo() {
        PainelResumoElementosAnefs painelResumoElementosAnefs =
                new PainelResumoElementosAnefs("idPainelResumoElementosAnef", aqt, isPerfilRiscoAtual);
        painelResumoElementosAnefs.setOutputMarkupId(true);
        form.addOrReplace(painelResumoElementosAnefs);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addOrReplace(form);
        addComponentes();
    }

    private void addComponentes() {
        addPainelCiclo();
        addPainelAcoes();
        addPainelInformacao();
        addPainelResumo();
        addPainelNotas();
        addPainelAnexos();
        addPainelElementos();
    }

    private void addPainelNotas() {
        PainelNotasAnef painelNotas = new PainelNotasAnef("idPainelNotasElementosAnef", aqt, isPerfilRiscoAtual);
        painelNotas.setOutputMarkupId(true);
        form.addOrReplace(painelNotas);
    }

    private void addPainelElementos() {
        PainelElementosAnef painelElementos = new PainelElementosAnef("idPainelElementosAnefs", aqt);
        painelElementos.setOutputMarkupId(true);
        form.addOrReplace(painelElementos);
    }

    private void addPainelInformacao() {
        form.addOrReplace(new PainelInformacoesAQT("idPainelResumoAqt", aqt));
    }

    private void addPainelCiclo() {
        PainelResumoCiclo painelResumoCiclo = new PainelResumoCiclo("resumoCicloPanel", aqt.getCiclo().getPk(), null);
        painelResumoCiclo.setOutputMarkupId(true);
        form.addOrReplace(painelResumoCiclo);
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

    @SuppressWarnings("unused")
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

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0212";
    }

    @Override
    public String getTitulo() {
        return "Detalhes do ANEF";
    }

    public AnaliseQuantitativaAQT getAqt() {
        return aqt;
    }

}
