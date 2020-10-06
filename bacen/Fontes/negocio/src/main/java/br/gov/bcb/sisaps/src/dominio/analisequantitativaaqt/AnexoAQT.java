package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import java.io.InputStream;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.IEntidadeJcifs;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;

@Entity
@Table(name = "AAQ_ANEXO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AAQ_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AAQ_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class AnexoAQT extends ObjetoPersistenteAuditavelSisAps<Integer> implements IEntidadeJcifs {
    public static final String CAMPO_ID = "AAQ_ID";
    private static final int TAMANHO_LINK = 200;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private String link;
    private transient InputStream inputStream;


    @ManyToOne(optional = false)
    @JoinColumn(name = AnaliseQuantitativaAQT.CAMPO_ID, nullable = false)
    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    @Column(name = "AAQ_DS_LINK", nullable = false, length = TAMANHO_LINK)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    @Transient
    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
