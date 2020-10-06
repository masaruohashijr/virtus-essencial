package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.handler.resource.ResourceRequestHandler;
import org.apache.wicket.request.resource.ByteArrayResource;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;
import br.gov.bcb.sisaps.web.page.componentes.modal.AjaxSubmitLinkModalWindow;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaFormatador;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.tabela.coluna.ColunaLink;

public abstract class TabelaAnexoComum<T extends ObjetoPersistenteVO> extends Panel {
    private static final String WIDTH_20 = "width:20%";
    private static final String PROP_DESCRICAO = "descricao";
    private static final String PROP_LINK = "link";
    protected Tabela<T> tabela;
    protected IModel<List<T>> modelListaAnexo;
    private final boolean edicao;
    private final String entidade;
    private final boolean mostrarColunaIndex;
    private final String propriedadeExibicao;
    private boolean paginaModal;

    public TabelaAnexoComum(String id, IModel<List<T>> modelListaAnexo, boolean edicao,
            Configuracao configuracaoTabela, String entidade, boolean mostrarColunaIndex) {
        this(id, modelListaAnexo, edicao, configuracaoTabela, entidade, mostrarColunaIndex, PROP_LINK, false);
    }

    public TabelaAnexoComum(String id, IModel<List<T>> modelListaAnexo, boolean edicao,
            Configuracao configuracaoTabela, String entidade, boolean mostrarColunaIndex, String propriedadeExibicao,
            boolean isPaginaModal) {
        super(id);
        this.paginaModal = isPaginaModal;
        this.entidade = entidade;
        this.mostrarColunaIndex = mostrarColunaIndex;
        this.propriedadeExibicao = propriedadeExibicao;
        setMarkupId(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.modelListaAnexo = modelListaAnexo;
        this.edicao = edicao;

        buildTabelaAnexo(configuracaoTabela);
    }

    private void buildTabelaAnexo(Configuracao configuracaoTabela) {
        Configuracao cfg = configuracaoTabela;
        if (cfg == null) {
            cfg = configureTabela();
        }
        List<Coluna<T>> colunas = criarColulas();
        ProviderGenericoList<T> provider =
                new ProviderGenericoList<T>(PROP_DESCRICAO, SortOrder.ASCENDING, modelListaAnexo);
        tabela = new Tabela<T>("tabela", cfg, colunas, provider, true);
        addOrReplace(tabela);
    }

    private Configuracao configureTabela() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(""));
        cfg.setExibirTituloHeader(false);
        return cfg;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    private List<Coluna<T>> criarColulas() {
        List<Coluna<T>> colunas = new LinkedList<Coluna<T>>();
        if (mostrarColunaIndex) {
            colunas.add(new Coluna<T>().setEstiloCabecalho(WIDTH_20).setOrdenar(false)
                    .setFormatador(new FormatadorControle()).setPropriedade(PROP_DESCRICAO));
        }

        colunas.add(new Coluna<T>().setComponente(new ComponenteCelulaLink()).setEstiloCabecalho("width:75%")
                .setOrdenar(false));
        if (edicao) {
            colunas.add(new Coluna<T>().setComponente(new ComponenteLinkExcluir()).setEstiloCabecalho("width:5%")
                    .setOrdenar(false));
        }
        return colunas;
    }

    private WebMarkupContainer criarLinkDownload(final IModel<T> rowModel) {
        if (paginaModal) {
            return new AjaxSubmitLinkModalWindow(PROP_LINK, paginaModal) {
                @Override
                public void executeSubmit(AjaxRequestTarget target) {
                    onDownLoad(rowModel.getObject());

                }
            };

        } else {

            return new Link(PROP_LINK, rowModel) {
                @Override
                public void onClick() {

                    getPage().getFeedbackMessages().clear();
                    try {
                        onDownLoad(rowModel.getObject());
                    } catch (NegocioException e) {
                        ExceptionUtils.tratarNegocioException(e, getPage());
                    }
                }
            };
        }
    }

    private class ComponenteCelulaLink implements IColunaComponente<T> {
        @Override
        public Component obterComponente(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {
            WebMarkupContainer link = criarLinkDownload(rowModel);
            if (link instanceof AjaxSubmitLinkModalWindow) {
                ((AjaxSubmitLinkModalWindow) link).setBody(new PropertyModel<String>(rowModel, propriedadeExibicao));
            } else {
                ((Link) link).setBody(new PropertyModel<String>(rowModel, propriedadeExibicao));
            }
            link.setMarkupId(link.getId() + "anexo" + rowModel.getObject().getPk());
            link.setOutputMarkupId(true);
            return new ColunaLink(componentId, link);
        }

    }

    private class ComponenteLinkExcluir implements IColunaComponente<T> {
        @Override
        public Component obterComponente(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {
            return new AcaoExcluirAnexo<T>(componentId, rowModel, entidade) {
                @Override
                public void onExcluir(AjaxRequestTarget target) {
                    excluirAnexo(target, rowModel);
                }
            };
        }
    }

    protected abstract void excluirAnexo(AjaxRequestTarget target, IModel<T> rowModel);

    protected abstract void onDownLoad(T vo);

    private class FormatadorControle implements IColunaFormatador<T> {
        @Override
        public String obterCss(T obj) {
            return "nomeCampo";
        }

        @Override
        public String obterStyle(T obj) {
            return WIDTH_20;
        }
    }

    public boolean isEdicao() {
        return edicao;
    }

    protected void executarDowload(String link, byte[] arquivoBytes) {
        if (arquivoBytes == null) {
            error("Arquivo '" + link + "' não foi encontrado.");
        } else {
            ByteArrayResource resouce =
                    new ByteArrayResource(ContentType.DEFAULT_BINARY.getMimeType(), arquivoBytes, link);
            getRequestCycle().scheduleRequestHandlerAfterCurrent(
                    new ResourceRequestHandler(resouce, getPage().getPageParameters()));
        }
    }

    public Tabela<T> getTabela() {
        return tabela;
    }

}
