package br.gov.bcb.sisaps.src.dominio;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(schema = "SUP", name = "DOA_DOCUMENTO_APS")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = DocumentoAPS.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "DOA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "DOA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "DOA_NU_VERSAO"))})
public class DocumentoAPS extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "DOA_ID";

    private String justificativa;
    private List<AnexoDocumentoAPS> anexosDocumentoAps;

    @Lob
    @Column(name = "DOA_AN_CONTEUDO")
    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @OneToMany(mappedBy = "documentoAps", fetch = FetchType.EAGER)
    public List<AnexoDocumentoAPS> getAnexosDocumentoAps() {
        return anexosDocumentoAps;
    }

    public void setAnexosDocumentoAps(List<AnexoDocumentoAPS> anexosDocumentoAps) {
        this.anexosDocumentoAps = anexosDocumentoAps;
    }

}
