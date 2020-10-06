package br.gov.bcb.sisaps.web.page.dominio.aqt.consulta;

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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.aqt.GerenciarNotaSinteseAQTPage;

public class PainelSintesesAQTRevisao extends PainelSisAps {

    private static final String PROP_NOME_PARAMETRO_AQT = "parametroAQT.descricao";
    private static final String PROP_NOME_ENTIDADE_SUPERVISIONAVEL = "ciclo.entidadeSupervisionavel.nome";

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    public PainelSintesesAQTRevisao(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addTabela();
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<AnaliseQuantitativaAQT>> colunas = obterColunas();
        // provider
        ProviderGenericoList<AnaliseQuantitativaAQT> providerGenericoList = criarProvider();
        // tabela
        Tabela<AnaliseQuantitativaAQT> tabela =
                new Tabela<AnaliseQuantitativaAQT>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloSinteseAQT", "idDadosSintesesAQT");
        cfg.setTitulo(Model.of("Sínteses de ANEF para revisão"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro2"));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private List<Coluna<AnaliseQuantitativaAQT>> obterColunas() {
        List<Coluna<AnaliseQuantitativaAQT>> colunas = new LinkedList<Coluna<AnaliseQuantitativaAQT>>();
        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("ES")
                .setPropriedade(PROP_NOME_ENTIDADE_SUPERVISIONAVEL).setOrdenar(true).setEstiloCabecalho("width:35%"));
        colunas.add(new Coluna<AnaliseQuantitativaAQT>().setCabecalho("Síntese")
                .setPropriedade(PROP_NOME_PARAMETRO_AQT).setComponente(new ComponenteCelulaLink())
                .setEstiloCabecalho("width:25%").setOrdenar(true));

        return colunas;
    }

    private ProviderGenericoList<AnaliseQuantitativaAQT> criarProvider() {
        ProviderGenericoList<AnaliseQuantitativaAQT> provider =
                new ProviderGenericoList<AnaliseQuantitativaAQT>(PROP_NOME_ENTIDADE_SUPERVISIONAVEL,
                        SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<AnaliseQuantitativaAQT>> obterModel() {
        IModel<List<AnaliseQuantitativaAQT>> modelConsulta = new AbstractReadOnlyModel<List<AnaliseQuantitativaAQT>>() {
            @Override
            public List<AnaliseQuantitativaAQT> getObject() {
                return AnaliseQuantitativaAQTMediator.get().consultaPainelAQTAnalisados();
            }
        };
        return modelConsulta;
    }

    private class ComponenteCelulaLink implements IColunaComponente<AnaliseQuantitativaAQT> {
        @Override
        public Component obterComponente(Item<ICellPopulator<AnaliseQuantitativaAQT>> cellItem, String componentId,
                final IModel<AnaliseQuantitativaAQT> rowModel) {
            final AnaliseQuantitativaAQT analiseQuantitativaAQT = rowModel.getObject();
            Link<AnaliseQuantitativaAQT> link = new Link<AnaliseQuantitativaAQT>("link", rowModel) {
                @Override
                public void onClick() {
                    avancarParaNovaPagina(analiseQuantitativaAQT);
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), PROP_NOME_PARAMETRO_AQT));
            link.setMarkupId(link.getId() + "Sintese_Parametro"
                    + analiseQuantitativaAQT.getParametroAQT().getDescricao() + "_ES"
                    + analiseQuantitativaAQT.getCiclo().getEntidadeSupervisionavel().getNome());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    private void avancarParaNovaPagina(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        getPaginaAtual().avancarParaNovaPagina(
                new GerenciarNotaSinteseAQTPage(perfilRiscoMediator.obterPerfilRiscoAtual(analiseQuantitativaAQT
                        .getCiclo().getPk()), obterNomePainelFocus(analiseQuantitativaAQT)));
    }

    private String obterNomePainelFocus(AnaliseQuantitativaAQT analiseQuantitativaAQT) {

        return "idTituloSintese"
                + SisapsUtil.criarMarkupId(analiseQuantitativaAQT.getParametroAQT().getDescricao());
    }

}
