package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;

public class PainelHistoricoArc extends PainelSisAps {

    private static final String WIDTH_10 = "width:10%";

    private static final String ESTADO_MODIFICADO = "estadoModificado";

    @SpringBean
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    private final ConsultaAvaliacaoRiscoControleVO consulta;

    private Tabela<AvaliacaoRiscoControleVO> tabela;

    private IModel<List<AvaliacaoRiscoControleVO>> modelConsulta;

    private Configuracao cfg;

    public PainelHistoricoArc(String id, ConsultaAvaliacaoRiscoControleVO consulta) {
        super(id);
        this.consulta = consulta;
        addTabela();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        cfg.setExibirPaginador(!modelConsulta.getObject().isEmpty());
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AvaliacaoRiscoControleVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<AvaliacaoRiscoControleVO> providerGenericoList = criarProvider();
        tabela = new Tabela<AvaliacaoRiscoControleVO>("tabelaHistoricoARC", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        cfg = new Configuracao("idTitulo", "idDadosARCs");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setTitulo(Model.of("ARCs"));
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private ProviderGenericoList<AvaliacaoRiscoControleVO> criarProvider() {
        ProviderGenericoList<AvaliacaoRiscoControleVO> provider =
                new ProviderGenericoList<AvaliacaoRiscoControleVO>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<AvaliacaoRiscoControleVO>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<AvaliacaoRiscoControleVO>>() {
            @Override
            public List<AvaliacaoRiscoControleVO> getObject() {
                return avaliacaoRiscoControleMediator.consultaHistoricoARC(consulta);
            }
        };
        return modelConsulta;
    }

    private List<Coluna<AvaliacaoRiscoControleVO>> obterColunas() {
        List<Coluna<AvaliacaoRiscoControleVO>> colunas = new LinkedList<Coluna<AvaliacaoRiscoControleVO>>();
        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("ES").setPropriedade("nomeEs")
                .setOrdenar(true).setEstiloCabecalho("width:35%"));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Atividade").setPropriedade("atividade.nome")
                .setComponente(new ComponenteNomeAtividade()).setOrdenar(true).setEstiloCabecalho("width:20%"));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Grupo")
                .setPropriedade("parametroGrupoRiscoControle.nomeAbreviado").setOrdenar(true)
                .setEstiloCabecalho("width:15%"));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("R/C").setPropriedade("tipo.abreviacao")
                .setOrdenar(true).setEstiloCabecalho("width:5%"));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Ação").setPropriedade("acao").setOrdenar(true)
                .setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<AvaliacaoRiscoControleVO>().setCabecalho("Versão").setPropriedade(ESTADO_MODIFICADO)
                .setOrdenar(true).setEstiloCabecalho("width:12%").setComponente(new ComponenteCelulaLink()));
        return colunas;
    }

    private class ComponenteNomeAtividade implements IColunaComponente<AvaliacaoRiscoControleVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AvaliacaoRiscoControleVO>> cellItem,
                final String componentId, final IModel<AvaliacaoRiscoControleVO> rowModel) {
            String nome = AtividadeMediator.get().retornarNomeAtividade(rowModel.getObject().getAtividade());
            return new Label(componentId, nome);
        }

    }

    private class ComponenteCelulaLink implements IColunaComponente<AvaliacaoRiscoControleVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AvaliacaoRiscoControleVO>> cellItem, String componentId,
                final IModel<AvaliacaoRiscoControleVO> rowModel) {

            Link<AvaliacaoRiscoControleVO> link = new Link<AvaliacaoRiscoControleVO>("link", rowModel) {
                @Override
                public void onClick() {

                    TituloTelaARCEnum titulo = null;
                    EstadoARCEnum estado = rowModel.getObject().getEstado();
                    if (AvaliacaoRiscoControleMediator.get().estadoAnaliseDelegada(estado)) {
                        titulo = TituloTelaARCEnum.PAGINA_DETALHE_DELEGADO;
                    } else if (AvaliacaoRiscoControleMediator.get().estadoEmAnalise(estado)) {
                        titulo = TituloTelaARCEnum.PAGINA_DETALHE_ANALISE;
                    } else if (AvaliacaoRiscoControleMediator.get().estadoConcluido(estado)) {
                        titulo = TituloTelaARCEnum.PAGINA_DETALHE_CONCLUIDO;
                    } else if (AvaliacaoRiscoControleMediator.get().estadoPreenchido(estado)) {
                        titulo = TituloTelaARCEnum.PAGINA_DETALHE_PREENCHIDO;
                    } else if (AvaliacaoRiscoControleMediator.get().estadoAnalisado(estado)) {
                        titulo = TituloTelaARCEnum.PAGINA_DETALHE_ANALISADO;
                    }
                    instanciarPaginaMeuHistorico(rowModel, titulo);
                }
            };
            link.setBody(new Model<String>(rowModel.getObject().getVersao()));
            link.setMarkupId(link.getId() + "_Arc" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    public Tabela<AvaliacaoRiscoControleVO> getTabela() {
        return tabela;
    }

    public void setTabela(Tabela<AvaliacaoRiscoControleVO> tabela) {
        this.tabela = tabela;
    }

    public IModel<List<AvaliacaoRiscoControleVO>> getModelConsulta() {
        return modelConsulta;
    }

    public void setModelConsulta(IModel<List<AvaliacaoRiscoControleVO>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

}
