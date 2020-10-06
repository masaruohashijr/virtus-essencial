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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.ArcResumidoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeARC;

public class PainelConsultaARCInspetor extends PainelSisAps {

    private static final String MATRIZ_VIGENTE_CICLO_ENTIDADE_SUPERVISIONAVEL_NOME = "nomeES";

    private static final String ID_TITULO = "idTitulo";

    private static final String WIDTH_30 = "width:30%";

    private static final String ESTADO_DESCRICAO = "estado.descricao";

    private final String titulo;

    private final String id;

    private final boolean isDesignar;

    @SpringBean
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public PainelConsultaARCInspetor(String id, String titulo, boolean isDesignar) {
        super(id);
        this.id = id;
        this.titulo = titulo;
        this.isDesignar = isDesignar;
        addTitulo();
        addTabela();
    }

    private void addTitulo() {
        Label tituloPainel = new Label("idTituloArcs", titulo);
        tituloPainel.setOutputMarkupId(true);
        tituloPainel.setMarkupId(id + ID_TITULO);
        add(tituloPainel);
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<ArcResumidoVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<ArcResumidoVO> providerGenericoList;
        if (isDesignar) {
            providerGenericoList = criarProviderDesignacao();
        } else {
            providerGenericoList = criarProviderDelegacao();
        }
        // tabela
        Tabela<ArcResumidoVO> tabela =
                new Tabela<ArcResumidoVO>("tabela", cfg, colunas, providerGenericoList, false);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao(ID_TITULO, id + "idDados");
        cfg.setTitulo(Model.of(titulo));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        return cfg;
    }

    private ProviderGenericoList<ArcResumidoVO> criarProviderDesignacao() {
        ProviderGenericoList<ArcResumidoVO> provider = new ProviderGenericoList<ArcResumidoVO>(
                        MATRIZ_VIGENTE_CICLO_ENTIDADE_SUPERVISIONAVEL_NOME, SortOrder.ASCENDING,
                        obterModelDesignacao());
        return provider;
    }

    private ProviderGenericoList<ArcResumidoVO> criarProviderDelegacao() {
        ProviderGenericoList<ArcResumidoVO> provider = new ProviderGenericoList<ArcResumidoVO>(
                        MATRIZ_VIGENTE_CICLO_ENTIDADE_SUPERVISIONAVEL_NOME, SortOrder.ASCENDING,
                        obterModelDelegacao());
        return provider;
    }

    private IModel<List<ArcResumidoVO>> obterModelDesignacao() {
        IModel<List<ArcResumidoVO>> modelConsulta = new AbstractReadOnlyModel<List<ArcResumidoVO>>() {
                    @Override
            public List<ArcResumidoVO> getObject() {
                        return avaliacaoRiscoControleMediator.consultaPainelArcDesignados();
                    }
                };
        return modelConsulta;
    }

    private IModel<List<ArcResumidoVO>> obterModelDelegacao() {
        IModel<List<ArcResumidoVO>> modelConsulta = new AbstractReadOnlyModel<List<ArcResumidoVO>>() {
                    @Override
            public List<ArcResumidoVO> getObject() {
                        return avaliacaoRiscoControleMediator.consultaPainelArcDelegados();
                    }
                };
        return modelConsulta;
    }

    private List<Coluna<ArcResumidoVO>> obterColunas() {
        List<Coluna<ArcResumidoVO>> colunas = new LinkedList<Coluna<ArcResumidoVO>>();
        colunas.add(new Coluna<ArcResumidoVO>().setCabecalho("ES").setPropriedade(
                MATRIZ_VIGENTE_CICLO_ENTIDADE_SUPERVISIONAVEL_NOME)
                .setOrdenar(true).setEstiloCabecalho(WIDTH_30));
        colunas.add(new Coluna<ArcResumidoVO>().setCabecalho("Atividade").setPropriedade("nomeAtividade")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_30));
        colunas.add(new Coluna<ArcResumidoVO>().setCabecalho("Grupo")
                .setPropriedade("nomeGrupo").setOrdenar(true)
                .setEstiloCabecalho("width:23%"));
        colunas.add(new Coluna<ArcResumidoVO>().setCabecalho("R/C")
                .setPropriedade("tipo.abreviacao").setOrdenar(true)
                .setEstiloCabecalho("width:5%"));
        colunas.add(new Coluna<ArcResumidoVO>().setCabecalho("Estado").setPropriedade(ESTADO_DESCRICAO)
                .setOrdenar(true).setEstiloCabecalho("width:10%").setComponente(new ComponenteCelulaLink()));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<ArcResumidoVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<ArcResumidoVO>> cellItem, String componentId,
                final IModel<ArcResumidoVO> rowModel) {

            Link<ArcResumidoVO> link = new Link<ArcResumidoVO>("link", rowModel) {
                @Override
                public void onClick() {
                    TituloTelaARCEnum titulo =
                            UtilNavegabilidadeARC.avancarParaNovaPaginaArcResumido(rowModel,
                                    getPaginaAtual().getPerfilPorPagina(), true);
                    instanciarPaginaDestinoARCResumido(rowModel, titulo, false);
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), ESTADO_DESCRICAO));
            link.setMarkupId(link.getId() + "_Arc" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

}
