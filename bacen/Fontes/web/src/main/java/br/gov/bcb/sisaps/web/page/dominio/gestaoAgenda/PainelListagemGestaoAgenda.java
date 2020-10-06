package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.io.File;
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

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.relatorios.GerarRelatorioComitesLink;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.dominio.agenda.AgendaPage;

public class PainelListagemGestaoAgenda extends PainelSisAps {

    private static final String WIDTH_8 = "width:8%";

    private static final String NOME = "entidadeSupervisionavel.nome";

    private Tabela<CicloVO> tabela;
    private IModel<List<CicloVO>> modelConsulta;
    private ConsultaCicloVO consulta;
    private final String titulo;
    private ProviderGenericoList<CicloVO> provider;
    private final int indice;
    private GerarRelatorioComitesLink linkRelatorioComitesRealizados;
    private GerarRelatorioComitesLink linkRelatorioComitesARealizar;
    private final DefaultPage paginaAtual;
    private ModalWindow modalAlteracao;
    private ModalWindow modalDetalhar;
    private PainelArquivoParticipante painelArquivo;

    public PainelListagemGestaoAgenda(String id, ConsultaCicloVO consulta, String titulo, int indice,
            DefaultPage paginaAtual) {
        super(id);
        this.consulta = consulta;
        this.titulo = titulo;
        this.indice = indice;
        this.paginaAtual = paginaAtual;
        inicializarModalsWindows();
        addTabela();
    }

    private void inicializarModalsWindows() {
        modalAlteracao = new ModalWindow("modalAlteracao");
        modalAlteracao.setResizable(false);
        modalAlteracao.setInitialHeight(640);
        modalAlteracao.setInitialWidth(850);
        addOrReplace(modalAlteracao);

        modalDetalhar = new ModalWindow("modalDetalhar");
        modalDetalhar.setResizable(false);
        modalDetalhar.setInitialHeight(640);
        modalDetalhar.setInitialWidth(850);
        addOrReplace(modalDetalhar);
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<CicloVO> providerGenericoList = criarProvider();
        List<Coluna<CicloVO>> colunas = obterColunas();
        tabela = new Tabela<CicloVO>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
        addPainelArquivo();
        addLinksExportacao();
    }

    private void addPainelArquivo() {
        painelArquivo = new PainelArquivoParticipante("painelArquivo");
        painelArquivo.setVisibilityAllowed(indice == 0 && !(paginaAtual instanceof AgendaPage));
        addOrReplace(painelArquivo);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(true);
        cfg.setTitulo(Model.of(titulo));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirPaginador(true);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        return cfg;
    }

    private ProviderGenericoList<CicloVO> criarProvider() {
        provider = new ProviderGenericoList<CicloVO>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<CicloVO>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<CicloVO>>() {
            @Override
            public List<CicloVO> getObject() {
                return getObjectModel();
            }
        };
        return modelConsulta;
    }

    private List<CicloVO> getObjectModel() {
        if (getPaginaAtual() instanceof GestaoAgendaPage) {
            GestaoAgendaPage page = (GestaoAgendaPage) getPaginaAtual();
            if (page.getPainelConsultaGestaoES().getConsultaCiclo() != null) {
                consulta = page.getPainelConsultaGestaoES().getConsultaCiclo();
            }
        } else {
            AgendaPage page = (AgendaPage) getPaginaAtual();
            if (page.getPainelConsultaGestaoES().getConsultaCiclo() != null) {
                consulta = page.getPainelConsultaGestaoES().getConsultaCiclo();
            }
        }

        if (indice == 0) {
            return CicloMediator.get().listarComitesRealizar(consulta);
        } else {
            return CicloMediator.get().listarComitesRealizados(consulta);
        }
    }

    private List<Coluna<CicloVO>> obterColunas() {
        List<Coluna<CicloVO>> colunas = new LinkedList<Coluna<CicloVO>>();

        colunas.add(new Coluna<CicloVO>().setCabecalho("Início do ciclo").setOrdenar(true).setPropriedade("dataInicio")
                .setPropriedadeTela("dataInicioFormatada").setEstiloCabecalho(WIDTH_8));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Corec ").setOrdenar(true).setPropriedade("dataPrevisaoCorec")
                .setPropriedadeTela("dataPrevisaoFormatada").setEstiloCabecalho("width:6%"));

        colunas.add(new Coluna<CicloVO>().setCabecalho("ES").setPropriedade(NOME).setOrdenar(true)
                .setEstiloCabecalho("width:31%"));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Equipe").setPropriedade("entidadeSupervisionavel.localizacao")
                .setOrdenar(true).setEstiloCabecalho("width:20%"));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Supervisor titular").setPropriedade("nomeSupervisor")
                .setOrdenar(true).setEstiloCabecalho("width:24%"));
        
        if (paginaAtual.getPerfilPorPagina() != null
                && !paginaAtual.getPerfilPorPagina().equals(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS)) {
            colunas.add(new Coluna<CicloVO>().setCabecalho("Prioridade")
                    .setPropriedadeTela("entidadeSupervisionavel.prioridade.descricao")
                    .setPropriedade("entidadeSupervisionavel.prioridade.codigo").setOrdenar(true)
                    .setEstiloCabecalho(WIDTH_8));
        }

        colunas.add(new Coluna<CicloVO>().setCabecalho("Ação").setPropriedade(NOME)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho("width:3%"));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<CicloVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<CicloVO>> cellItem, final String componentId,
                final IModel<CicloVO> rowModel) {

            Fragment fragment = new Fragment(componentId, "frangmentLink", PainelListagemGestaoAgenda.this) {
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

    private AjaxSubmitLink criarLinkDetalhar(final IModel<CicloVO> rowModel) {
        AjaxSubmitLink linkDetalhar = new AjaxSubmitLink("linkDetalhar") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("detalhar", ConstantesImagens.IMG_DETALHAR));
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                modalDetalhar.setContent(new PainelDetalharAgenda(modalDetalhar, rowModel.getObject().getPk()));
                modalDetalhar.setOutputMarkupId(true);
                modalDetalhar.show(target);
            }
        };
        linkDetalhar.setMarkupId(getId() + "Agenda" + rowModel.getObject().getPk());
        linkDetalhar.setOutputMarkupId(true);
        return linkDetalhar;
    }

    private AjaxSubmitLink criarLinkAlterar(final IModel<CicloVO> rowModel) {
        AjaxSubmitLink linkAlterar = new AjaxSubmitLink("linkAlterar") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("alterar", ConstantesImagens.IMG_ATUALIZAR));
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                modalAlteracao.setContent(new PainelAlterarAgenda(modalAlteracao, rowModel.getObject().getPk()));
                modalAlteracao.setOutputMarkupId(true);
                modalAlteracao.show(target);
            }
        };
        linkAlterar.setVisibilityAllowed(rowModel.getObject().isPodeAlterar()
                && !(getPaginaAtual() instanceof AgendaPage));
        linkAlterar.setMarkupId(getId() + rowModel.getObject().getPk());
        linkAlterar.setOutputMarkupId(true);
        return linkAlterar;
    }

    private void addLinksExportacao() {
        linkRelatorioComitesRealizados =
                new GerarRelatorioComitesLink("linkExportarComitesRealizados", new File("ListaComitesRealizados.xls"),
                        paginaAtual, true, consulta) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(indice == 1);
                    }
                };
        addOrReplace(linkRelatorioComitesRealizados);
        linkRelatorioComitesARealizar =
                new GerarRelatorioComitesLink("linkExportarComitesARealizar", new File("ListaComitesARealizar.xls"),
                        paginaAtual, false, consulta) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(indice == 0);
                    }
                };
        addOrReplace(linkRelatorioComitesARealizar);
    }

    public void atualizarLinks(AjaxRequestTarget target) {
        target.add(linkRelatorioComitesRealizados);
        target.add(linkRelatorioComitesARealizar);
    }

    public Tabela<CicloVO> getTabelaEntidades() {
        return tabela;
    }

    public void setTabelaEntidades(Tabela<CicloVO> tabelaEntidades) {
        this.tabela = tabelaEntidades;
    }

    public IModel<List<CicloVO>> getModelConsulta() {
        return modelConsulta;
    }

    public ConsultaCicloVO getConsulta() {
        return consulta;
    }

    public void setConsultaCicloVO(ConsultaCicloVO consulta) {
        this.consulta = consulta;
    }

    public void setModelConsulta(IModel<List<CicloVO>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

    public ProviderGenericoList<CicloVO> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<CicloVO> provider) {
        this.provider = provider;
    }

    public PainelArquivoParticipante getPainelArquivo() {
        return painelArquivo;
    }

    public void setPainelArquivo(PainelArquivoParticipante painelArquivo) {
        this.painelArquivo = painelArquivo;
    }

}
