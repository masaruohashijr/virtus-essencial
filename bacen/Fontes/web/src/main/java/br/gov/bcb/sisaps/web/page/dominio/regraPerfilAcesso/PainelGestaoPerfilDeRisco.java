package br.gov.bcb.sisaps.web.page.dominio.regraPerfilAcesso;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaRegraPerfilAcessoVO;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderOrdenadoPaginado;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaFormatador;
import br.gov.bcb.sisaps.web.page.componentes.tabela.ProviderOrdenado;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelGestaoPerfilDeRisco extends PainelSisAps {

    private static final String WIDTH_10 = "width:10%";
    private static final String WIDTH_15 = "width:15%";
    private static final String LOCALIZACAO = "localizacao";
    private static final String WIDTH_20 = "width:20%";
    private final PerfilAcessoEnum perfil;
    private final ConsultaRegraPerfilAcessoVO consulta = new ConsultaRegraPerfilAcessoVO();
    private Tabela<RegraPerfilAcessoVO> tabela;
    @SpringBean
    private RegraPerfilAcessoMediator regraPerfilAcessoMediator;

    public PainelGestaoPerfilDeRisco(String id, PerfilAcessoEnum perfil) {
        super(id);
        this.perfil = perfil;
        consulta.setPerfilAcesso(perfil);
        setOutputMarkupId(true);
        setMarkupId(getId());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addModal();
        addTabela();
        add(new Label("idTitulo", perfil.getDescricao()));

        AjaxSubmitLink link = new AjaxSubmitLink("linkIncluirRegra") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                modalEdicao.setContent(new PainelInclusaoRegraPerfilAcesso(modalEdicao, perfil, tabela));
                modalEdicao.show(target);
            }
        };
        link.setMarkupId(link.getId());
        add(link);
    }

    private void addModal() {
        adicionarModal();
        modalEdicao.setAutoSize(false);
        modalEdicao.setInitialWidth(600);
        modalEdicao.setInitialHeight(350);
    }

    private void addTabela() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<RegraPerfilAcessoVO>> colunas = obterColunas();
        // provider
        ProviderOrdenado<RegraPerfilAcessoVO> providerGenericoList = criarProvider();
        // tabela
        tabela = new Tabela<RegraPerfilAcessoVO>("tabela", cfg, colunas, providerGenericoList, true);
        tabela.setOutputMarkupPlaceholderTag(true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTituloTabela", "idDados");
        cfg.setTitulo(Model.of("Regras para acesso"));
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirTitulo(true);
        cfg.setExibirPaginador(true);
        return cfg;
    }

    private ProviderOrdenado<RegraPerfilAcessoVO> criarProvider() {
        ProviderOrdenado<RegraPerfilAcessoVO> provider =
                new ProviderOrdenadoPaginado<RegraPerfilAcessoVO, Integer, ConsultaRegraPerfilAcessoVO>("pk",
                        SortOrder.ASCENDING, regraPerfilAcessoMediator, Model.of(consulta));
        return provider;
    }

    private List<Coluna<RegraPerfilAcessoVO>> obterColunas() {
        List<Coluna<RegraPerfilAcessoVO>> colunas = new LinkedList<Coluna<RegraPerfilAcessoVO>>();

        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Localização").setPropriedade(LOCALIZACAO)
                .setOrdenar(true).setEstiloCabecalho(WIDTH_20));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Localizações subordinadas")
                .setPropriedade("localizacoesSubordinadas").setPropriedadeTela("descricaoLocalizacoesSubordinadas")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_15));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Função").setPropriedade("codigoFuncao")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_10));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Substituto eventual")
                .setPropriedade("substitutoEventualFuncao.descricao").setOrdenar(true).setEstiloCabecalho(WIDTH_15));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Substituto prazo certo")
                .setPropriedade("substitutoPrazoCerto.descricao").setOrdenar(true).setEstiloCabecalho(WIDTH_15));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Situação").setPropriedade("situacao.codigo")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_10));
        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Matrícula").setPropriedade("matricula")
                .setPropriedadeTela("matriculaFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_10));

        colunas.add(new Coluna<RegraPerfilAcessoVO>().setCabecalho("Ação")
                .setEstiloCabecalho("width:75px;white-space: nowrap;").setFormatador(formatador("width:75px"))
                .setComponente(new IColunaComponente<RegraPerfilAcessoVO>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<RegraPerfilAcessoVO>> cellItem,
                            String componentId, IModel<RegraPerfilAcessoVO> rowModel) {
                        return new AcoesPanel(componentId, rowModel, PainelGestaoPerfilDeRisco.this);
                    }
                }));
        return colunas;
    }

    private IColunaFormatador<RegraPerfilAcessoVO> formatador(final String style) {
        IColunaFormatador<RegraPerfilAcessoVO> formatador = new IColunaFormatador<RegraPerfilAcessoVO>() {

            @Override
            public String obterCss(RegraPerfilAcessoVO obj) {
                return null;
            }

            @Override
            public String obterStyle(RegraPerfilAcessoVO obj) {
                return style;
            }

        };
        return formatador;
    }

    public Tabela<RegraPerfilAcessoVO> getTabela() {
        return tabela;
    }

    public void setTabela(Tabela<RegraPerfilAcessoVO> tabela) {
        this.tabela = tabela;
    }

}
