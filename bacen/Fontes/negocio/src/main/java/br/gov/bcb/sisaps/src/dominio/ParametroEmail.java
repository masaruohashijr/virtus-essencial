package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PEM_PAR_EMAIL", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroEmail.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PEM_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PEM_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PEM_NU_VERSAO", nullable = false))})
public class ParametroEmail extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "PEM_ID";

    private TipoEmailCorecEnum tipo;
    private String remetente;
    private String titulo;
    private String corpo;
    private Integer prazo;
    private SimNaoEnum prazoObrigatorio;

    @Column(name = "PEM_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = TipoEmailCorecEnum.CLASS_NAME)})
    public TipoEmailCorecEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEmailCorecEnum tipo) {
        this.tipo = tipo;
    }

    @Column(name = "PEM_IB_PRAZO_OBRIGATORIO", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPrazoObrigatorio() {
        return prazoObrigatorio;
    }

    public void setPrazoObrigatorio(SimNaoEnum prazoObrigatorio) {
        this.prazoObrigatorio = prazoObrigatorio;
    }

    @Column(name = "PEM_DS_REMETENTE")
    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    @Column(name = "PEM_DS_TITULO")
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "PEM_AN_CORPO", length = 100000)
    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    @Column(name = "PEM_QT_PRAZO")
    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

}
