package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "ITA_ITEM_ELEMENTO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ItemElementoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ITA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ITA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "ITA_NU_VERSAO", nullable = false))})
public class ItemElementoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "ITA_ID";
    private ElementoAQT elemento;
    private ParametroItemElementoAQT parametroItemElemento;
    private Documento documento;
    private DateTime dataAlteracao;
    private String operadorAlteracao;

    @ManyToOne(optional = false)
    @JoinColumn(name = ElementoAQT.CAMPO_ID, nullable = false)
    public ElementoAQT getElemento() {
        return elemento;
    }

    public void setElemento(ElementoAQT elemento) {
        this.elemento = elemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroItemElementoAQT.CAMPO_ID, nullable = false)
    public ParametroItemElementoAQT getParametroItemElemento() {
        return parametroItemElemento;
    }

    public void setParametroItemElemento(ParametroItemElementoAQT parametroItemElemento) {
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

    @Column(name = "ITA_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Column(name = "ITA_CD_OPER_ALTERACAO")
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    public void setOperadorAlteracao(String operadorAlteracao) {
        this.operadorAlteracao = operadorAlteracao;
    }

    @Override
    @Transient
    public String getData(DateTime data) {
        return data == null ? "" : data.toString(Constantes.FORMATO_DATA_COM_BARRAS);
    }

}
