package br.gov.bcb.sisaps.src.dominio;

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
@Table(name = "ANA_ANEXO_ARC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoARC.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ANA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ANA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class AnexoARC extends ObjetoPersistenteAuditavelSisAps<Integer> implements IEntidadeJcifs {
    public static final String CAMPO_ID = "ANA_ID";
    private static final int TAMANHO_LINK = 200;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private String link;
    private transient InputStream inputStream;

    @ManyToOne(optional = false)
    @JoinColumn(name = AvaliacaoRiscoControle.CAMPO_ID, nullable = false)
    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }

    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

    @Column(name = "ANA_DS_LINK", nullable = false, length = TAMANHO_LINK)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Transient
    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
