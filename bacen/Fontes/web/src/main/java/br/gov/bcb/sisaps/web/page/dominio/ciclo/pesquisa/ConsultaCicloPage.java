/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelConsultaAQTSupervisor;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelSintesesAQTRevisao;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.PainelConsultaARCSupervisor;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.PainelConsultaNovasESs;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelSintesesRiscoRevisao;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class ConsultaCicloPage extends DefaultPageMenu {

    private static final String MENU = "Painel do supervisor";
    private ConsultaCicloPanel painelCicloPanel;
    private PainelConsultaNovasESs painelConsultaNovasESs;
    private final Form<?> form = new Form<Object>("formulario");
    private final WebMarkupContainer wmcExibirMensagem = new WebMarkupContainer("mensagemSucessoConclusaoARC");
    private final WebMarkupContainer wmcExibirNovasEs = new WebMarkupContainer("tDadosARCsPendAnalise");
    private PerfilRisco perfilRisco = new PerfilRisco();
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;
    private boolean mostrarMensagem;

    public ConsultaCicloPage(Ciclo ciclo) {
        mostrarMensagem = true;
        perfilRisco = perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk());
        addMensagem();
        addComponentes();
    }

    public ConsultaCicloPage() {
        addComponentes();
    }

    private void addMensagem() {
        AjaxSubmitLink link = new AjaxSubmitLink("linkPaginaGerenciarNotasSinteses") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                avancarParaNovaPagina(new GerenciarNotaSintesePage(perfilRisco));
            }
        };
        link.setMarkupId(link.getId());
        link.setOutputMarkupId(true);
        wmcExibirMensagem.addOrReplace(link);
        wmcExibirMensagem.setVisible(mostrarMensagem);
        form.addOrReplace(wmcExibirMensagem);
    }

    private void addComponentes() {
        addMensagem();
        addPainelCicloPanel();
        painelConsultaNovasESs = new PainelConsultaNovasESs("NovasEss");
        wmcExibirNovasEs.add(painelConsultaNovasESs);
        mostrarNovasEs();
        form.add(new PainelConsultaARCSupervisor("consultaARCSupervisor"));
        form.add(wmcExibirNovasEs);
        form.add(new PainelSintesesRiscoRevisao("painelSintesesRiscoRevisao"));
        form.add(new PainelConsultaAQTSupervisor("consultaAQTSupervisor"));
        form.add(new PainelSintesesAQTRevisao("painelSintesesAQTRevisao"));
        add(form);
    }

    private void addPainelCicloPanel() {
        painelCicloPanel = new ConsultaCicloPanel("consultaCicloPanel", PerfilAcessoEnum.SUPERVISOR);
        form.addOrReplace(painelCicloPanel);
    }

    private void mostrarNovasEs() {
        wmcExibirNovasEs.setVisible(painelConsultaNovasESs.getProvider().size() != 0);
    }

    public void atualizarPaineis(AjaxRequestTarget target) {
        addPainelCicloPanel();
        target.add(painelConsultaNovasESs.getTabela());
        mostrarNovasEs();
        target.add(wmcExibirNovasEs);
        Page pagina = painelConsultaNovasESs.getPage();
        pagina.success("O ciclo foi incluído com sucesso, bem como o registro para análise econômico-financeira."
                + " A matriz de riscos e controles está disponível para edição.");
        target.add(pagina.get("feedback"));
        target.add(painelCicloPanel);
        target.add(painelCicloPanel.getTabela());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return MENU;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0201";
    }

    public WebMarkupContainer getExibirNovasEs() {
        return wmcExibirNovasEs;
    }

}
