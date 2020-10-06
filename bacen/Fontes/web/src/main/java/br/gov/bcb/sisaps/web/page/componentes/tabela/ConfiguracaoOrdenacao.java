package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class ConfiguracaoOrdenacao implements Serializable {

    private IModel<String> cssCrescente = Model.of("crescente");
    private IModel<String> cssDecrescente = Model.of("decrescente");
    private IModel<String> cssNeutro = Model.of("fundoPadraoAEscuro4");

    public IModel<String> getCssCrescente() {
        return cssCrescente;
    }

    public ConfiguracaoOrdenacao setCssCrescente(IModel<String> cssCrescente) {
        this.cssCrescente = cssCrescente;
        return this;
    }

    public IModel<String> getCssDecrescente() {
        return cssDecrescente;
    }

    public ConfiguracaoOrdenacao setCssDecrescente(IModel<String> cssDecrescente) {
        this.cssDecrescente = cssDecrescente;
        return this;
    }

    public IModel<String> getCssNeutro() {
        return cssNeutro;
    }

    public ConfiguracaoOrdenacao setCssNeutro(IModel<String> cssNeutro) {
        this.cssNeutro = cssNeutro;
        return this;
    }
}