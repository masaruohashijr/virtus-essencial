package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;


public class SinteseRiscoRevisaoVO implements Serializable {

    private Integer pkArc;
    private Integer pkEntidadeSupervisionavel;
    private String nomeEntidadeSupervisionavel;
    private String nomeParametroGrupoRiscoControle;
    private Integer pkParametroGrupoRiscoControle;
    private Long arcsPendentesPublicacao;
    
    public SinteseRiscoRevisaoVO(Integer pkEntidadeSupervisionavel, String nomeEntidadeSupervisionavel,
            Integer pkParametroGrupoRiscoControle, String nomeParametroGrupoRiscoControle,
            Long arcsPendentesPublicacao) {
        this.pkEntidadeSupervisionavel = pkEntidadeSupervisionavel;
        this.nomeEntidadeSupervisionavel = nomeEntidadeSupervisionavel;
        this.nomeParametroGrupoRiscoControle = nomeParametroGrupoRiscoControle;
        this.pkParametroGrupoRiscoControle = pkParametroGrupoRiscoControle;
        this.arcsPendentesPublicacao = arcsPendentesPublicacao;
    }

    public SinteseRiscoRevisaoVO(Integer pkArc, Integer pkEntidadeSupervisionavel, String nomeEntidadeSupervisionavel,
            Integer pkParametroGrupoRiscoControle, String nomeParametroGrupoRiscoControle,
            Long arcsPendentesPublicacao) {
        this(pkEntidadeSupervisionavel, nomeEntidadeSupervisionavel, pkParametroGrupoRiscoControle, nomeParametroGrupoRiscoControle, arcsPendentesPublicacao);
        this.pkArc = pkArc;
    }

    public Integer getPkEntidadeSupervisionavel() {
        return pkEntidadeSupervisionavel;
    }

    public void setPkEntidadeSupervisionavel(Integer pkEntidadeSupervisionavel) {
        this.pkEntidadeSupervisionavel = pkEntidadeSupervisionavel;
    }

    public String getNomeEntidadeSupervisionavel() {
        return nomeEntidadeSupervisionavel;
    }

    public void setNomeEntidadeSupervisionavel(String nomeEntidadeSupervisionavel) {
        this.nomeEntidadeSupervisionavel = nomeEntidadeSupervisionavel;
    }

    public String getNomeParametroGrupoRiscoControle() {
        return nomeParametroGrupoRiscoControle;
    }

    public void setNomeParametroGrupoRiscoControle(String nomeParametroGrupoRiscoControle) {
        this.nomeParametroGrupoRiscoControle = nomeParametroGrupoRiscoControle;
    }

    public Integer getPkParametroGrupoRiscoControle() {
        return pkParametroGrupoRiscoControle;
    }

    public void setPkParametroGrupoRiscoControle(Integer pkParametroGrupoRiscoControle) {
        this.pkParametroGrupoRiscoControle = pkParametroGrupoRiscoControle;
    }

    public Long getArcsPendentesPublicacao() {
        return arcsPendentesPublicacao;
    }

    public void setArcsPendentesPublicacao(Long arcsPendentesPublicacao) {
        this.arcsPendentesPublicacao = arcsPendentesPublicacao;
    }

    public Integer getPkArc() {
        return pkArc;
    }

    public void setPkArc(Integer pkArc) {
        this.pkArc = pkArc;
    }
    
    

}
