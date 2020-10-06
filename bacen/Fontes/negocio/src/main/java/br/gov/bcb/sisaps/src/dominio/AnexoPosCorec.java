package br.gov.bcb.sisaps.src.dominio;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.IEntidadeJcifs;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;

@Entity
@Table(name = "APC_ANEXO_POS_COREC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoPosCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "APC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "APC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class AnexoPosCorec extends ObjetoPersistenteAuditavelSisAps<Integer> implements IEntidadeJcifs {
    public static final String CAMPO_ID = "APC_ID";
    private static final int TAMANHO_LINK = 200;
    private transient InputStream inputStream;
    private String link;
    private String tipo;
    private Ciclo ciclo;
    private Date dataEntrega;

    @Column(name = "APC_DS_LINK", nullable = false, length = TAMANHO_LINK)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "APC_CD", nullable = false, length = TAMANHO_LINK)
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    public void setDataEntrega(Date dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    @Column(name = "APC_DT_ENTREGA", nullable = true, columnDefinition = "date")
    public Date getDataEntrega() {
        return dataEntrega;
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
    public String getDataEntregaFormatada() {
        return this.dataEntrega == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.dataEntrega);
    }

}
