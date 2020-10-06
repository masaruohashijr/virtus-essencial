package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

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

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;
import br.gov.bcb.sisaps.web.page.dominio.gerenciarnotasintese.GerenciarNotaSintesePage;

public class PainelSintesesRiscoRevisao extends PainelSisAps {

    private static final String PROP_NOME_PARAMETRO_GRUPO_RISCO_CONTROLE = "nomeParametroGrupoRiscoControle";
    private static final String PROP_NOME_ENTIDADE_SUPERVISIONAVEL = "nomeEntidadeSupervisionavel";

    @SpringBean
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;
    @SpringBean
    private CicloMediator cicloMediator;
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    public PainelSintesesRiscoRevisao(String id) {
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
        List<Coluna<SinteseRiscoRevisaoVO>> colunas = obterColunas();
        // provider
        ProviderGenericoList<SinteseRiscoRevisaoVO> providerGenericoList = criarProvider();
        // tabela
        Tabela<SinteseRiscoRevisaoVO> tabela =
                new Tabela<SinteseRiscoRevisaoVO>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloSintesesRiscoRevisao", "idDadosSintesesRiscoRevisao");
        cfg.setTitulo(Model.of("Sínteses de risco para revisão"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro2"));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(true);
        return cfg;
    }

    private List<Coluna<SinteseRiscoRevisaoVO>> obterColunas() {
        List<Coluna<SinteseRiscoRevisaoVO>> colunas = new LinkedList<Coluna<SinteseRiscoRevisaoVO>>();
        colunas.add(new Coluna<SinteseRiscoRevisaoVO>().setCabecalho("ES")
                .setPropriedade(PROP_NOME_ENTIDADE_SUPERVISIONAVEL).setOrdenar(true).setEstiloCabecalho("width:35%"));
        colunas.add(new Coluna<SinteseRiscoRevisaoVO>().setCabecalho("Síntese")
                .setPropriedade(PROP_NOME_PARAMETRO_GRUPO_RISCO_CONTROLE).setComponente(new ComponenteCelulaLink())
                .setEstiloCabecalho("width:25%").setOrdenar(true));
        colunas.add(new Coluna<SinteseRiscoRevisaoVO>().setCabecalho("ARCs pendentes de publicação")
                .setPropriedade("arcsPendentesPublicacao").setOrdenar(true).setEstiloCabecalho("width:40%"));
        return colunas;
    }

    private ProviderGenericoList<SinteseRiscoRevisaoVO> criarProvider() {
        ProviderGenericoList<SinteseRiscoRevisaoVO> provider =
                new ProviderGenericoList<SinteseRiscoRevisaoVO>(PROP_NOME_ENTIDADE_SUPERVISIONAVEL,
                        SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<SinteseRiscoRevisaoVO>> obterModel() {
        IModel<List<SinteseRiscoRevisaoVO>> modelConsulta = new AbstractReadOnlyModel<List<SinteseRiscoRevisaoVO>>() {
            @Override
            public List<SinteseRiscoRevisaoVO> getObject() {
                UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
                return entidadeSupervisionavelMediator.buscarSintesesRiscoRevisao(usuario.getServidorVO()
                        .getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR));
            }
        };
        return modelConsulta;
    }

    private class ComponenteCelulaLink implements IColunaComponente<SinteseRiscoRevisaoVO> {
        @Override
        public Component obterComponente(Item<ICellPopulator<SinteseRiscoRevisaoVO>> cellItem, String componentId,
                final IModel<SinteseRiscoRevisaoVO> rowModel) {
            final SinteseRiscoRevisaoVO sinteseRiscoRevisaoVO = rowModel.getObject();
            Link<SinteseRiscoRevisaoVO> link = new Link<SinteseRiscoRevisaoVO>("link", rowModel) {
                @Override
                public void onClick() {
                    avancarParaNovaPagina(sinteseRiscoRevisaoVO);
                }
            };
            link.setBody(new PropertyModel<String>(link.getModelObject(), PROP_NOME_PARAMETRO_GRUPO_RISCO_CONTROLE));
            link.setMarkupId(link.getId() + "Sintese_Grupo" + sinteseRiscoRevisaoVO.getPkParametroGrupoRiscoControle()
                    + "_ES" + sinteseRiscoRevisaoVO.getPkEntidadeSupervisionavel());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }
    }

    private void avancarParaNovaPagina(SinteseRiscoRevisaoVO sinteseRiscoRevisaoVO) {
        Ciclo ciclo =
                cicloMediator.consultarUltimoCicloEmAndamentoCorecES(sinteseRiscoRevisaoVO
                        .getPkEntidadeSupervisionavel());
        ParametroGrupoRiscoControle parametro =
                ParametroGrupoRiscoControleMediator.get().buscarParametroGrupoRisco(
                        sinteseRiscoRevisaoVO.getPkParametroGrupoRiscoControle());

        getPaginaAtual().avancarParaNovaPagina(
                new GerenciarNotaSintesePage(perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk()), parametro
                        .getNomeAbreviado()));
    }

}
