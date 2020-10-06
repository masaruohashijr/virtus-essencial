package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.Matriz;


public class AtividadeVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private String nome;
    private MatrizVO matriz;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public MatrizVO getMatriz() {
        return matriz;
    }

    /**
     * Usado para o AliasToBeanResultTransformer transformar a entidade resultante da consulta em VO
     * 
     * @param matriz
     */
    public void setMatriz(Matriz matriz) {
        this.matriz = new MatrizVO(matriz.getPk(), matriz.getEstadoMatriz(), matriz.getCiclo());
    }

}
