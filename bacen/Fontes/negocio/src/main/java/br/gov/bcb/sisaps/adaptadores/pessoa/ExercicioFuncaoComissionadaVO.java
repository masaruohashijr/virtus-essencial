/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
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
