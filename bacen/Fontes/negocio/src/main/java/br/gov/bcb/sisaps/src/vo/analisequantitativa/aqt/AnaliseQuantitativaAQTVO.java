package br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt;

import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public class AnaliseQuantitativaAQTVO extends ObjetoPersistenteVO {

    public static final String CAMPO_ID = "AQA_ID";
    private EstadoAQTEnum estado;
    private ParametroNotaAQT notaSupervisor;
    private ParametroAQT parametroAQT;
    private Ciclo ciclo;
    private EntidadeSupervisionavel entidadeSupervisionavel;
    private Integer pKVigente;
    private PesoAQT pesoAQT;
    private String responsavel;
    private String acao;
    private String versao;
    private String codOperadorPreenchido;
    private String codOperadorAnalise;
    private DateTime dataConclusao;

    public AnaliseQuantitativaAQTVO(Integer pk, EstadoAQTEnum estado, ParametroAQT parametroAQT, Ciclo ciclo,
            EntidadeSupervisionavel entidadeSupervisionavel, String codOperadorPreenchido, String codOperadorAnalise,
            DateTime dataConclusao) {
        this.pk = pk;
        this.estado = estado;
        this.parametroAQT = parametroAQT;
        this.ciclo = ciclo;
        this.entidadeSupervisionavel = entidadeSupervisionavel;
        this.codOperadorPreenchido = codOperadorPreenchido;
        this.codOperadorAnalise = codOperadorAnalise;
        this.dataConclusao = dataConclusao;
    }

    public AnaliseQuantitativaAQTVO(Integer pk, EstadoAQTEnum estado, ParametroAQT parametroAQT, Ciclo ciclo,
            EntidadeSupervisionavel entidadeSupervisionavel) {
        this.pk = pk;
        this.estado = estado;
        this.parametroAQT = parametroAQT;
        this.ciclo = ciclo;
        this.entidadeSupervisionavel = entidadeSupervisionavel;

    }

    public EstadoAQTEnum getEstadoCiclo() {
        return estado;
    }

    public void setEstado(EstadoAQTEnum estado) {
        this.estado = estado;
    }

    public ParametroNotaAQT getNotaSupervisor() {
        return notaSupervisor;
    }

    public void setNotaSupervisor(ParametroNotaAQT notaSupervisor) {
        this.notaSupervisor = notaSupervisor;
    }

    public ParametroAQT getParametroAQT() {
        return parametroAQT;
    }

    public void setParametroAQT(ParametroAQT parametroAQT) {
        this.parametroAQT = parametroAQT;
    }


    public Integer getpKVigente() {
        return pKVigente;
    }

    public void setpKVigente(Integer pKVigente) {
        this.pKVigente = pKVigente;
    }

    public PesoAQT getPesoAQT() {
        return pesoAQT;
    }

    public void setPesoAQT(PesoAQT pesoAQT) {
        this.pesoAQT = pesoAQT;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public EntidadeSupervisionavel getEntidadeSupervisionavel() {
        return entidadeSupervisionavel;
    }

    public void setEntidadeSupervisionavel(EntidadeSupervisionavel entidadeSupervisionavel) {
        this.entidadeSupervisionavel = entidadeSupervisionavel;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getCodOperadorPreenchido() {
        return codOperadorPreenchido;
    }

    public void setCodOperadorPreenchido(String codOperadorPreenchido) {
        this.codOperadorPreenchido = codOperadorPreenchido;
    }

    public String getCodOperadorAnalise() {
        return codOperadorAnalise;
    }

    public void setCodOperadorAnalise(String codOperadorAnalise) {
        this.codOperadorAnalise = codOperadorAnalise;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public DateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(DateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public EstadoAQTEnum getEstado() {
        return estado;
    }
    

}
