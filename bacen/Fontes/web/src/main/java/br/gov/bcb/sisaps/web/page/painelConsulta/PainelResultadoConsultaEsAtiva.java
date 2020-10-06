package br.gov.bcb.sisaps.web.page.painelConsulta;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPage;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPageResumido;

public class PainelResultadoConsultaEsAtiva extends PainelSisAps {
    private static final String NOME = "nome";
    private final ConsultaEntidadeSupervisionavelVO consulta;
    private final boolean mostrarPrioridade;
    private final ModalWindow modalESBloqueada;

    public PainelResultadoConsultaEsAtiva(String id, ConsultaEntidadeSupervisionavelVO consulta,
            boolean mostrarPrioridade) {
        super(id);
        this.consulta = consulta;
        this.mostrarPrioridade = mostrarPrioridade;
        addTabela();
        modalESBloqueada = new ModalWindow("modalESBloqueada");
        modalESBloqueada.setOutputMarkupId(true);
        modalESBloqueada.setResizable(false);
        modalESBloqueada.setInitialWidth(700);
        modalESBloqueada.setInitialHeight(80);
        addOrReplace(modalESBloqueada);
        addTabela();

    }
    
    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<EntidadeSupervisionavelVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<EntidadeSupervisionavelVO> providerGenericoList = criarProvider();
        // tabela
        Tabela<EntidadeSupervisionavelVO> tabela =
                new Tabela<EntidadeSupervisionavelVO>("tabela", cfg, colunas, providerGenericoList, false);
        tabela.setOutputMarkupId(true);

        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloEsAtivas", "idDadosEsAtivas");
        cfg.setTitulo(Model.of("ESs ativas"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirTitulo(true);
        cfg.setExibirPaginador(false);
        return cfg;
    }

    private ProviderGenericoList<EntidadeSupervisionavelVO> criarProvider() {
        ProviderGenericoList<EntidadeSupervisionavelVO> provider =
                new ProviderGenericoList<EntidadeSupervisionavelVO>(NOME, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<EntidadeSupervisionavelVO>> obterModel() {
        IModel<List<EntidadeSupervisionavelVO>> modelConsulta =
                new AbstractReadOnlyModel<List<EntidadeSupervisionavelVO>>() {
                    @Override
                    public List<EntidadeSupervisionavelVO> getObject() {
                        consulta.setPaginada(false);
                        return EntidadeSupervisionavelMediator.get().consultarEntidadesPerfilConsulta(consulta);
                    }
                };
        return modelConsulta;
    }

    private List<Coluna<EntidadeSupervisionavelVO>> obterColunas() {
        List<Coluna<EntidadeSupervisionavelVO>> colunas = new LinkedList<Coluna<EntidadeSupervisionavelVO>>();

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("ES").setPropriedade(NOME)
                .setComponente(new ComponenteLink()).setOrdenar(true).setEstiloCabecalho("width:30%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Equipe").setPropriedade("localizacao")
                .setOrdenar(true).setEstiloCabecalho("width:15%"));

        colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Supervisor titular")
                .setPropriedade("nomeSupervisor").setOrdenar(true).setEstiloCabecalho("width:20%"));
        if (mostrarPrioridade) {
            colunas.add(new Coluna<EntidadeSupervisionavelVO>().setCabecalho("Prioridade")
                    .setPropriedadeTela("prioridade.descricao").setPropriedade("prioridade.codigo").setOrdenar(true)
                    .setEstiloCabecalho("width:10%"));
        }

        return colunas;
    }

    private class ComponenteLink implements IColunaComponente<EntidadeSupervisionavelVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<EntidadeSupervisionavelVO>> cellItem, String componentId,
                final IModel<EntidadeSupervisionavelVO> rowModel) {
            final EntidadeSupervisionavelVO entidadeSupervisionavelVO = rowModel.getObject();

            if (entidadeSupervisionavelVO.getPkVersaoPerfilRisco() == null) {
                return new Label(componentId, entidadeSupervisionavelVO.getNome());
            } else {
                AjaxSubmitLinkSisAps link = new AjaxSubmitLinkSisAps("link", true) {
                    @Override
                    public void executeSubmit(AjaxRequestTarget target) {
                        acessarPerfilRisco(entidadeSupervisionavelVO, target);
                    }
                };
                link.setBody(new PropertyModel<String>(rowModel.getObject(), NOME));
                link.setMarkupId("linkPerfil" + rowModel.getObject().getPk());
                link.setOutputMarkupId(true);
                return new ColunaLink(componentId, link);
            }
        }
    }

    private void acessarPerfilRisco(final EntidadeSupervisionavelVO entidadeSupervisionavelVO, AjaxRequestTarget target) {
        Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(entidadeSupervisionavelVO.getPk());

        String bloqueio = EntidadeSupervisionavelMediator.get().possuiBloqueioES(ciclo);
        if ("".equals(bloqueio)) {
            if (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)
                    || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)) {
                getPaginaAtual().avancarParaNovaPagina(new PerfilDeRiscoPage(ciclo.getPk(), true));
            } else {
                getPaginaAtual().avancarParaNovaPagina(new PerfilDeRiscoPageResumido(ciclo.getPk()));
            }
        } else {
            modalESBloqueada.setContent(new PainelEsBloqueada(modalESBloqueada, bloqueio));
            modalESBloqueada.setOutputMarkupId(true);
            modalESBloqueada.show(target);
        }
    }

}
