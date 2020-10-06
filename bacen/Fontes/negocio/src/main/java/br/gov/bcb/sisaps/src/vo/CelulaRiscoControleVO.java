package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;

public class CelulaRiscoControleVO extends ObjetoPersistenteVO {

    private Integer atividadePk;
    private Integer parametroGrupoPk;
    private Short ordemParametroGrupo;
    private Integer arcRiscoPk;
    private Integer arcControlePk;
    private Short valorPeso;
    private Integer tipoAtividade;
    private Integer unidadePk;
    private Integer matrizPk;
    private Integer cicloPk;
    private Integer metodologiaPk;
    private Short valorPesoAtividade;
    private Short valorPesoUnidade;
    private TipoUnidadeAtividadeEnum tipoUnidade;

    public CelulaRiscoControleVO(Integer pk, Integer atividadePk, Integer parametroGrupoPk, Short ordemParametroGrupo,
            Integer arcRiscoPk, Integer arcControlePk, Short valorPeso, Integer tipoAtividade, Integer unidadePk,
            Integer matrizPk, Integer cicloPk, Integer metodologiaPk, Short valorPesoAtividade, Short valorPesoUnidade,
            TipoUnidadeAtividadeEnum tipoUnidade) {
        this.pk = pk;
        this.atividadePk = atividadePk;
        this.parametroGrupoPk = parametroGrupoPk;
        this.ordemParametroGrupo = ordemParametroGrupo;
        this.arcRiscoPk = arcRiscoPk;
        this.arcControlePk = arcControlePk;
        this.valorPeso = valorPeso;
        this.tipoAtividade = tipoAtividade;
        this.unidadePk = unidadePk;
        this.matrizPk = matrizPk;
        this.cicloPk = cicloPk;
        this.metodologiaPk = metodologiaPk;
        this.valorPesoAtividade = valorPesoAtividade;
        this.valorPesoUnidade = valorPesoUnidade;
        this.tipoUnidade = tipoUnidade;
    }

    public CelulaRiscoControleVO(Integer pk) {
        this.pk = pk;
    }

    public Integer getAtividadePk() {
        return atividadePk;
    }

    public void setAtividadePk(Integer atividadePk) {
        this.atividadePk = atividadePk;
    }

    public Integer getParametroGrupoPk() {
        return parametroGrupoPk;
    }

    public void setParametroGrupoPk(Integer parametroGrupoPk) {
        this.parametroGrupoPk = parametroGrupoPk;
    }

    public Integer getArcRiscoPk() {
        return arcRiscoPk;
    }

    public void setArcRiscoPk(Integer arcRiscoPk) {
        this.arcRiscoPk = arcRiscoPk;
    }

    public Integer getArcControlePk() {
        return arcControlePk;
    }

    public void setArcControlePk(Integer arcControlePk) {
        this.arcControlePk = arcControlePk;
    }

    public Short getValorPeso() {
        return valorPeso;
    }

    public void setValorPeso(Short valorPeso) {
        this.valorPeso = valorPeso;
    }

    public Integer getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(Integer tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    public Integer getUnidadePk() {
        return unidadePk;
    }

    public void setUnidadePk(Integer unidadePk) {
        this.unidadePk = unidadePk;
    }

    public Integer getMatrizPk() {
        return matrizPk;
    }

    public void setMatrizPk(Integer matrizPk) {
        this.matrizPk = matrizPk;
    }

    public Integer getCicloPk() {
        return cicloPk;
    }

    public void setCicloPk(Integer cicloPk) {
        this.cicloPk = cicloPk;
    }

    public Integer getMetodologiaPk() {
        return metodologiaPk;
    }

    public void setMetodologiaPk(Integer metodologiaPk) {
        this.metodologiaPk = metodologiaPk;
    }

    public Short getOrdemParametroGrupo() {
        return ordemParametroGrupo;
    }

    public void setOrdemParametroGrupo(Short ordemParametroGrupo) {
        this.ordemParametroGrupo = ordemParametroGrupo;
    }

    public Short getValorPesoAtividade() {
        return valorPesoAtividade;
    }

    public void setValorPesoAtividade(Short valorPesoAtividade) {
        this.valorPesoAtividade = valorPesoAtividade;
    }

    public TipoUnidadeAtividadeEnum getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidadeAtividadeEnum tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public Short getValorPesoUnidade() {
        return valorPesoUnidade;
    }

    public void setValorPesoUnidade(Short valorPesoUnidade) {
        this.valorPesoUnidade = valorPesoUnidade;
    }

}
