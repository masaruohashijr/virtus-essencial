package br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt;

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

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
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

public class PainelAnefAQT extends PainelSisAps {

    private static final String ESTADO_DESCRICAO = "estado.descricao";

    private static final String PARAMETRO_AQT_DESCRICAO = "parametroAQT.descricao";

    private static final String WIDTH_40 = "width:40%";

    @SpringBean
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    private final Ciclo ciclo;

    private Tabela<AnaliseQuantitativaAQTVO> tabela;

    public PainelAnefAQT(String id, Ciclo ciclo) {
        super(id);
        this.ciclo = ciclo;
        setMarkupId(id);
        addListaAQT();
    }

    private void addListaAQT() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AnaliseQuantitativaAQTVO>> colunas = obterColunas();

        // tabela
        tabela = new Tabela<AnaliseQuantitativaAQTVO>("lista", cfg, colunas, criarProvider(), true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        return cfg;
    }

    private ProviderGenericoList<AnaliseQuantitativaAQTVO> criarProvider() {
        ProviderGenericoList<AnaliseQuantitativaAQTVO> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQTVO>(ESTADO_DESCRICAO, SortOrder.ASCENDING,
                        obterModel());
        return provider;
    }

    private IModel<List<AnaliseQuantitativaAQTVO>> obterModel() {
        IModel<List<AnaliseQuantitativaAQTVO>> modelConsulta =
                new AbstractReadOnlyModel<List<AnaliseQuantitativaAQTVO>>() {
                    @Override
                    public List<AnaliseQuantitativaAQTVO> getObject() {
                        return analiseQuantitativaAQTMediator.listarANEFsResponsaveis(ciclo);
                    }
                };
        return modelConsulta;
    }

    private List<Coluna<AnaliseQuantitativaAQTVO>> obterColunas() {
        List<Coluna<AnaliseQuantitativaAQTVO>> colunas = new LinkedList<Coluna<AnaliseQuantitativaAQTVO>>();

        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Componente")
                .setPropriedade(PARAMETRO_AQT_DESCRICAO).setEstiloCabecalho(WIDTH_40).setOrdenar(true));

        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Estado").setPropriedade(ESTADO_DESCRICAO)
                .setOrdenar(true).setEstiloCabecalho("width:20%").setComponente(new ComponenteCelulaLink()));

        colunas.add(new Coluna<AnaliseQuantitativaAQTVO>().setCabecalho("Responsável").setOrdenar(true)
                .setEstiloCabecalho(WIDTH_40).setPropriedade("responsavel"));

        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<AnaliseQuantitativaAQTVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AnaliseQuantitativaAQTVO>> cellItem, String componentId,
                final IModel<AnaliseQuantitativaAQTVO> rowModel) {

            Link<AnaliseQuantitativaAQTVO> link = new Link<AnaliseQuantitativaAQTVO>("link", rowModel) {
                @Override
                public void onClick() {
                    getPaginaAtual().avancarParaNovaPagina(
                            new DetalharAQT(analiseQuantitativaAQTMediator.buscar(rowModel.getObject().getPk()), true));

                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), ESTADO_DESCRICAO));
            link.setMarkupId(link.getId() + "_AQT" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    public Ciclo getCiclo() {
        return ciclo;
    }
}
