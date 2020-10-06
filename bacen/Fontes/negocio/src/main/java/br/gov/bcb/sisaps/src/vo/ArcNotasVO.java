package br.gov.bcb.sisaps.src.vo;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;

public class ArcNotasVO extends ObjetoPersistenteVO {

    private BigDecimal valorNota;
    private ParametroNota notaSupervisor;
    private ParametroNota notaCorec;
    private Integer arcVigentePk;
    private ArcNotasVO arcVigente;
    private EstadoARCEnum estado;
    private Integer designacaoPk;
    private Integer delegacaoPk;
    private TipoGrupoEnum tipo;
    private DateTime ultimaAtualizacao;

    public ArcNotasVO() {
    }

    public ArcNotasVO(Integer pk, BigDecimal valorNota, ParametroNota notaSupervisor, ParametroNota notaCorec,
            Integer arcVigentePk, EstadoARCEnum estado, Integer designacaoPk, Integer delegacaoPk, TipoGrupoEnum tipo) {
        this.pk = pk;
        this.valorNota = valorNota;
        this.notaSupervisor = notaSupervisor;
        this.notaCorec = notaCorec;
        this.arcVigentePk = arcVigentePk;
        this.estado = estado;
        this.designacaoPk = designacaoPk;
        this.delegacaoPk = delegacaoPk;
        this.tipo = tipo;
    }

    public ArcNotasVO(Integer pk, BigDecimal valorNota, ParametroNota notaSupervisor, ParametroNota notaCorec,
            Integer arcVigentePk, EstadoARCEnum estado, Integer designacaoPk, Integer delegacaoPk, TipoGrupoEnum tipo,
            DateTime ultimaAtualizacao) {
        this.pk = pk;
        this.valorNota = valorNota;
        this.notaSupervisor = notaSupervisor;
        this.notaCorec = notaCorec;
        this.arcVigentePk = arcVigentePk;
        this.estado = estado;
        this.designacaoPk = designacaoPk;
        this.delegacaoPk = delegacaoPk;
        this.tipo = tipo;
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public BigDecimal getValorNota() {
        return valorNota;
    }

    public void setValorNota(BigDecimal valorNota) {
        this.valorNota = valorNota;
    }

    public ParametroNota getNotaSupervisor() {
        return notaSupervisor;
    }

    public void setNotaSupervisor(ParametroNota notaSupervisor) {
        this.notaSupervisor = notaSupervisor;
    }

    public ParametroNota getNotaCorec() {
        return notaCorec;
    }

    public void setNotaCorec(ParametroNota notaCorec) {
        this.notaCorec = notaCorec;
    }

    public Integer getArcVigentePk() {
        return arcVigentePk;
    }

    public void setArcVigentePk(Integer arcVigentePk) {
        this.arcVigentePk = arcVigentePk;
    }

    public ArcNotasVO getArcVigente() {
        return arcVigente;
    }

    public void setArcVigente(ArcNotasVO arcVigente) {
        this.arcVigente = arcVigente;
    }

    public EstadoARCEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoARCEnum estado) {
        this.estado = estado;
    }

    public Integer getDesignacaoPk() {
        return designacaoPk;
    }

    public void setDesignacaoPk(Integer designacaoPk) {
        this.designacaoPk = designacaoPk;
    }

    public Integer getDelegacaoPk() {
        return delegacaoPk;
    }

    public void setDelegacaoPk(Integer delegacaoPk) {
        this.delegacaoPk = delegacaoPk;
    }

    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }

    public DateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(DateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
    
}
