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

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaARCDesignacaoVO extends Consulta<CicloVO> {

    private Matriz matriz;
    private String nomeAtividade;
    private String nomeGrupoRiscoControle;
    private TipoGrupoEnum tipoGrupo;
    private EstadoARCEnum estadoARC;
    private String matriculaResponsavel;
    private ServidorVO servidor;
    private List<EstadoARCEnum> listaEstados;

    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }

    public String getNomeGrupoRiscoControle() {
        return nomeGrupoRiscoControle;
    }

    public void setNomeGrupoRiscoControle(String nomeGrupoRiscoControle) {
        this.nomeGrupoRiscoControle = nomeGrupoRiscoControle;
    }

    public TipoGrupoEnum getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupoEnum tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public EstadoARCEnum getEstadoARC() {
        return estadoARC;
    }

    public void setEstadoARC(EstadoARCEnum estadoARC) {
        this.estadoARC = estadoARC;
    }

    public String getMatriculaResponsavel() {
        return matriculaResponsavel;
    }

    public void setMatriculaResponsavel(String matriculaResponsavel) {
        this.matriculaResponsavel = matriculaResponsavel;
    }

    public ServidorVO getServidor() {
        return servidor;
    }

    public void setServidor(ServidorVO servidor) {
        this.servidor = servidor;
    }

    public List<EstadoARCEnum> getListaEstados() {
        return listaEstados;
    }

    public void setListaEstados(List<EstadoARCEnum> listaEstados) {
        this.listaEstados = listaEstados;
    }

}