package br.gov.bcb.sisaps.web.page.painelAdministrador;

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

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso.GestaoPerfilAcessoPage;

public class PainelConsultaPerfilDeRiscoAdministrador extends PainelSisAps {

    private static final String DESCRICAO = "descricao";

    public PainelConsultaPerfilDeRiscoAdministrador(String id) {
        super(id);

        addTabela();

    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<PerfilAcessoEnum>> colunas = obterColunas();
        // provider
        ProviderGenericoList<PerfilAcessoEnum> providerGenericoList = criarProvider();
        // tabela
        Tabela<PerfilAcessoEnum> tabela =
                new Tabela<PerfilAcessoEnum>("tabela", cfg, colunas, providerGenericoList, false);
        tabela.setOutputMarkupId(true);

        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloPerfilAcesso", "idDadosPerfilAcesso");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirTitulo(false);
        cfg.setExibirPaginador(false);
        return cfg;
    }

    private ProviderGenericoList<PerfilAcessoEnum> criarProvider() {
        ProviderGenericoList<PerfilAcessoEnum> provider =
                new ProviderGenericoList<PerfilAcessoEnum>(DESCRICAO, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<PerfilAcessoEnum>> obterModel() {
        IModel<List<PerfilAcessoEnum>> modelConsulta = new AbstractReadOnlyModel<List<PerfilAcessoEnum>>() {
            @Override
            public List<PerfilAcessoEnum> getObject() {
                return PerfilAcessoEnum.LIST;
            }
        };
        return modelConsulta;
    }

    private List<Coluna<PerfilAcessoEnum>> obterColunas() {
        List<Coluna<PerfilAcessoEnum>> colunas = new LinkedList<Coluna<PerfilAcessoEnum>>();

        colunas.add(new Coluna<PerfilAcessoEnum>().setCabecalho("Perfil").setPropriedade(DESCRICAO)
                .setComponente(new ComponenteLink()).setOrdenar(true).setEstiloCabecalho("width:100%"));

        return colunas;
    }

    private class ComponenteLink implements IColunaComponente<PerfilAcessoEnum> {
        @Override
        public Component obterComponente(Item<ICellPopulator<PerfilAcessoEnum>> cellItem, String componentId,
                final IModel<PerfilAcessoEnum> rowModel) {

            Link<PerfilAcessoEnum> link = new Link<PerfilAcessoEnum>("link", rowModel) {
                @Override
                public void onClick() {
                    getPaginaAtual().avancarParaNovaPagina(new GestaoPerfilAcessoPage(rowModel.getObject()));
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), DESCRICAO));
            link.setMarkupId("linkPerfil" + rowModel.getObject().getCodigo());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

}
