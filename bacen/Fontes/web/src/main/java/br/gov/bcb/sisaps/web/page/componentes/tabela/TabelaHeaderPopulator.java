package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class TabelaHeaderPopulator<T> extends ListView<ICellPopulator<T>> {
    private static final String VALOR = "valor";
    private Configuracao configuracao;
    private final SortableDataProvider<T, String> provider;
    private final List<Coluna<T>> colunas;

    public TabelaHeaderPopulator(String id, List<? extends ICellPopulator<T>> list, Configuracao configuracao,
            SortableDataProvider<T, String> provider, List<Coluna<T>> colunas) {
        super(id, list);
        this.configuracao = configuracao;
        this.provider = provider;
        this.colunas = colunas;
    }

    @Override
    protected void populateItem(ListItem<ICellPopulator<T>> item) {

        final Coluna<T> coluna = colunas.get(item.getIndex());
        IColunaComponenteHeader<T> componenteHeader = coluna.getComponenteHeader();
        String idCelula = "celula";
        WebMarkupContainer componente;
        if (componenteHeader == null) {
            if (coluna.getOrdenar() != null && coluna.getOrdenar()) {
                componente = new Borda(idCelula, coluna.getPropriedade(), provider) {
                    @Override
                    public String getMarkupId() {
                        return "ordenar_" + coluna.getPropriedade();
                    }
                };
                prepararOrdenacao(configuracao, provider, coluna, componente);
            } else {
                componente = new WebMarkupContainer(idCelula);
                componente.add(new AttributeModifier("style", "padding:6px; background-color:#93abc5;"));
            }
            componente.setOutputMarkupId(true);
            componente.add(new Label(VALOR, Model.of(coluna.getCabecalho())));
            componente.add(new AttributeModifier("title", Model.of(coluna.getTituloCabecalho())));

        } else {
            componente = new WebMarkupContainer(idCelula);
            componente.add(componenteHeader.obterComponenteHeader(VALOR));
        }

        if (coluna.getEstiloCabecalho() != null) {
            componente.add(new AttributeAppender(ConstantesWeb.STYLE, Model.of(coluna.getEstiloCabecalho()),
                    ConstantesWeb.SEPARATOR_STYLE));
        }
        if (coluna.getCssCabecalho() != null) {
            componente.add(new AttributeAppender(ConstantesWeb.CLASS, Model.of(coluna.getCssCabecalho()),
                    ConstantesWeb.SEPARATOR_CLASS));
        }

        item.add(componente);
    }

    protected void prepararOrdenacao(Configuracao configuracao, SortableDataProvider<T, String> provider,
            Coluna<T> coluna, WebMarkupContainer componente) {
        new TabelaHeaderPopulatorHelper().prepararOrdenacao(configuracao, provider, coluna, componente);
    }
}
