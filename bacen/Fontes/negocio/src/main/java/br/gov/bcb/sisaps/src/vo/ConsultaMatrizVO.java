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