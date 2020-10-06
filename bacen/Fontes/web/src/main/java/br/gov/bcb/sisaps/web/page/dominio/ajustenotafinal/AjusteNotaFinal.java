package br.gov.bcb.sisaps.web.page.dominio.ajustenotafinal;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.LogOperacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.painel.PainelGerenciarNota;

public class AjusteNotaFinal extends DefaultPage {

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

    private PainelDadosMatrizPercentual painelDadosMatrizVigente;

    private final String idFocusSintense;

    public AjusteNotaFinal(PerfilRisco perfilRiscoAtual) {
        this(perfilRiscoAtual, null);
    }

    public AjusteNotaFinal(PerfilRisco perfilRiscoAtual, String idFocusSintense) {
        setSubirTelaAoSalvar(false);
        this.perfilRiscoAtual = perfilRiscoAtual;
        this.idFocusSintense = idFocusSintense;
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

        Matriz matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRiscoAtual);

        Metodologia metodologia =
                metodologiaMediator.buscarMetodologiaPorPK(perfilRiscoAtual.getCiclo().getMetodologia().getPk());
        
        form.addOrReplace(new PainelGerenciarNota("gerenciarNotaPanel", metodologia, matriz));
        
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
    }

    @Override
    public String getTitulo() {
        return "Ajuste de nota final";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0214";
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

    public String jsAtualizarBotoesVoltar() {
        return CKEditorUtils.jsAtualizarBotoesVoltar(linkVoltar.getId(), linkVoltarSemSalvar.getId(), true);
    }

    public void atualizarPerfilRiscoAtual() {
        this.perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRiscoAtual.getCiclo().getPk());
    }

    public String getNotaCalculadaFinal() {
        return painelDadosMatrizVigente.getNotaCalculadaFinal();
    }

}
