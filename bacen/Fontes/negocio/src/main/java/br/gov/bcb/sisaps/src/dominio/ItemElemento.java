package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "ITE_ITEM_ELEMENTO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ItemElemento.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ITE_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ITE_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "ITE_NU_VERSAO", nullable = false))})
public class ItemElemento extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "ITE_ID";
    private Elemento elemento;
    private ParametroItemElemento parametroItemElemento;
    private Documento documento;
    private DateTime dataAlteracao;
    private String operadorAlteracao;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = Elemento.CAMPO_ID, nullable = false)
    public Elemento getElemento() {
        return elemento;
    }
    
    public void setElemento(Elemento elemento) {
        this.elemento = elemento;
    }
    
    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroItemElemento.CAMPO_ID, nullable = false)
    public ParametroItemElemento getParametroItemElemento() {
        return parametroItemElemento;
    }
    
    public void setParametroItemElemento(ParametroItemElemento parametroItemElemento) {
        this.parametroItemElemento = parametroItemElemento;
    }
    
    @ManyToOne
    @JoinColumn(name = Documento.CAMPO_ID)
    public Documento getDocumento() {
        return documento;
    }
    
    public void setDocumento(Documento documento) {
        this.documento = documento;
    }
    
    @Column(name = "ITE_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Column(name = "ITE_CD_OPER_ALTERACAO")
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    public void setOperadorAlteracao(String operadorAlteracao) {
        this.operadorAlteracao = operadorAlteracao;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPk()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemElemento) {
            ItemElemento itemElemento = (ItemElemento) obj;
            return new EqualsBuilder().append(getPk(), itemElemento.getPk()).isEquals();
        }
        return false;
    }
    
}
