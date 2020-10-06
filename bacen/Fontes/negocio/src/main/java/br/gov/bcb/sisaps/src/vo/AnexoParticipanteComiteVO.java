package br.gov.bcb.sisaps.src.vo;

import java.io.InputStream;
import java.io.Serializable;

import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoParticipanteComiteEnum;

public class AnexoParticipanteComiteVO extends ObjetoPersistenteAuditavelVO implements Serializable {

    private String nome;
    private DateTime dataHoraUpload;
    private DateTime dataHoraProcessamento;
    private transient InputStream inputStream;
    private EstadoParticipanteComiteEnum status;

    public AnexoParticipanteComiteVO() {
        // TODO Auto-generated constructor stub
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public DateTime getDataHoraUpload() {
        return dataHoraUpload;
    }

    public void setDataHoraUpload(DateTime dataHoraUpload) {
        this.dataHoraUpload = dataHoraUpload;
    }

    public DateTime getDataHoraProcessamento() {
        return dataHoraProcessamento;
    }

    public void setDataHoraProcessamento(DateTime dataHoraProcessamento) {
        this.dataHoraProcessamento = dataHoraProcessamento;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public EstadoParticipanteComiteEnum getStatus() {
        return status;
    }

    public void setStatus(EstadoParticipanteComiteEnum status) {
        this.status = status;
    }


}
