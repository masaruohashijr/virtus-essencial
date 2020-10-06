package br.gov.bcb.sisaps.src.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "AGC_AGENDA_COREC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AgendaCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AGC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AGC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "AGC_NU_VERSAO", nullable = false))})
public class AgendaCorec extends ObjetoPersistenteAuditavelVersionado<Integer> {


    public static final String CAMPO_ID = "AGC_ID";
    private static final String AGENDA = "agenda";

    private Ciclo ciclo;
    private String local;
    private DateTime horaCorec;
    private DateTime dataEnvioApresentacao;
    private DateTime dataEnvioDisponibilidade;
    private List<ObservacaoAgendaCorec> observacoes;
    private List<ParticipanteAgendaCorec> participantes;
    
    @OneToMany(mappedBy = AGENDA, fetch = FetchType.LAZY)
    public List<ParticipanteAgendaCorec> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<ParticipanteAgendaCorec> participantes) {
        this.participantes = participantes;
    }
    
    
    @OneToMany(mappedBy = AGENDA, fetch = FetchType.LAZY,  cascade = CascadeType.ALL)
    public List<ObservacaoAgendaCorec> getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(List<ObservacaoAgendaCorec> observacoes) {
        this.observacoes = observacoes;
    }
    
    public void addObservacao(ObservacaoAgendaCorec observacao) {
        if (observacoes == null) {
            List<ObservacaoAgendaCorec> versoes = new ArrayList<ObservacaoAgendaCorec>();
            versoes.add(observacao);
            this.setObservacoes(versoes);
        } else {
            observacoes.add(observacao);
        }
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @Column(name = "AGC_DS_LOCAL", length = 100)
    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    @Column(name = "AGC_HR_COREC")
    public DateTime getHoraCorec() {
        return horaCorec;
    }

    public void setHoraCorec(DateTime horaCorec) {
        this.horaCorec = horaCorec;
    }
    
    
    @Transient
    public String getHoraCorecFormatada() {
        return this.getHoraCorec() == null ? "" : this.getHoraCorec().toString(Constantes.FORMATO_HORA_AGENDA);
    }

    @Column(name = "AGC_DH_EMAIL_APRESENTACAO")
    public DateTime getDataEnvioApresentacao() {
        return dataEnvioApresentacao;
    }

    public void setDataEnvioApresentacao(DateTime dataEnvioApresentacao) {
        this.dataEnvioApresentacao = dataEnvioApresentacao;
    }
    
    @Transient
    public String getDataEnvioApresentacaoFormatada() {
        return this.getDataEnvioApresentacao() == null ? "" : this.getDataEnvioApresentacao().toString(
                Constantes.FORMATO_DATA_COM_BARRAS);
    }
    

    @Column(name = "AGC_DH_EMAIL_DISPONIBILIDADE")
    public DateTime getDataEnvioDisponibilidade() {
        return dataEnvioDisponibilidade;
    }

    public void setDataEnvioDisponibilidade(DateTime dataEnvioDisponibilidade) {
        this.dataEnvioDisponibilidade = dataEnvioDisponibilidade;
    }
    
    
    @Transient
    public String getDataEnvioDisponibilidadeFormatada() {
        return this.getDataEnvioDisponibilidade() == null ? "" : this.getDataEnvioDisponibilidade().toString(
                Constantes.FORMATO_DATA_COM_BARRAS);
    }

}
