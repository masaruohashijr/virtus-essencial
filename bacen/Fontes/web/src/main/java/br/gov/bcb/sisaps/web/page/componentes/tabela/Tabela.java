package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.gov.bcb.sisaps.web.page.componentes.tabela.paginador.Paginador;

@SuppressWarnings("serial")
public class Tabela<T> extends Panel {

    private Configuracao configuracao;
    private List<Coluna<T>> colunas;
    private SortableDataProvider<T, String> provider;
    private boolean visivel = true;
    private boolean ineficiente;

    public Tabela(String id, Configuracao configuracao, List<Coluna<T>> colunas,
            final SortableDataProvider<T, String> provider, boolean infereVisibilidadeComProvider) {
        super(id);
        setOutputMarkupId(true);
        this.configuracao = configuracao;
        this.colunas = colunas;
        this.provider = provider;
        this.ineficiente = infereVisibilidadeComProvider;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        TabelaHelper helper = newTabelaHelper();
        WebMarkupContainer tabelaTitulo =
                helper.newTabela("tabelaTitulo", configuracao.getMarkupIdTitulo(), configuracao.getCssTitulo(),
                        configuracao.getStyleTitulo());
        tabelaTitulo.add(new Label("titulo", configuracao.getTitulo()));
        tabelaTitulo.setVisible(configuracao.isExibirTitulo());
        addOrReplace(tabelaTitulo);
        WebMarkupContainer tabelaDados =
                helper.newTabela("tabelaDados", configuracao.getMarkupIdDados(), configuracao.getCssDados(),
                        configuracao.getStyleDados());
        addOrReplace(tabelaDados);
        if (configuracao.isExibirCabecalhoSempre()) {
            this.visivel = true;
        } else {
            this.visivel = provider.size() > 0;
        }
        WebMarkupContainer dados = new WebMarkupContainer("dados") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (Tabela.this.ineficiente) {
                    Tabela.this.visivel = provider.size() > 0;
                }
                setVisible(Tabela.this.visivel);
            }
        };

        dados.setOutputMarkupId(true);
        tabelaDados.addOrReplace(dados);

        List<ICellPopulator<T>> celulas = new LinkedList<ICellPopulator<T>>();
        for (Coluna<T> c : colunas) {
            celulas.add(newCellPopulator(c));
        }

        dados.addOrReplace(newHeaderPopulator(celulas, configuracao, provider, colunas));

        DataGridView<T> linhas = newDataView(configuracao, celulas, provider);
        dados.addOrReplace(linhas);

        // tabela vazia
        tabelaDados.addOrReplace(helper.newTabelaVazia(configuracao, dados));

        addOrReplace(newPaginador(linhas, dados));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(Tabela.class, "tabela.css")));
    }

    public TabelaHelper newTabelaHelper() {
        return new TabelaHelper();
    }

    protected TabelaCellPopulator<T> newCellPopulator(Coluna<T> c) {
        return new TabelaCellPopulator<T>(c);
    }

    protected TabelaHeaderPopulator<T> newHeaderPopulator(List<ICellPopulator<T>> celulas, Configuracao configuracao,
            SortableDataProvider<T, String> provider, List<Coluna<T>> colunas) {
        TabelaHeaderPopulator<T> tabelaHeaderPopulator =
                new TabelaHeaderPopulator<T>("cabecalhos", celulas, configuracao, provider, colunas);
        tabelaHeaderPopulator.setVisible(configuracao.isExibirTituloHeader());
        return tabelaHeaderPopulator;
    }

    protected DataGridView<T> newDataView(Configuracao configuracao, List<ICellPopulator<T>> celulas,
            SortableDataProvider<T, String> provider) {
        return new Linhas<T>("linhas", celulas, provider, configuracao);
    }

    protected Paginador newPaginador(DataGridView<T> linhas, final WebMarkupContainer dados) {
        return new Paginador("paginador", linhas) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(configuracao.isExibirPaginador() && Tabela.this.visivel);
            }
        };
    }

    public boolean isVisivel() {
        return visivel;
    }

}