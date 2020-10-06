package br.gov.bcb.sisaps.src.dominio;

import java.io.InputStream;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.IEntidadeJcifs;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "APA_ARQUIVO_PARTICIPANTES_COMITE", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoParticipanteComite.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "APA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "APA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "APA_NU_VERSAO", nullable = false))})
public class AnexoParticipanteComite extends ObjetoPersistenteAuditavelVersionado<Integer> implements IEntidadeJcifs {

    public static final String CAMPO_ID = "APA_ID";
    public static final String PROP_DOCUMENTO = "documento";

    private String nome;
    private DateTime dataHoraUpload;
    private transient InputStream inputStream;

    @Column(name = "APA_NM", nullable = false, length = 200)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "APA_DH_UPLOAD")
    public DateTime getDataHoraUpload() {
        return dataHoraUpload;
    }

    public void setDataHoraUpload(DateTime dataHoraUpload) {
        this.dataHoraUpload = dataHoraUpload;
    }

    @Override
    @Transient
    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Transient
    public String getDataUploadFormatada() {
        return dataHoraUpload.toString("dd/MM/yyyy HH:mm:ss");
    }

}
