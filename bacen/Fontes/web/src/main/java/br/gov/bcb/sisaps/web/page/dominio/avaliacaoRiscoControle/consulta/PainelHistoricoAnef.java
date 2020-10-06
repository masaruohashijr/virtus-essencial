package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

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
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaAnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.DetalharAQT;

public class PainelHistoricoAnef extends PainelSisAps {

    private static final String PROP_NOME_ES = "ciclo.entidadeSupervisionavel.nome";

    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    private final ConsultaAnaliseQuantitativaAQTVO consultaAnef;

    private Tabela<AnaliseQuantitativaAQTVO> tabela;

    private Configuracao cfg;

    private IModel<List<AnaliseQuantitativaAQTVO>> modelConsulta;
    
    public PainelHistoricoAnef(String id, ConsultaAnaliseQuantitativaAQTVO consultaAnef) {
        super(id);
        this.consultaAnef = consultaAnef;
        addTabela();
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AnaliseQuantitativaAQTVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<AnaliseQuantitativaAQTVO> providerGenericoList = criarProvider();
        tabela = new Tabela<AnaliseQuantitativaAQTVO>("tabelaHistoricoANEF", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        cfg = new Configuracao("idTitulo", "idDadosANEFs");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setTitulo(Model.of("ANEFs"));
        cfg.setExibirPaginador(true);
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private ProviderGenericoList<AnaliseQuantitativaAQTVO> criarProvider() {
        ProviderGenericoList<AnaliseQuantitativaAQTVO> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQTVO>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<AnaliseQuantitativaAQTVO>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<AnaliseQuantitativaAQTVO>>() {

            @Override
            public List<AnaliseQuantitativaAQTVO> getObject() {
                return  analiseQuantitativaAQTMediator.consultarHistoricoAQTfinal(consultaAnef);
            }
        };
        return modelConsulta;
    }

    private List<Coluna<AnaliseQuantitativaAQTVO>> obterColunas() {
        List<Coluna<AnaliseQuantitativaAQTVO>> colunas = new LinkedList<Coluna<AnaliseQuantitativaAQTVO>>();
        
        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("ES").setPropriedade(PROP_NOME_ES)
                .setOrdenar(true).setEstiloCabecalho("width:55%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Componente")
                .setPropriedade("parametroAQT.descricao").setOrdenar(true).setEstiloCabecalho("width:18%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Ação").setPropriedade("acao").setOrdenar(true)
                .setEstiloCabecalho("width:15%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Versão").setPropriedade("versao")
                .setComponente(new ComponenteCelulaLink()).setOrdenar(true).setEstiloCabecalho("width:12%"));
        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<AnaliseQuantitativaAQTVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AnaliseQuantitativaAQTVO>> cellItem, String componentId,
                final IModel<AnaliseQuantitativaAQTVO> rowModel) {

            Link<AnaliseQuantitativaAQTVO> link = new Link<AnaliseQuantitativaAQTVO>("link", rowModel) {
                @Override
                public void onClick() {
                    AnaliseQuantitativaAQT aqt =
                            AnaliseQuantitativaAQTMediator.get().buscar(rowModel.getObject().getPk());
                    getPaginaAtual().avancarParaNovaPagina(new DetalharAQT(aqt, true, true));
                }
            };
            link.setBody(new Model<String>(rowModel.getObject().getVersao()));
            link.setMarkupId(link.getId() + "_ANEF" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    public Tabela<AnaliseQuantitativaAQTVO> getTabela() {
        return tabela;
    }

    public void setTabela(Tabela<AnaliseQuantitativaAQTVO> tabela) {
        this.tabela = tabela;
    }

}
