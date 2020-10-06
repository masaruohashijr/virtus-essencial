package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaFormatador;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class TabelaSemFormatoHelper {

    private static final String FORMATO_CABECALHO = "padding:0px; background-color:#fff; color:#000;";
    private static final String FUNDO_PADRAO_A_CLARO3 = "fundoPadraoAClaro3";

    public static Configuracao configureTabela(String titulo, String markupId) {
        String markupIdTitulo = "idTitulo";
        String markupIdDados = "idDados";
        if (titulo != null) {
            markupIdTitulo = markupIdTitulo + Constantes.SUBLINHADO + markupId;
            markupIdDados = markupIdDados + Constantes.SUBLINHADO + markupId;
        }
        Configuracao cfg = new Configuracao(markupIdTitulo, markupIdDados);
        cfg.setMensagemVazio(null);
        cfg.setCssPar(Model.of(FUNDO_PADRAO_A_CLARO3));
        cfg.setCssImpar(Model.of(FUNDO_PADRAO_A_CLARO3));
        cfg.setTitulo(Model.of(titulo));
        cfg.setExibirTitulo(true);
        return cfg;
    }

    public static Configuracao configureTabelaSemTitulo(String titulo, String markupId) {
        Configuracao ct = configureTabela(titulo, markupId);
        ct.setExibirTitulo(false);
        return ct;
    }

    public static <T> Tabela<T> buildTabela(String tabelaId, IModel<List<T>> modelLista, Configuracao cfg,
            List<Coluna<T>> colunas, String propiedadeOrdenacao) {
        return buildTabela(tabelaId, modelLista, cfg, colunas, propiedadeOrdenacao, SortOrder.ASCENDING);
    }

    public static <T> Tabela<T> buildTabela(String tabelaId, IModel<List<T>> modelLista, Configuracao cfg,
            List<Coluna<T>> colunas, String propiedadeOrdenacao, SortOrder order) {
        ProviderGenericoList<T> provider = new ProviderGenericoList<T>(propiedadeOrdenacao, order, modelLista);
        return new Tabela<T>(tabelaId, cfg, colunas, provider, true) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isVisivel());
            }
        };
    }

    public static String getFormatoCabecalho(String width) {
        return FORMATO_CABECALHO + width;
    }

    public static class FormatoColunaBold<T> implements IColunaFormatador<T> {
        @Override
        public String obterCss(T obj) {
            return null;
        }

        @Override
        public String obterStyle(T obj) {
            return "font-weight: bold;";
        }
    }

    protected abstract static class WebMarkupContainerExibicao extends WebMarkupContainer {

        public WebMarkupContainerExibicao(String id) {
            super(id);
            setMarkupId(id);
            setOutputMarkupId(true);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisibilityAllowed(getVisible());
        }

        protected abstract boolean getVisible();

    }

}
