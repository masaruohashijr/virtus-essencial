package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(schema = "SUP", name = "PRT_PERFIL_ATUACAO_CON")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = PerfilAtuacaoConsolidado.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PRT_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PRT_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "PRT_NU_VERSAO"))})
public class PerfilAtuacaoConsolidado extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "PRT_ID";

    private Consolidado consolidado;
    private DocumentoAPS documento;
    private PerfilAtuacaoConsolidado perfilPosterior;
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

    @JoinColumn(name = "DOA_ID", referencedColumnName = "DOA_ID")
    @ManyToOne(targetEntity = DocumentoAPS.class, optional = true, fetch = FetchType.EAGER)
    public DocumentoAPS getDocumento() {
        return documento;
    }

    @JoinColumn(name = "PRT_ID_POSTERIOR", referencedColumnName = "PRT_ID", nullable = true)
    @OneToOne(targetEntity = PerfilAtuacaoConsolidado.class, optional = true)
    public PerfilAtuacaoConsolidado getPerfilPosterior() {
        return perfilPosterior;
    }

    @Column(name = "PRT_CD_STATUS", nullable = false, length = 1, columnDefinition = "varchar(1)")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = TipoStatusDocumento.CLASS_NAME)})
    public TipoStatusDocumento getStatus() {
        return status;
    }

    @Column(name = "PRT_CD_BLOQUEADO", nullable = true, length = 1, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getBloqueado() {
        return bloqueado;
    }

    @Column(name = "PRT_CD_OPER_ENCAMINHAMENTO", length = 20)
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    @Column(name = "PRT_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    @Column(name = "PRT_CD_OPER_PUBLICACAO", length = 20)
    public String getOperadorPublicacao() {
        return operadorPublicacao;
    }

    @Column(name = "PRT_DH_PUBLICACAO")
    public DateTime getDataPublicacao() {
        return dataPublicacao;
    }

    @Column(name = "PRT_CD_OPER_ALTERACAO", length = 20)
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    @Column(name = "PRT_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setConsolidado(Consolidado consolidado) {
        this.consolidado = consolidado;
    }

    public void setDocumento(DocumentoAPS documento) {
        this.documento = documento;
    }

    public void setPerfilPosterior(PerfilAtuacaoConsolidado perfilPosterior) {
        this.perfilPosterior = perfilPosterior;
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