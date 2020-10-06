package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

@SuppressWarnings("serial")
public class TabelaHelper implements Serializable {

    public WebMarkupContainer newTabela(String id, String markupId, IModel<String> css, IModel<String> style) {
        WebMarkupContainer tabela = new WebMarkupContainer(id);
        tabela.setOutputMarkupId(true);
        tabela.setMarkupId(markupId);
        tabela.add(new AttributeAppender(ConstantesWeb.CLASS, css, ConstantesWeb.SEPARATOR_CLASS));
        tabela.add(new AttributeAppender(ConstantesWeb.STYLE, style, ConstantesWeb.SEPARATOR_CLASS));
        tabela.add(new AttributeAppender("cellspacing", Model.of("1"), ConstantesWeb.SEPARATOR_CLASS));
        return tabela;
    }

    public WebMarkupContainer newTabelaVazia(Configuracao configuracao, final WebMarkupContainer dados) {
        WebMarkupContainer vazio = new WebMarkupContainer("vazio") {
            @Override
            public boolean isVisible() {
                return !dados.isVisible();
            }
        };

        WebMarkupContainer linha = new WebMarkupContainer("linha");
        linha.add(new AttributeAppender(ConstantesWeb.CLASS, Model.of(configuracao.getCssImpar()),
                ConstantesWeb.SEPARATOR_CLASS));

        Label mensagem = new Label("mensagem", configuracao.getMensagemVazio());
        mensagem.add(new AttributeAppender(ConstantesWeb.STYLE, Model.of("padding:4px"), ConstantesWeb.SEPARATOR_STYLE));
        linha.add(mensagem);

        vazio.add(linha);

        return vazio;
    }
}