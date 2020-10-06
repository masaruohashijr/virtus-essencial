package br.gov.bcb.sisaps.src.vo;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAvaliacaoRiscoControleVO extends Consulta<AvaliacaoRiscoControleVO> {

    private AtividadeVO atividade;
    private String matriculaServidorDesignado;
    private String nomeAtividade;
    private String nomeGrupoRiscoControle;
    private TipoGrupoEnum tipoGrupo;
    private EstadoARCEnum estadoARC;
    private List<EstadoARCEnum> listaEstados;
    private String nomeES;
    private boolean isAcao;
    private List<EstadoARCEnum> estados;
    private boolean isTeste;

    public AtividadeVO getAtividade() {
        return atividade;
    }

    public void setAtividade(AtividadeVO atividade) {
        this.atividade = atividade;
    }

    public String getMatriculaServidorDesignado() {
        return matriculaServidorDesignado;
    }

    public void setMatriculaServidorDesignado(String matriculaServidorDesignado) {
        this.matriculaServidorDesignado = matriculaServidorDesignado;
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

    public List<EstadoARCEnum> getListaEstados() {
        return listaEstados;
    }

    public void setListaEstados(List<EstadoARCEnum> listaEstados) {
        this.listaEstados = listaEstados;
    }

    public String getNomeES() {
        return nomeES;
    }

    public void setNomeES(String nomeES) {
        this.nomeES = nomeES;
    }

    public boolean isAcao() {
        return isAcao;
    }

    public void setAcao(boolean isAcao) {
        this.isAcao = isAcao;
    }

    public List<EstadoARCEnum> getEstados() {
        return estados;
    }

    public void setEstados(List<EstadoARCEnum> estados) {
        this.estados = EstadoARCEnum.listaEstados();
    }

    public boolean isTeste() {
        return isTeste;
    }

    public void setTeste(boolean isTeste) {
        this.isTeste = isTeste;
    }
    
    

}
