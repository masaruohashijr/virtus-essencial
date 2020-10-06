package br.gov.bcb.sisaps.web.page.componentes.tabela;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesImagens;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class TabelaHeaderPopulatorHelper {

    public <T> void prepararOrdenacao(Configuracao configuracao, final SortableDataProvider<T, String> provider,
            final Coluna<T> coluna, WebMarkupContainer componente) {
        Image ordenar = criarImagem(coluna, componente);
        String property = provider.getSort().getProperty();
        ConfiguracaoOrdenacao configuracaoOrdenacao = configuracao.getConfiguracaoOrdenacao();
        if (property != null && property.equals(coluna.getPropriedade())) {
            ordenar.setImageResourceReference(provider.getSort().isAscending() ? ConstantesImagens.IMG_SORT_CRESCENTE
                    : ConstantesImagens.IMG_SORT_DECRESCENTE);
            componente.add(new AttributeAppender(ConstantesWeb.CLASS,
                    provider.getSort().isAscending() ? configuracaoOrdenacao.getCssCrescente() : configuracaoOrdenacao
                            .getCssDecrescente(), ConstantesWeb.SEPARATOR_CLASS));
        } else {
            ordenar.setImageResourceReference(ConstantesImagens.IMG_SORT);
            componente.add(new AttributeAppender(ConstantesWeb.CLASS, configuracaoOrdenacao.getCssNeutro(),
                    ConstantesWeb.SEPARATOR_CLASS));
        }
    }

    private <T> Image criarImagem(final Coluna<T> coluna, WebMarkupContainer componente) {
        WebMarkupContainer link = (WebMarkupContainer) componente.get("orderByLink");
        link.setMarkupId("idOrdenar"
                + (Character.toUpperCase(coluna.getPropriedade().charAt(0)) + coluna.getPropriedade().substring(1))
                        .replace(".", ""));
        Image ordenar = new Image("ordenar", ConstantesImagens.IMG_SORT);
        link.add(ordenar);
        return ordenar;
    }
}
