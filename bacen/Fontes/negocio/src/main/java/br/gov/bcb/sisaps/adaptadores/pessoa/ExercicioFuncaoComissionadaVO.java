/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.io.Serializable;

public class ExercicioFuncaoComissionadaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String matricula;
    private String codigoFuncaoComissionada;
    private String descricao;
    private String tipoExercicioFuncaoComissionada;
    private String localizacaoFuncaoComissionada;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCodigoFuncaoComissionada() {
        return codigoFuncaoComissionada;
    }

    public void setCodigoFuncaoComissionada(String codigoFuncaoComissionada) {
        this.codigoFuncaoComissionada = codigoFuncaoComissionada;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoExercicioFuncaoComissionada() {
        return tipoExercicioFuncaoComissionada;
    }

    public void setTipoExercicioFuncaoComissionada(String tipoExercicioFuncaoComissionada) {
        this.tipoExercicioFuncaoComissionada = tipoExercicioFuncaoComissionada;
    }

    public String getLocalizacaoFuncaoComissionada() {
        return localizacaoFuncaoComissionada;
    }

    public void setLocalizacaoFuncaoComissionada(String localizacaoFuncaoComissionada) {
        this.localizacaoFuncaoComissionada = localizacaoFuncaoComissionada;
    }

}
