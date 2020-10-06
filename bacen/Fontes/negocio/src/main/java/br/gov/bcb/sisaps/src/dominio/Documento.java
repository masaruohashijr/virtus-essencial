package br.gov.bcb.sisaps.src.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "DOC_DOCUMENTO_SRC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Documento.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "DOC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "DOC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "DOC_NU_VERSAO", nullable = false))})
public class Documento extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "DOC_ID";
    private String justificativa;
    private DateTime dataAlteracao;
    private String operadorAlteracao;
    private List<AnexoDocumento> anexosDocumento = new ArrayList<AnexoDocumento>();

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "DOC_AN_JUSTIFICATIVA")
    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @OneToMany(mappedBy = "documento", fetch = FetchType.EAGER)
    public List<AnexoDocumento> getAnexosItemElemento() {
        return anexosDocumento;
    }

    public void setAnexosItemElemento(List<AnexoDocumento> anexosItemElemento) {
        this.anexosDocumento = anexosItemElemento;
    }

    @Transient
    public String getJustificativaDetalhe() {
        if (justificativa == null || justificativa.isEmpty()) {
            return "";
        }
        return justificativa;
    }
    
    @Column(name = "DOC_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Column(name = "DOC_CD_OPER_ALTERACAO")
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    public void setOperadorAlteracao(String operadorAlteracao) {
        this.operadorAlteracao = operadorAlteracao;
    }
    
    

}
