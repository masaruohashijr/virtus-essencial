package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;

public class ARCDesignacaoVO extends ObjetoPersistenteVO {

    private Integer pkAtividade;
    private String nomeAtividade;
    private String nomeGrupoRiscoControle;
    private TipoGrupoEnum tipoGrupoRiscoControle;
    private EstadoARCEnum estado;
    private Integer pkDesignacao;
    private String matriculaResponsavel;
    private Integer pkMatriz;
    
    public ARCDesignacaoVO() {
        super();
    }
    
    public ARCDesignacaoVO(Integer pk, Integer pkMatriz, Integer pkAtividade, String nomeAtividade, 
            String nomeGrupoRiscoControle, TipoGrupoEnum tipoGrupoRiscoControle, 
            EstadoARCEnum estado, Integer pkDesignacao, String matriculaResponsavel) {
        this.pk = pk;
        this.pkMatriz = pkMatriz;
        this.pkAtividade = pkAtividade;
        this.nomeAtividade = nomeAtividade;
        this.nomeGrupoRiscoControle = nomeGrupoRiscoControle;
        this.tipoGrupoRiscoControle = tipoGrupoRiscoControle;
        this.estado = estado;
        this.pkDesignacao = pkDesignacao;
        this.matriculaResponsavel = matriculaResponsavel;
    }
    
    public ARCDesignacaoVO(Integer pk, Integer pkMatriz, String nomeGrupoRiscoControle, 
            TipoGrupoEnum tipoGrupoRiscoControle, EstadoARCEnum estado, Integer pkDesignacao, 
            String matriculaResponsavel) {
        this(pk, pkMatriz, null, null, nomeGrupoRiscoControle, tipoGrupoRiscoControle, 
                estado, pkDesignacao, matriculaResponsavel);
    }
    
    public ARCDesignacaoVO(Integer pk) {
        this.pk = pk;
    }
    
    public Integer getPkMatriz() {
        return pkMatriz;
    }

    public void setPkMatriz(Integer pkMatriz) {
        this.pkMatriz = pkMatriz;
    }

    public Integer getPkAtividade() {
        return pkAtividade;
    }

    public void setPkAtividade(Integer pkAtividade) {
        this.pkAtividade = pkAtividade;
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
    
    public TipoGrupoEnum getTipoGrupoRiscoControle() {
        return tipoGrupoRiscoControle;
    }

    public void setTipoGrupoRiscoControle(TipoGrupoEnum tipoGrupoRiscoControle) {
        this.tipoGrupoRiscoControle = tipoGrupoRiscoControle;
    }
    
    public EstadoARCEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoARCEnum estado) {
        this.estado = estado;
    }

    public Integer getPkDesignacao() {
        return pkDesignacao;
    }

    public void setPkDesignacao(Integer pkDesignacao) {
        this.pkDesignacao = pkDesignacao;
    }

    public String getMatriculaResponsavel() {
        return matriculaResponsavel;
    }
    
    public void setMatriculaResponsavel(String matriculaResponsavel) {
        this.matriculaResponsavel = matriculaResponsavel;
    }
    
    public String getResponsavel() {
        if (matriculaResponsavel == null) {
            return "";
        } else {
            return BcPessoaAdapter.get().buscarServidor(matriculaResponsavel).getNome();
        }
    }

}
