package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;

public class ParticipanteComiteVO implements Serializable {

    private String matricula;
    private String nome;
    private Integer pkCargaParticipante;
    private Integer pkParticipanteAgendaCorec;
    private String email;
    private AgendaCorec agenda;

    public ParticipanteComiteVO() {
    }

    public ParticipanteComiteVO(String matricula, Integer pkParticipanteAgendaCorec, String nome) {
        super();
        this.matricula = matricula;
        this.pkParticipanteAgendaCorec = pkParticipanteAgendaCorec;
        this.nome = nome;
    }

    public ParticipanteComiteVO(String matricula, String nome, Integer pkCargaParticipante, String email) {
        super();
        this.matricula = matricula;
        this.nome = nome;
        this.pkCargaParticipante = pkCargaParticipante;
        this.email = email;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        if (nome == null) {
            ServidorVO servidorvo = BcPessoaAdapter.get().buscarServidor(getMatricula());
            return servidorvo == null ? "" : servidorvo.getNome();
        } else {
            return nome;
        }
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getPkCargaParticipante() {
        return pkCargaParticipante;
    }

    public void setPkCargaParticipante(Integer pkCargaParticipante) {
        this.pkCargaParticipante = pkCargaParticipante;
    }

    public Integer getPkParticipanteAgendaCorec() {
        return pkParticipanteAgendaCorec;
    }

    public void setPkParticipanteAgendaCorec(Integer pkParticipanteAgendaCorec) {
        this.pkParticipanteAgendaCorec = pkParticipanteAgendaCorec;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(matricula).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParticipanteComiteVO) {
            ParticipanteComiteVO participante = (ParticipanteComiteVO) obj;
            return new EqualsBuilder().append(getMatricula(), participante.getMatricula()).isEquals();
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AgendaCorec getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaCorec agenda) {
        this.agenda = agenda;
    }

}
