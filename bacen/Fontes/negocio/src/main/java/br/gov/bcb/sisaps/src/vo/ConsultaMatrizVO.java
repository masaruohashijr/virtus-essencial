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
package br.gov.bcb.sisaps.src.vo;

import java.util.Set;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.EstadoMatriz;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaMatrizVO extends Consulta<MatrizVO> {

    private EstadoMatriz estadoMatriz;
    private Set<Unidade> unidades;
    private Set<Atividade> atividades;

    public EstadoMatriz getEstadoMatriz() {
        return estadoMatriz;
    }

    public void setEstadoMatriz(EstadoMatriz estadoMatriz) {
        this.estadoMatriz = estadoMatriz;
    }

    public Set<Unidade> getUnidades() {
        return unidades;
    }

    public void setUnidades(Set<Unidade> unidades) {
        this.unidades = unidades;
    }

    public Set<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(Set<Atividade> atividades) {
        this.atividades = atividades;
    }

}