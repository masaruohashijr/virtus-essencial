package br.gov.bcb.sisaps.web.page.painelGerente;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderOrdenadoPaginado;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

public class PainelCicloSubLocalizao extends PainelSisAps {

    private static final String PROP_ENTIDADE_SUPERVISIONAVEL_NOME = "entidadeSupervisionavel.nome";

    private static final String WIDTH_10 = "width:10%;";
    protected CicloVO cicloVOSelecionado = new CicloVO();

    @SpringBean
    private CicloMediator cicloMediator;

    private ConsultaCicloVO consulta;

    private Tabela<CicloVO> tabela;

    private final PerfilAcessoEnum perfilEnum;
    private final ComponenteOrganizacionalVO componente;

    public PainelCicloSubLocalizao(String id, ComponenteOrganizacionalVO componente) {
        super(id);
        this.componente = componente;
        this.perfilEnum = PerfilAcessoEnum.GERENTE;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponents();
    }

    private void addComponents() {
        addListaCiclo();

    }

    private void addListaCiclo() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<CicloVO>> colunas = obterColunas();
        // model da consulta
        IModel<ConsultaCicloVO> modelConsulta = obterModelConsulta();
        // provider
        ProviderOrdenadoPaginado<CicloVO, Integer, ConsultaCicloVO> provider = obterProvider(modelConsulta);
        // tabela
        tabela = new Tabela<CicloVO>("tabela", cfg, colunas, provider, false);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        ConsultaChefia consulta = new ConsultaChefia();
        consulta.setComponenteOrganizacionalRotulo(componente.getRotulo());
        consulta.setDataBase(new Date());
        ChefiaVO chefe = BcPessoaAdapter.get().buscarChefia(consulta);
        String nome = "";
        if (chefe != null && chefe.getChefeEmExercicio() != null) {
            nome = chefe.getChefeEmExercicio().getNome();
        }
        cfg.setTitulo(Model.of(componente.getSigla() + " - " + nome));
        cfg.setMensagemVazio(Model.of("Nenhuma ES da equipe com ciclo iniciado"));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private List<Coluna<CicloVO>> obterColunas() {
        List<Coluna<CicloVO>> colunas = new LinkedList<Coluna<CicloVO>>();

        colunas.add(new Coluna<CicloVO>().setCabecalho("ES").setPropriedade(PROP_ENTIDADE_SUPERVISIONAVEL_NOME)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho("width:70%").setOrdenar(true));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Início").setPropriedade("dataInicio")
                .setPropriedadeTela("dataInicioFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Corec").setPropriedade("dataPrevisaoCorec")
                .setPropriedadeTela("dataPrevisaoFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Estado").setPropriedade("estadoCiclo.estado")
                .setPropriedadeTela("estadoCiclo.estado.descricao").setOrdenar(true).setEstiloCabecalho(WIDTH_10));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<CicloVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<CicloVO>> cellItem, String componentId,
                final IModel<CicloVO> rowModel) {

            Link<CicloVO> link = new Link<CicloVO>("link", rowModel) {
                @Override
                public void onClick() {
                    getPaginaAtual().avancarParaNovaPagina(new PerfilDeRiscoPage(rowModel.getObject().getPk()));
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), PROP_ENTIDADE_SUPERVISIONAVEL_NOME));
            link.setMarkupId("link_ES" + rowModel.getObject().getEntidadeSupervisionavel().getPk().toString());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    private IModel<ConsultaCicloVO> obterModelConsulta() {
        consulta = new ConsultaCicloVO();
        preencherFiltro(consulta);
        IModel<ConsultaCicloVO> modelConsulta = new AbstractReadOnlyModel<ConsultaCicloVO>() {
            @Override
            public ConsultaCicloVO getObject() {
                return consulta;
            }
        };
        return modelConsulta;
    }

    private void preencherFiltro(ConsultaCicloVO consulta) {
        consulta.setPerfil(perfilEnum);
        consulta.setEstados(Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC));
        consulta.setRotuloLocalizacao(componente.getRotulo());
    }

    private ProviderOrdenadoPaginado<CicloVO, Integer, ConsultaCicloVO> obterProvider(
            IModel<ConsultaCicloVO> modelConsulta) {
        ProviderOrdenadoPaginado<CicloVO, Integer, ConsultaCicloVO> provider =
                new ProviderOrdenadoPaginado<CicloVO, Integer, ConsultaCicloVO>(PROP_ENTIDADE_SUPERVISIONAVEL_NOME,
                        SortOrder.ASCENDING, cicloMediator, modelConsulta);
        return provider;
    }

    public CicloVO getCicloVOSelecionado() {
        return cicloVOSelecionado;
    }

    public void setCicloVOSelecionado(CicloVO cicloVOSelecionado) {
        this.cicloVOSelecionado = cicloVOSelecionado;
    }

    public Tabela<CicloVO> getTabela() {
        return tabela;
    }

}
