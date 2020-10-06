package br.gov.bcb.sisaps.web.page.painelGerente;

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

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.GerenciarES;

public class PainelPendenciasGerente extends PainelSisAps {

    private static final String NOME_ES = "nomeES";

    public PainelPendenciasGerente(String id) {
        super(id);
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addTabela();
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        List<Coluna<CicloVO>> colunas = obterColunas();
        ProviderGenericoList<CicloVO> provider = criarProvider();
        addOrReplace(new Tabela<CicloVO>("tabela", cfg, colunas, provider, true));
    }
    
    private ProviderGenericoList<CicloVO> criarProvider() {
        ProviderGenericoList<CicloVO> provider =
                new ProviderGenericoList<CicloVO>(NOME_ES, SortOrder.ASCENDING, obterModel());
        return provider;
    }
    
    private IModel<List<CicloVO>> obterModel() {
        IModel<List<CicloVO>> modelConsulta = new AbstractReadOnlyModel<List<CicloVO>>() {
            @Override
            public List<CicloVO> getObject() {
                return CicloMediator.get().getPendenciasGerente();
            }
        };
        return modelConsulta;
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setMensagemVazio(Model.of("Nenhuma ES com pendências."));
        cfg.setExibirPaginador(true);
        cfg.setExibirTitulo(false);
        return cfg;
    }
    
    private List<Coluna<CicloVO>> obterColunas() {
        List<Coluna<CicloVO>> colunas = new LinkedList<Coluna<CicloVO>>();

        colunas.add(new Coluna<CicloVO>().setCabecalho("ES").setPropriedade(NOME_ES)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho("width:70%").setOrdenar(true));

        return colunas;
    }
    
    private class ComponenteCelulaLink implements IColunaComponente<CicloVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<CicloVO>> cellItem, String componentId,
                final IModel<CicloVO> rowModel) {

            Link<CicloVO> link = new Link<CicloVO>("link", rowModel) {
                @Override
                public void onClick() {
                    PerfilRisco perfilRiscoAtual = 
                            PerfilRiscoMediator.get().obterPerfilRiscoAtual(rowModel.getObject().getPk());
                    getPaginaAtual().avancarParaNovaPagina(new GerenciarES(rowModel.getObject().getPk(), perfilRiscoAtual));
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), NOME_ES));
            link.setMarkupId("link_Ciclo" + rowModel.getObject().getPk().toString());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

}
