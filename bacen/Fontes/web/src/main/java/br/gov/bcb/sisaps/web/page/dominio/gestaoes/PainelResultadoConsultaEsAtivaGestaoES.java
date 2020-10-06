package br.gov.bcb.sisaps.web.page.dominio.gestaoes;

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

import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;

public class PainelResultadoConsultaEsAtivaGestaoES extends PainelSisAps {
    private static final String WIDTH_5 = "width:5%";
    private static final String WIDTH_20 = "width:20%";
    private static final String NOME = "nome";
    private ConsultaEntidadeSupervisionavelVO consulta;
    private Tabela<EntidadeSupervisionavelVO> tabela;
    private ProviderGenericoList<EntidadeSupervisionavelVO> provider;
    private final ModalWindow modalAlteracao;

    public PainelResultadoConsultaEsAtivaGestaoES(String id, ConsultaEntidadeSupervisionavelVO consulta) {
        super(id);
        modalAlteracao = new ModalWindow("modalInclusaoCiclo");
        modalAlteracao.setResizable(false);
        modalAlteracao.setInitialHeight(270);
        modalAlteracao.setInitialWidth(750);
        addOrReplace(modalAlteracao);
        this.consulta = consulta;
        addTabela();
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<EntidadeSupervisionavelVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<EntidadeSupervisionavelVO> providerGenericoList = criarProvider();
        tabela = new Tabela<EntidadeSupervisionavelVO>("tabela", cfg, colunas, providerGenericoList, true);
        tabela.setOutputMarkupId(true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloEsAtivas", "idDadosEsAtivas");
        cfg.setTitulo(Model.of("ESs ativas"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirTitulo(true);
        cfg.setExibirPaginador(true);
        return cfg;
    }

    private ProviderGenericoList<EntidadeSupervisionavelVO> criarProvider() {
        provider = new ProviderGenericoList<EntidadeSupervisionavelVO>(NOME, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<EntidadeSupervisionavelVO>> obterModel() {
        IModel<List<EntidadeSupervisionavelVO>> modelConsulta =
                new AbstractReadOnlyModel<List<EntidadeSupervisionavelVO>>() {
                    @Override
                    public List<EntidadeSupervisionavelVO> getObject() {
                        consulta.setPaginada(false);
                        consulta.setAdministrador(true);
                        consulta.setPossuiPrioridade(true);
                        return EntidadeSupervisionavelMediator.get().consultarEntidadesPerfilConsulta(consulta);
                    }
                };
        return modelConsulta;
    }

    private List<Coluna<EntidadeSupervisionavelVO>> obterColunas() {
        List<Coluna<EntidadeSupervisionavelVO>> colunas = new LinkedList<Coluna<EntidadeSupervisionavelVO>>();
        
        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("ES").setPropriedade(NOME)
                .setOrdenar(true).setEstiloCabecalho("width:28%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Equipe").setPropriedade("localizacao")
                .setOrdenar(true).setEstiloCabecalho("width:25%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Supervisor titular")
                .setPropriedade("nomeSupervisor").setOrdenar(true).setEstiloCabecalho(WIDTH_20));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Prioridade")
                .setPropriedadeTela("prioridade.descricao").setPropriedade("prioridade.codigo").setOrdenar(true)
                .setEstiloCabecalho("width:8%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Corec previsto").setOrdenar(true)
                .setPropriedade("corecPrevisto").setPropriedadeTela("dataPrevisaoFormatada")
                .setEstiloCabecalho("width:10%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Ciclo").setPropriedade("ciclos")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_5));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Ação").setPropriedade(NOME)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho(WIDTH_5));

        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<EntidadeSupervisionavelVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<EntidadeSupervisionavelVO>> cellItem,
                final String componentId, final IModel<EntidadeSupervisionavelVO> rowModel) {

            Fragment fragment =
                    new Fragment(componentId, "frangmentLink", PainelResultadoConsultaEsAtivaGestaoES.this) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    AjaxSubmitLink link = criarLink(rowModel);
                    addOrReplace(link);
                }
            };

            return fragment;
        }
    }
    
    private AjaxSubmitLink criarLink(final IModel<EntidadeSupervisionavelVO> rowModel) {
        AjaxSubmitLink link = new AjaxSubmitLink("link") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add(new Image("alterar", ConstantesImagens.IMG_ATUALIZAR));
                setMarkupId(getId() + "ENS" + rowModel.getObject().getPk());
                setOutputMarkupId(true);
            }
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalAlteracao.setContent(new PainelGestaoES(modalAlteracao, rowModel.getObject()));
                modalAlteracao.setOutputMarkupId(true);
                modalAlteracao.show(target);
            }
        };
        return link;
    }

    public Tabela<EntidadeSupervisionavelVO> getTabela() {
        return tabela;
    }

    public void setTabela(Tabela<EntidadeSupervisionavelVO> tabela) {
        this.tabela = tabela;
    }

    public ProviderGenericoList<EntidadeSupervisionavelVO> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<EntidadeSupervisionavelVO> provider) {
        this.provider = provider;
    }

    public ConsultaEntidadeSupervisionavelVO getConsulta() {
        return consulta;
    }

    public void setConsultaEntidadeSupervisionavelVO(ConsultaEntidadeSupervisionavelVO consulta) {
        this.consulta = consulta;
    }

}
