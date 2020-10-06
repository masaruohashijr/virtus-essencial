package br.gov.bcb.sisaps.web.page.painelConsulta;

import java.util.ArrayList;
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

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;

public class PainelResultadoConsultaOutrasES extends PainelSisAps {
    private static final String NOME = "entidadeSupervisionavel.nome";
    private final ConsultaCicloVO consulta;

    public PainelResultadoConsultaOutrasES(String id, ConsultaCicloVO consulta) {
        super(id);
        this.consulta = consulta;
        addTabela();

    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<CicloVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<CicloVO> providerGenericoList = criarProvider();
        // tabela
        Tabela<CicloVO> tabela = new Tabela<CicloVO>("tabela", cfg, colunas, providerGenericoList, false);
        tabela.setOutputMarkupId(true);

        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloOutrasES", "idDadosOutrasEs");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));

        cfg.setExibirTitulo(false);
        cfg.setExibirPaginador(false);
        return cfg;
    }

    private ProviderGenericoList<CicloVO> criarProvider() {
        ProviderGenericoList<CicloVO> provider =
                new ProviderGenericoList<CicloVO>(NOME, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<CicloVO>> obterModel() {
        IModel<List<CicloVO>> modelConsulta = new AbstractReadOnlyModel<List<CicloVO>>() {
            @Override
            public List<CicloVO> getObject() {
                consulta.setPaginada(false);
                List<EstadoCicloEnum> estados = new ArrayList<EstadoCicloEnum>();
                estados.add(EstadoCicloEnum.ENCERRADO);
                consulta.setEstados(estados);
                return CicloMediator.get().consultarES(consulta);
            }
        };
        return modelConsulta;
    }

    private List<Coluna<CicloVO>> obterColunas() {
        List<Coluna<CicloVO>> colunas = new LinkedList<Coluna<CicloVO>>();

        colunas.add(new Coluna<CicloVO>().setCabecalho("ES").setPropriedade(NOME).setComponente(new ComponenteLink())
                .setOrdenar(true).setEstiloCabecalho("width:30%"));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Encerramento").setPropriedade("estadoCiclo.dataTermino")
                .setPropriedadeTela("estadoCiclo.dataTerminoFormatada").setOrdenar(true)
                .setEstiloCabecalho("width:15%"));

        return colunas;
    }

    private class ComponenteLink implements IColunaComponente<CicloVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<CicloVO>> cellItem, String componentId,
                final IModel<CicloVO> rowModel) {

            Link<CicloVO> link = new Link<CicloVO>("link", rowModel) {
                @Override
                public void onClick() {
                    getPaginaAtual().avancarParaNovaPagina(new PerfilDeRiscoPage(rowModel.getObject().getPk()));
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), NOME));
            link.setMarkupId("linkPerfil" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

}
