package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;

public class ArcResumidoVO extends ObjetoPersistenteVO {

    private String nomeES;
    private String nomeAtividade;
    private String nomeGrupo;
    private TipoGrupoEnum tipo;
    private EstadoARCEnum estado;
    private Integer pkAtividade;
    private Integer pkMatriz;

    public ArcResumidoVO() {

    }

    public ArcResumidoVO(Integer pk, String nomeES, String nomeAtividade, String nomeGrupo,
            TipoGrupoEnum tipo, EstadoARCEnum estado, Integer pkAtividade, Integer pkMatriz) {
        this.pk = pk;
        this.nomeES = nomeES;
        this.nomeAtividade = nomeAtividade;
        this.nomeGrupo = nomeGrupo;
        this.tipo = tipo;
        this.estado = estado;
        this.pkAtividade = pkAtividade;
        this.pkMatriz = pkMatriz;
    }
    
    public ArcResumidoVO(Integer pk, String nomeES, String nomeGrupo, TipoGrupoEnum tipo, EstadoARCEnum estado,
            Integer pkMatriz) {
        this.pk = pk;
        this.nomeES = nomeES;
        this.nomeGrupo = nomeGrupo;
        this.tipo = tipo;
        this.estado = estado;
        this.pkMatriz = pkMatriz;
    }

    public String getNomeES() {
        return nomeES;
    }

    public void setNomeES(String nomeES) {
        this.nomeES = nomeES;
    }

    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }

    public EstadoARCEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoARCEnum estado) {
        this.estado = estado;
    }

    public Integer getPkAtividade() {
        return pkAtividade;
    }

    public void setPkAtividade(Integer pkAtividade) {
        this.pkAtividade = pkAtividade;
    }

    public Integer getPkMatriz() {
        return pkMatriz;
    }

    public void setPkMatriz(Integer pkMatriz) {
        this.pkMatriz = pkMatriz;
    }

}
