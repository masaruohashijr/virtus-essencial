package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.PainelIniciarCiclo;

public class PainelConsultaNovasESs extends PainelSisAps {
    private static final String WIDTH_75 = "width:75%";
    private static final String PROP_NOME_ES = "nome";
    private String linkInicio = "Iniciar ciclo";
    private final ModalWindow modalInclusaoCiclo;
    private Tabela<EntidadeSupervisionavelVO> tabela;
    private ProviderGenericoList<EntidadeSupervisionavelVO> provider;

    public PainelConsultaNovasESs(String id) {
        super(id);
        modalInclusaoCiclo = new ModalWindow("modalInclusaoCiclo");
        modalInclusaoCiclo.setResizable(false);
        modalInclusaoCiclo.setInitialHeight(280);
        modalInclusaoCiclo.setInitialWidth(650);
        addOrReplace(modalInclusaoCiclo);
        addTabela();
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<EntidadeSupervisionavelVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<EntidadeSupervisionavelVO> providerGenericoList = criarProvider();
        // tabela
        tabela = new Tabela<EntidadeSupervisionavelVO>("tabela", cfg, colunas, providerGenericoList, false);
        addOrReplace(tabela);

    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloNovaES", "idDadosNovaES");
        cfg.setTitulo(Model.of("Novas ESs"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private ProviderGenericoList<EntidadeSupervisionavelVO> criarProvider() {
        provider = new ProviderGenericoList<EntidadeSupervisionavelVO>(PROP_NOME_ES, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<EntidadeSupervisionavelVO>> obterModel() {
        IModel<List<EntidadeSupervisionavelVO>> moCons = new AbstractReadOnlyModel<List<EntidadeSupervisionavelVO>>() {
            @Override
            public List<EntidadeSupervisionavelVO> getObject() {
                return EntidadeSupervisionavelMediator.get().buscarEntidadeSemCiclo();
            }
        };
        return moCons;
    }

    private List<Coluna<EntidadeSupervisionavelVO>> obterColunas() {
        List<Coluna<EntidadeSupervisionavelVO>> colunas = new LinkedList<Coluna<EntidadeSupervisionavelVO>>();

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("ES").setPropriedade(PROP_NOME_ES)
                .setOrdenar(true).setEstiloCabecalho(WIDTH_75));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Ação").setPropriedade(PROP_NOME_ES)
                .setComponente(new ComponenteCelulaLink()).setEstiloCabecalho(WIDTH_75).setOrdenar(false));

        return colunas;
    }

    private class ComponenteCelulaLink implements IColunaComponente<EntidadeSupervisionavelVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<EntidadeSupervisionavelVO>> cellItem,
                final String componentId, final IModel<EntidadeSupervisionavelVO> rowModel) {

            AjaxSubmitLink link = new AjaxSubmitLink("link") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    modalInclusaoCiclo.setContent(new PainelIniciarCiclo(modalInclusaoCiclo,
                            EntidadeSupervisionavelMediator.get().loadPK(rowModel.getObject().getPk())));
                    modalInclusaoCiclo.setOutputMarkupId(true);
                    modalInclusaoCiclo.show(target);
                }
            };
            link.setBody(new PropertyModel<String>(linkInicio, ""));
            link.setMarkupId(link.getId() + "ENS" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    public String getLinkInicio() {
        return linkInicio;
    }

    public void setLinkInicio(String linkInicio) {
        this.linkInicio = linkInicio;
    }

    public Tabela<EntidadeSupervisionavelVO> getTabela() {
        return tabela;
    }

    public ProviderGenericoList<EntidadeSupervisionavelVO> getProvider() {
        return provider;
    }

}
