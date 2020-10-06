package br.gov.bcb.sisaps.web.page.dominio.agenda;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelListagemParticipantesAssinatura extends PainelSisAps {

    private static final String MEU_COMITE = "MeuComite";
    private static final String WIDTH_10 = "width:10%";
    private static final String WIDTH_15 = "width:15%";
    private static final String WIDTH_5 = "width:5%";
    private static final String NOME = "agenda.ciclo.entidadeSupervisionavel.nome";

    private Tabela<ParticipanteAgendaCorec> tabela;
    private IModel<List<ParticipanteAgendaCorec>> modelConsulta;
    private ProviderGenericoList<ParticipanteAgendaCorec> provider;
    private final boolean assinaturaPendente;
    private ModalWindow modalAlteracao;
    private ModalWindow modalDetalhar;

    public PainelListagemParticipantesAssinatura(String id, boolean assinaturaPendente) {
        super(id);
        this.assinaturaPendente = assinaturaPendente;
        inicializarModalsWindows();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addTabela();
    }
   
    private void inicializarModalsWindows() {
        modalAlteracao = new ModalWindow("modalAlteracao");
        modalAlteracao.setResizable(false);
        modalAlteracao.setInitialHeight(640);
        modalAlteracao.setInitialWidth(750);
        modalAlteracao.showUnloadConfirmation(false);
        addOrReplace(modalAlteracao);

        modalDetalhar = new ModalWindow("modalDetalhar");
        modalDetalhar.setResizable(false);
        modalDetalhar.setInitialHeight(640);
        modalDetalhar.setInitialWidth(750);
        modalDetalhar.showUnloadConfirmation(false);
        addOrReplace(modalDetalhar);
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<ParticipanteAgendaCorec> providerGenericoList = criarProvider();
        List<Coluna<ParticipanteAgendaCorec>> colunas = obterColunas();
        tabela = new Tabela<ParticipanteAgendaCorec>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(false);
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirPaginador(true);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        return cfg;
    }

    private ProviderGenericoList<ParticipanteAgendaCorec> criarProvider() {
        provider = new ProviderGenericoList<ParticipanteAgendaCorec>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<ParticipanteAgendaCorec>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<ParticipanteAgendaCorec>>() {
            @Override
            public List<ParticipanteAgendaCorec> getObject() {
                if (assinaturaPendente) {
                    return ParticipanteAgendaCorecMediator.get().consultaAtasPendente();
                } else {
                    return ParticipanteAgendaCorecMediator.get().consultaAtasAssinadas();
                }
            }
        };
        return modelConsulta;
    }

    private List<Coluna<ParticipanteAgendaCorec>> obterColunas() {
        List<Coluna<ParticipanteAgendaCorec>> colunas = new LinkedList<Coluna<ParticipanteAgendaCorec>>();

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Início do ciclo").setOrdenar(true)
                .setPropriedade("agenda.ciclo.dataInicioFormatada").setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Corec ").setOrdenar(true)
                .setPropriedade("agenda.ciclo.dataPrevisaoFormatada").setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("ES").setPropriedade(NOME).setOrdenar(true)
                .setEstiloCabecalho("width:25%"));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Equipe")
                .setPropriedade("agenda.ciclo.entidadeSupervisionavel.localizacao").setOrdenar(true)
                .setEstiloCabecalho("width:20%"));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Supervisor titular")
                .setPropriedade("agenda.ciclo.entidadeSupervisionavel.nomeSupervisor").setOrdenar(true)
                .setEstiloCabecalho(WIDTH_15));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Prioridade")
                .setPropriedadeTela("agenda.ciclo.entidadeSupervisionavel.prioridade.descricao")
                .setPropriedade("agenda.ciclo.entidadeSupervisionavel.prioridade.codigo").setOrdenar(true)
                .setEstiloCabecalho("width:8%"));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Ação").setPropriedade(NOME)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho(WIDTH_5));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<ParticipanteAgendaCorec> {

        public ComponenteCelulaLink() {
        }

        @Override
        public Component obterComponente(Item<ICellPopulator<ParticipanteAgendaCorec>> cellItem,
                final String componentId, final IModel<ParticipanteAgendaCorec> rowModel) {

            Fragment fragment = new Fragment(componentId, "frangmentLink", PainelListagemParticipantesAssinatura.this) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    AjaxSubmitLink linkDetalhar = criarLinkDetalhar(rowModel);
                    addOrReplace(linkDetalhar);

                    AjaxSubmitLink linkAlterar = criarLinkAlterar(rowModel);
                    addOrReplace(linkAlterar);
                }
            };

            return fragment;
        }
    }
    
    private AjaxSubmitLink criarLinkAlterar(final IModel<ParticipanteAgendaCorec> rowModel) {
        AjaxSubmitLink linkAlterar = new AjaxSubmitLink("linkAlterar") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("alterar", ConstantesImagens.IMG_ATUALIZAR));
            }
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                
                modalAlteracao.setContent(new PainelAlterarAssinaturaAgenda(modalAlteracao, rowModel
                        .getObject()));
                modalAlteracao.setOutputMarkupId(true);
                modalAlteracao.show(target);
            }
        };
        linkAlterar.setMarkupId(getId() + rowModel.getObject().getPk() + MEU_COMITE);
        linkAlterar.setOutputMarkupId(true);
        linkAlterar.setVisibilityAllowed(assinaturaPendente);
        return linkAlterar;
    }
    
    private AjaxSubmitLink criarLinkDetalhar(final IModel<ParticipanteAgendaCorec> rowModel) {
        AjaxSubmitLink linkDetalhar = new AjaxSubmitLink("linkDetalhar") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("detalhar", ConstantesImagens.IMG_DETALHAR));
            }
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                
                modalDetalhar.setContent(new PainelDetalharAssinaturaAgenda(modalDetalhar, rowModel
                        .getObject()));
                modalDetalhar.setOutputMarkupId(true);
                modalDetalhar.show(target);
            }
        };
        linkDetalhar.setMarkupId(getId() + rowModel.getObject().getPk() + MEU_COMITE);
        linkDetalhar.setOutputMarkupId(true);
        linkDetalhar.setVisibilityAllowed(!assinaturaPendente);
        return linkDetalhar;
    }

    public Tabela<ParticipanteAgendaCorec> getTabelaEntidades() {
        return tabela;
    }

    public void setTabelaEntidades(Tabela<ParticipanteAgendaCorec> tabelaEntidades) {
        this.tabela = tabelaEntidades;
    }

    public IModel<List<ParticipanteAgendaCorec>> getModelConsulta() {
        return modelConsulta;
    }

    public void setModelConsulta(IModel<List<ParticipanteAgendaCorec>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

    public ProviderGenericoList<ParticipanteAgendaCorec> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<ParticipanteAgendaCorec> provider) {
        this.provider = provider;
    }

}
