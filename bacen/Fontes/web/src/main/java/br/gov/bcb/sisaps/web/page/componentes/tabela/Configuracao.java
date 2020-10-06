package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.util.consulta.IPaginado;

@SuppressWarnings("serial")
public class Configuracao implements Serializable {

    // cabeçalhos
    private String markupIdTitulo;
    private IModel<String> titulo = Model.of("Registros:");
    private IModel<String> cssTitulo = Model.of("tabela fundoPadraoAEscuro3");
    private IModel<String> styleTitulo = Model.of("width:100%; font-weight: bold; border-bottom: none; ");

    private ConfiguracaoOrdenacao configuracaoOrdenacao = new ConfiguracaoOrdenacao();
    private boolean exibirTitulo;
    private boolean exibirTituloHeader = true;
    // dados
    private String markupIdDados;
    private IModel<String> cssDados = Model.of("tabela");
    private IModel<String> styleDados = Model.of("width:100%;");
    private IModel<String> mensagemVazio = Model.of("Nenhum registro encontrado.");
    private IModel<Integer> tamanho = Model.of(IPaginado.QUANTIDADE_PADRAO);
    private IModel<String> cssPar = Model.of("fundoPadraoAClaro2");
    private IModel<String> cssImpar = Model.of("fundoPadraoAClaro3");
    private IModel<String> styleLinhas = Model.of("");
    private boolean exibirPaginador;
    private boolean exibirCabecalhoSempre;

    public Configuracao(String markupIdTitulo, String markupIdDados) {
        this.markupIdTitulo = markupIdTitulo;
        this.markupIdDados = markupIdDados;
    }

    public String getMarkupIdTitulo() {
        return markupIdTitulo;
    }

    public Configuracao setMarkupIdTitulo(String markupIdTitulo) {
        this.markupIdTitulo = markupIdTitulo;
        return this;
    }

    public IModel<String> getTitulo() {
        return titulo;
    }

    public Configuracao setTitulo(IModel<String> titulo) {
        this.titulo = titulo;
        return this;
    }

    public IModel<String> getCssTitulo() {
        return cssTitulo;
    }

    public Configuracao setCssTitulo(IModel<String> cssTitulo) {
        this.cssTitulo = cssTitulo;
        return this;
    }

    public IModel<String> getStyleTitulo() {
        return styleTitulo;
    }

    public Configuracao setStyleTitulo(IModel<String> styleTitulo) {
        this.styleTitulo = styleTitulo;
        return this;
    }

    public String getMarkupIdDados() {
        return markupIdDados;
    }

    public Configuracao setMarkupIdDados(String markupIdDados) {
        this.markupIdDados = markupIdDados;
        return this;
    }

    public IModel<String> getCssDados() {
        return cssDados;
    }

    public Configuracao setCssDados(IModel<String> cssDados) {
        this.cssDados = cssDados;
        return this;
    }

    public IModel<String> getStyleDados() {
        return styleDados;
    }

    public Configuracao setStyleDados(IModel<String> styleDados) {
        this.styleDados = styleDados;
        return this;
    }

    public IModel<String> getMensagemVazio() {
        return mensagemVazio;
    }

    public Configuracao setMensagemVazio(IModel<String> mensagemVazio) {
        this.mensagemVazio = mensagemVazio;
        return this;
    }

    public IModel<Integer> getTamanho() {
        return tamanho;
    }

    public Configuracao setTamanho(IModel<Integer> tamanho) {
        this.tamanho = tamanho;
        return this;
    }

    public IModel<String> getCssPar() {
        return cssPar;
    }

    public Configuracao setCssPar(IModel<String> cssPar) {
        this.cssPar = cssPar;
        return this;
    }

    public IModel<String> getCssImpar() {
        return cssImpar;
    }

    public Configuracao setCssImpar(IModel<String> cssImpar) {
        this.cssImpar = cssImpar;
        return this;
    }

    public boolean isExibirPaginador() {
        return exibirPaginador;
    }

    public Configuracao setExibirPaginador(boolean exibirPaginador) {
        this.exibirPaginador = exibirPaginador;
        return this;
    }

    public boolean isExibirTitulo() {
        return exibirTitulo;
    }

    public Configuracao setExibirTitulo(boolean exibirTitulo) {
        this.exibirTitulo = exibirTitulo;
        return this;
    }

    public boolean isExibirTituloHeader() {
        return exibirTituloHeader;
    }

    public Configuracao setExibirTituloHeader(boolean exibirTituloHeader) {
        this.exibirTituloHeader = exibirTituloHeader;
        return this;
    }

    public ConfiguracaoOrdenacao getConfiguracaoOrdenacao() {
        return configuracaoOrdenacao;
    }

    public void setConfiguracaoOrdenacao(ConfiguracaoOrdenacao configuracaoOrdenacao) {
        this.configuracaoOrdenacao = configuracaoOrdenacao;
    }
    
    public boolean isExibirCabecalhoSempre() {
        return exibirCabecalhoSempre;
    }

    public void setExibirCabecalhoSempre(boolean exibirCabecalho) {
        this.exibirCabecalhoSempre = exibirCabecalho;
    }

    public IModel<String> getStyleLinhas() {
        return styleLinhas;
    }

    public Configuracao setStyleLinhas(IModel<String> styleLinhas) {
        this.styleLinhas = styleLinhas;
        return this;
    }

}