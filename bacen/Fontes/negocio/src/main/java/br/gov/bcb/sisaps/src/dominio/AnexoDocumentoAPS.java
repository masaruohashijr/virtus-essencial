/*
 * Sistema Auditar
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import java.io.InputStream;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;

@Entity
@Table(schema = "SUP", name = "ADA_ANEXO_DOCUMENTO_ARQ")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnexoDocumentoAPS.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ADA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ADA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class AnexoDocumentoAPS extends ObjetoPersistenteAuditavelSisAps<Integer> {

    public static final String CAMPO_ID = "ADA_ID";
    
    private String link;
    private byte[] arquivo;
    private DocumentoAPS documentoAps;
    private transient InputStream inputStream;
    private String excluido;

    @Transient
    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Column(name = "ADA_DS_NOME_ARQ")
    public String getLink() {
        return link;
    }

    @Lob
    @Column(name = "ADA_BI_ARQUIVO")
    public byte[] getArquivo() {
        return arquivo;
    }

    @JoinColumn(name = "DOA_ID", referencedColumnName = "DOA_ID", nullable = false)
    @ManyToOne(targetEntity = DocumentoAPS.class, optional = false)
    public DocumentoAPS getDocumentoAps() {
        return documentoAps;
    }

    @Column(name = "ADA_CD_EXCLUIDO", nullable = true, length = 1)
    public String getExcluido() {
        return excluido;
    }

    public void setExcluido(String excluido) {
        this.excluido = excluido;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    public void setDocumentoAps(DocumentoAPS documentoAps) {
        this.documentoAps = documentoAps;
    }

}
