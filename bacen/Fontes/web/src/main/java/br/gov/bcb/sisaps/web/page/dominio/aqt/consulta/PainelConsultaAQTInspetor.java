package br.gov.bcb.sisaps.web.page.dominio.aqt.consulta;

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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;

public class PainelConsultaAQTInspetor extends PainelSisAps {

    private static final String ID_TITULO = "idTitulo";

    private static final String PROP_NOME_ES = "ciclo.entidadeSupervisionavel.nome";

    private static final String ESTADO_DESCRICAO = "estado.descricao";

    private final String titulo;

    private final String id;

    private final boolean isDesignar;

    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    public PainelConsultaAQTInspetor(String id, String titulo, boolean isDesignar) {
        super(id);
        this.id = id;
        this.titulo = titulo;
        this.isDesignar = isDesignar;
        addTitulo();
        addTabela();
    }

    private void addTitulo() {
        Label tituloPainel = new Label("idTituloAQTs", titulo);
        tituloPainel.setOutputMarkupId(true);
        tituloPainel.setMarkupId(id + ID_TITULO);
        add(tituloPainel);
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AnaliseQuantitativaAQT>> colunas = obterColunas();
        // provider
        ProviderGenericoList<AnaliseQuantitativaAQT> providerGenericoList;
        if (isDesignar) {
            providerGenericoList = criarProviderDesignacao();
        } else {
            providerGenericoList = criarProviderDelegacao();
        }
        // tabela
        Tabela<AnaliseQuantitativaAQT> tabela =
                new Tabela<AnaliseQuantitativaAQT>("tabela", cfg, colunas, providerGenericoList, false);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao(ID_TITULO, id + "idDados");
        cfg.setTitulo(Model.of(titulo));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        return cfg;
    }

    private ProviderGenericoList<AnaliseQuantitativaAQT> criarProviderDesignacao() {
        ProviderGenericoList<AnaliseQuantitativaAQT> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQT>(PROP_NOME_ES, SortOrder.ASCENDING,
                        obterModelDesignacao());
        return provider;
    }

    private ProviderGenericoList<AnaliseQuantitativaAQT> criarProviderDelegacao() {
        ProviderGenericoList<AnaliseQuantitativaAQT> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQT>(PROP_NOME_ES, SortOrder.ASCENDING,
                        obterModelDelegacao());
        return provider;
    }

    private IModel<List<AnaliseQuantitativaAQT>> obterModelDesignacao() {
        IModel<List<AnaliseQuantitativaAQT>> modelConsulta = new AbstractReadOnlyModel<List<AnaliseQuantitativaAQT>>() {
            @Override
            public List<AnaliseQuantitativaAQT> getObject() {
                return analiseQuantitativaAQTMediator.consultaPainelAQTDesignados();
            }
        };
        return modelConsulta;
    }

    private IModel<List<AnaliseQuantitativaAQT>> obterModelDelegacao() {
        IModel<List<AnaliseQuantitativaAQT>> modelConsulta = new AbstractReadOnlyModel<List<AnaliseQuantitativaAQT>>() {
            @Override
            public List<AnaliseQuantitativaAQT> getObject() {
                return analiseQuantitativaAQTMediator.consultaPainelAQTDelegados();
            }
        };
        return modelConsulta;
    }

    private List<Coluna<AnaliseQuantitativaAQT>> obterColunas() {
        List<Coluna<AnaliseQuantitativaAQT>> colunas = new LinkedList<Coluna<AnaliseQuantitativaAQT>>();
        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("ES").setPropriedade(PROP_NOME_ES)
                .setOrdenar(true).setEstiloCabecalho("width:45%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Componente")
                .setPropriedade("parametroAQT.descricao").setOrdenar(true).setEstiloCabecalho("width:35%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Estado").setPropriedade(ESTADO_DESCRICAO)
                .setOrdenar(true).setEstiloCabecalho("width:10%").setComponente(new ComponenteCelulaLink()));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<AnaliseQuantitativaAQT> {

        @Override
        public Component obterComponente(Item<ICellPopulator<AnaliseQuantitativaAQT>> cellItem, String componentId,
                final IModel<AnaliseQuantitativaAQT> rowModel) {

            Link<AnaliseQuantitativaAQT> link = new Link<AnaliseQuantitativaAQT>("link", rowModel) {
                @Override
                public void onClick() {
                    TituloTelaAnefEnum titulo =
                            UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(rowModel, getPaginaAtual()
                                    .getPerfilPorPagina(), true);
                    instanciarPaginaDestino(rowModel.getObject(), titulo, true);
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), ESTADO_DESCRICAO));
            link.setMarkupId(link.getId() + "_AQT" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

}
