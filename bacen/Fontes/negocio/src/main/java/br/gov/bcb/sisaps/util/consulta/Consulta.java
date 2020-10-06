package br.gov.bcb.sisaps.util.consulta;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Consulta<T> implements Serializable {

    private boolean paginada;
    private Long inicio;
    private Long quantidade;

    private Ordenacao ordenacao;
    private List<Ordenacao> ordenacaoSecundaria;

    public boolean isPaginada() {
        return paginada;
    }

    public void setPaginada(boolean paginada) {
        this.paginada = paginada;
    }

    public Long getInicio() {
        return inicio;
    }

    public void setInicio(Long inicio) {
        this.inicio = inicio;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public Consulta<T> adicionarOrdemSecundaria(Ordenacao ordem) {
        if (ordenacaoSecundaria == null) {
            ordenacaoSecundaria = new LinkedList<Ordenacao>();
        }
        ordenacaoSecundaria.add(ordem);
        return this;
    }

    public List<Ordenacao> getOrdenacaoSecundaria() {
        return ordenacaoSecundaria;
    }

    public void setOrdenacaoSecundaria(List<Ordenacao> ordenacaoSecundaria) {
        this.ordenacaoSecundaria = ordenacaoSecundaria;
    }
}