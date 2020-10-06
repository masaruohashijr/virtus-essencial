package br.gov.bcb.sisaps.web.page.painelComite;

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

import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;

public class PainelCicloCorecEPosCorec extends PainelSisAps {

    private static final String DATA_PREVISAO_COREC = "dataPrevisaoCorec";

    private static final String PROP_ENTIDADE_SUPERVISIONAVEL_NOME = "entidadeSupervisionavel.nome";

    private static final String WIDTH_10 = "width:10%;";
    protected CicloVO cicloVOSelecionado = new CicloVO();

    private Tabela<CicloVO> tabela;
    private boolean isCorec;

    public PainelCicloCorecEPosCorec(String id, boolean isCorec) {
        super(id);
        this.isCorec = isCorec;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponents();
    }

    private void addComponents() {
        addListaCiclo();
        addOrReplace(new Label("titulo", isCorec ? "Ciclos em estado Corec" : "Ciclos em estado Pós-Corec"));

    }

    private void addListaCiclo() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<CicloVO>> colunas = obterColunas();

        // tabela
        tabela = new Tabela<CicloVO>("tabela", cfg, colunas, criarProvider(), true);
        addOrReplace(tabela);
    }

    private ProviderGenericoList<CicloVO> criarProvider() {
        ProviderGenericoList<CicloVO> provider =
                new ProviderGenericoList<CicloVO>(DATA_PREVISAO_COREC, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<CicloVO>> obterModel() {
        IModel<List<CicloVO>> modelConsulta = new AbstractReadOnlyModel<List<CicloVO>>() {
            @Override
            public List<CicloVO> getObject() {
                if (isCorec) {
                    return CicloMediator.get().consultarCicloCorec();
                } else {
                    return CicloMediator.get().consultarCicloPosCorec();
                }
            }
        };
        return modelConsulta;
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        return cfg;

    }

    private List<Coluna<CicloVO>> obterColunas() {
        List<Coluna<CicloVO>> colunas = new LinkedList<Coluna<CicloVO>>();

        colunas.add(new Coluna<CicloVO>().setCabecalho("ES").setPropriedade(PROP_ENTIDADE_SUPERVISIONAVEL_NOME)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho("width:30%").setOrdenar(true));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Supervisor titular")
                .setPropriedade("nomeSupervisorCorec").setOrdenar(true)
                .setEstiloCabecalho("width:20%"));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Entrada Corec").setPropriedade("dataInicioCorec")
                .setPropriedadeTela("dataInicioCorecFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<CicloVO>().setCabecalho("Corec").setPropriedade(DATA_PREVISAO_COREC)
                .setPropriedadeTela("dataPrevisaoFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_10));

        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<CicloVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<CicloVO>> cellItem, String componentId,
                final IModel<CicloVO> rowModel) {

            Link<CicloVO> link = new Link<CicloVO>("link", rowModel) {
                @Override
                public void onClick() {
                    getPaginaAtual().avancarParaNovaPagina(
                            isCorec ? new GestaoCorecPage(rowModel.getObject()) : new GestaoPosCorecPage(rowModel
                                    .getObject()));
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), PROP_ENTIDADE_SUPERVISIONAVEL_NOME));
            link.setMarkupId("link_ES" + rowModel.getObject().getEntidadeSupervisionavel().getPk().toString());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
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
