package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoStatusDocumento;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;


@Entity
@Table(schema = "SUP", name = "OCC_OPINIAO_CONCLUSIVA_CON")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = OpiniaoConclusivaConsolidado.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "OCC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "OCC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "OCC_NU_VERSAO"))})
public class OpiniaoConclusivaConsolidado extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "OCC_ID";

    private Consolidado consolidado;
    private DocumentoAPS documento;
    private OpiniaoConclusivaConsolidado opiniaoPosterior;
    private TipoStatusDocumento status;
    private SimNaoEnum bloqueado;
    private String operadorEncaminhamento;
    private DateTime dataEncaminhamento;
    private String operadorPublicacao;
    private DateTime dataPublicacao;
    private String operadorAlteracao;
    private DateTime dataAlteracao;

    @JoinColumn(name = "CON_ID", referencedColumnName = "CON_ID", nullable = false)
    @ManyToOne(targetEntity = Consolidado.class, optional = false)
    public Consolidado getConsolidado() {
        return consolidado;
    }

    @JoinColumn(name = "DOA_ID", referencedColumnName = "DOA_ID", nullable = true)
    @ManyToOne(targetEntity = DocumentoAPS.class, optional = true)
    public DocumentoAPS getDocumento() {
        return documento;
    }

    @JoinColumn(name = "OCC_ID_POSTERIOR", referencedColumnName = "OCC_ID", nullable = true)
    @OneToOne(targetEntity = OpiniaoConclusivaConsolidado.class, optional = true)
    public OpiniaoConclusivaConsolidado getOpiniaoPosterior() {
        return opiniaoPosterior;
    }

    @Column(name = "OCC_CD_STATUS", nullable = false, length = 1, columnDefinition = "varchar(1)")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = TipoStatusDocumento.CLASS_NAME)})
    public TipoStatusDocumento getStatus() {
        return status;
    }

    @Column(name = "OCC_CD_BLOQUEADO", nullable = true, length = 1, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getBloqueado() {
        return bloqueado;
    }

    @Column(name = "OCC_CD_OPER_ENCAMINHAMENTO", length = 20)
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    @Column(name = "OCC_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    @Column(name = "OCC_CD_OPER_PUBLICACAO", length = 20)
    public String getOperadorPublicacao() {
        return operadorPublicacao;
    }

    @Column(name = "OCC_DH_PUBLICACAO")
    public DateTime getDataPublicacao() {
        return dataPublicacao;
    }

    @Column(name = "OCC_CD_OPER_ALTERACAO", length = 20)
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    @Column(name = "OCC_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setConsolidado(Consolidado consolidado) {
        this.consolidado = consolidado;
    }

    public void setDocumento(DocumentoAPS documento) {
        this.documento = documento;
    }

    public void setOpiniaoPosterior(OpiniaoConclusivaConsolidado opiniaoPosterior) {
        this.opiniaoPosterior = opiniaoPosterior;
    }

    public void setStatus(TipoStatusDocumento status) {
        this.status = status;
    }

    public void setBloqueado(SimNaoEnum bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setOperadorEncaminhamento(String operadorEncaminhamento) {
        this.operadorEncaminhamento = operadorEncaminhamento;
    }

    public void setDataEncaminhamento(DateTime dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    public void setOperadorPublicacao(String operadorPublicacao) {
        this.operadorPublicacao = operadorPublicacao;
    }

    public void setDataPublicacao(DateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public void setOperadorAlteracao(String operadorAlteracao) {
        this.operadorAlteracao = operadorAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
    
    
    

}
