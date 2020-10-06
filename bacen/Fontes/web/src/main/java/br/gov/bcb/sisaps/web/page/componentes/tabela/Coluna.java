package br.gov.bcb.sisaps.web.page.componentes.tabela;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Coluna<T> implements Serializable {
    // cabeçalho
    private String cabecalho;
    private String tituloCabecalho;
    private String cssCabecalho;
    private String estiloCabecalho;
    // propriedades
    private String propriedade;
    private String propriedadeTela;
    private Boolean ordenar;
    // formatador
    private IColunaFormatador<T> formatador;
    // fábrica de células
    private IColunaComponente<T> componente;
    private IColunaComponenteHeader<T> componenteHeader;

    private boolean escapeModelStrings;

    public String getCabecalho() {
        return cabecalho;
    }

    public Coluna<T> setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
        return this;
    }

    public String getTituloCabecalho() {
        return tituloCabecalho;
    }

    public Coluna<T> setTituloCabecalho(String tituloCabecalho) {
        this.tituloCabecalho = tituloCabecalho;
        return this;
    }

    public String getCssCabecalho() {
        return cssCabecalho;
    }

    public Coluna<T> setCssCabecalho(String cssCabecalho) {
        this.cssCabecalho = cssCabecalho;
        return this;
    }

    public String getEstiloCabecalho() {
        return estiloCabecalho;
    }

    public Coluna<T> setEstiloCabecalho(String estiloCabecalho) {
        this.estiloCabecalho = estiloCabecalho;
        return this;
    }

    public String getPropriedade() {
        return propriedade;
    }

    public Coluna<T> setPropriedade(String propriedade) {
        this.propriedade = propriedade;
        return this;
    }

    public String getPropriedadeTela() {
        return propriedadeTela;
    }

    public Coluna<T> setPropriedadeTela(String propriedadeTela) {
        this.propriedadeTela = propriedadeTela;
        return this;
    }

    public Boolean getOrdenar() {
        return ordenar;
    }

    public Coluna<T> setOrdenar(Boolean ordenar) {
        this.ordenar = ordenar;
        return this;
    }

    public IColunaFormatador<T> getFormatador() {
        return formatador;
    }

    public Coluna<T> setFormatador(IColunaFormatador<T> formatador) {
        this.formatador = formatador;
        return this;
    }

    public IColunaComponente<T> getComponente() {
        return componente;
    }

    public Coluna<T> setComponente(IColunaComponente<T> componente) {
        this.componente = componente;
        return this;
    }

    public boolean isEscapeModelStrings() {
        return escapeModelStrings;
    }

    public Coluna<T> setEscapeModelStrings(boolean escapeModelStrings) {
        this.escapeModelStrings = escapeModelStrings;
        return this;
    }

    public IColunaComponenteHeader<T> getComponenteHeader() {
        return componenteHeader;
    }

    public Coluna<T> setComponenteHeader(IColunaComponenteHeader<T> componenteHeader) {
        this.componenteHeader = componenteHeader;
        return this;
    }

}