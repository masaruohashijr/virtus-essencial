package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.PanelCachingTab;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.geral.DataUtilLocalDate;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxLinkIndicator;
import br.gov.bcb.sisaps.web.page.dominio.gestaoes.PainelParametrosConsultaGestaoES;

public class GestaoAgendaPage extends DefaultPage {
    private Form<?> form;
    private ConsultaCicloVO consultaCiclo = new ConsultaCicloVO();
    private PainelParametrosConsultaGestaoES painelConsultaGestaoES;
    private final ConsultaEntidadeSupervisionavelVO consultaParametro = new ConsultaEntidadeSupervisionavelVO();
    private final ConsultaEntidadeSupervisionavelVO consultaResultado = new ConsultaEntidadeSupervisionavelVO();
    private PainelListagemGestaoAgenda painelComitesRealizados;
    private AjaxTabbedPanel<?> bcTabbedPanel;
    private final List<ITab> abasComite = new ArrayList<ITab>();
    private PainelListagemGestaoAgenda painelComitesRealizar;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        form = new Form<Object>("formulario");
        form.add(new LinkVoltar());
        
        List<ComponenteOrganizacionalVO> lista = EntidadeSupervisionavelMediator.get().consultarUnidadesESsAtivas();
        if (lista.size() == 1) {
            consultaCiclo.setEntidadeSupervisionavel(new EntidadeSupervisionavelVO());
            consultaCiclo.getEntidadeSupervisionavel().setLocalizacao(lista.get(0).getSigla());
            consultaCiclo.setBuscarHierarquiaInferior(true);
        }
        addPainelParametros();
        addAbas();
        addOrReplace(form);
    }

   

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                "res/indicator.css")));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AjaxLinkIndicator.class,
                "res/indicator.js")));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (consultaCiclo.getDataCorecInicio() != null) {
            painelConsultaGestaoES.getModelInicio().setObject(
                    DataUtilLocalDate.localDateToString(consultaCiclo.getDataCorecInicio()));
        }
        if (consultaCiclo.getDataCorecFim() != null) {
            painelConsultaGestaoES.getModelFim().setObject(
                    DataUtilLocalDate.localDateToString(consultaCiclo.getDataCorecFim()));
        }
    }

    private void addAbas() {
        criarAbaComiteRealizar();
        criarAbaComiteRealizado();
        bcTabbedPanel = new AbasComitesAjax("abas", abasComite);
        bcTabbedPanel.setSelectedTab(0);
        form.addOrReplace(bcTabbedPanel);
    }

    @SuppressWarnings("rawtypes")
    protected final class AbasComitesAjax extends AjaxTabbedPanel {
        @SuppressWarnings("unchecked")
        public AbasComitesAjax(String id, List<ITab> tabs) {
            super(id, tabs);
        }

        @SuppressWarnings("unused")
        private void onSubmitClick(final int index) {
            setSelectedTab(index);
        }

    }

    protected void criarAbaComiteRealizar() {
        PanelCachingTab panelCachingTab = new PanelCachingTab(new AbstractTab(new Model<String>("Comitês a realizar")) {

            @Override
            public Panel getPanel(String panelId) {
                painelComitesRealizar =
                        new PainelListagemGestaoAgenda(panelId, consultaCiclo, "Lista de comitês a realizar",
                                bcTabbedPanel.getSelectedTab(), getPaginaAtual());
                return painelComitesRealizar;
            }

        });

        abasComite.add(panelCachingTab);

    }

    protected void criarAbaComiteRealizado() {
        PanelCachingTab panelCachingTab = new PanelCachingTab(new AbstractTab(new Model<String>("Comitês realizados")) {

            @Override
            public Panel getPanel(String panelId) {
                painelComitesRealizados =
                        new PainelListagemGestaoAgenda(panelId, consultaCiclo, "Lista de comitês realizados",
                                bcTabbedPanel.getSelectedTab(), getPaginaAtual());
                return painelComitesRealizados;
            }
        });

        abasComite.add(panelCachingTab);
    }

    private void addPainelParametros() {
        painelParametros();
    }

    private void painelParametros() {
        painelConsultaGestaoES =
                new PainelParametrosConsultaGestaoES("painelParametros", consultaParametro, consultaResultado,
                        consultaCiclo);
        form.add(painelConsultaGestaoES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Gestão de agenda";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0504";
    }

    public void atualizar(AjaxRequestTarget target, ConsultaCicloVO consultaCiclo) {
        atualizarLista(target, consultaCiclo);
    }

    private void atualizarLista(AjaxRequestTarget target, ConsultaCicloVO consultaCiclo) {
        this.consultaCiclo = consultaCiclo;
        if (bcTabbedPanel.getSelectedTab() == 0) {
            painelComitesRealizar.setConsultaCicloVO(consultaCiclo);
            target.add(painelComitesRealizar);
        } else {
            painelComitesRealizados.setConsultaCicloVO(consultaCiclo);
            target.add(painelComitesRealizados);
        }
        target.add(getFeedBack());
    }

    public void limpar(AjaxRequestTarget target, ConsultaCicloVO consultaCiclo) {
        consultaCiclo.setDataCorecInicio(null);
        consultaCiclo.setDataCorecFim(null);
        atualizarLista(target, consultaCiclo);
        target.add(painelConsultaGestaoES);
    }

    public ConsultaCicloVO getConsultaCiclo() {
        return consultaCiclo;
    }

    public void setConsultaCiclo(ConsultaCicloVO consultaCiclo) {
        this.consultaCiclo = consultaCiclo;
    }

    public AjaxTabbedPanel<?> getBcTabbedPanel() {
        return bcTabbedPanel;
    }

    public void setBcTabbedPanel(AjaxTabbedPanel<?> bcTabbedPanel) {
        this.bcTabbedPanel = bcTabbedPanel;
    }

    public PainelParametrosConsultaGestaoES getPainelConsultaGestaoES() {
        return painelConsultaGestaoES;
    }

    public void setPainelConsultaGestaoES(PainelParametrosConsultaGestaoES painelConsultaGestaoES) {
        this.painelConsultaGestaoES = painelConsultaGestaoES;
    }

    public void atualizar(AjaxRequestTarget target, String msg) {
        if (msg != null) {

            atualizar(target, getConsultaCiclo());
            if (!msg.isEmpty()) {
                Page pagina = painelConsultaGestaoES.getPage();
                pagina.success(msg);
                target.add(pagina.get("feedback"));
            }
        }

    }




    public PainelListagemGestaoAgenda getPainelComitesRealizar() {
        return painelComitesRealizar;
    }



    public void setPainelComitesRealizar(PainelListagemGestaoAgenda painelComitesRealizar) {
        this.painelComitesRealizar = painelComitesRealizar;
    }

    
    
    
}
