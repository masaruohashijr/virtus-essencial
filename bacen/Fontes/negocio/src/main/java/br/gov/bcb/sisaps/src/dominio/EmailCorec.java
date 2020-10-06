package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;

@Entity
@Table(name = "ECO_EMAIL_COREC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EmailCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ECO_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ECO_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)"))})
public class EmailCorec extends ObjetoPersistenteAuditavelSisAps<Integer> {

    public static final String CAMPO_ID = "ECO_ID";

    private AgendaCorec agenda;
    private String matricula;
    private TipoEmailCorecEnum tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AgendaCorec.CAMPO_ID, nullable = false)
    public AgendaCorec getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaCorec agenda) {
        this.agenda = agenda;
    }

    @Column(name = "ECO_CD_MATRICULA", nullable = false, columnDefinition = "character(8)")
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Column(name = "ECO_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = TipoEmailCorecEnum.CLASS_NAME)})
    public TipoEmailCorecEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoEmailCorecEnum tipo) {
        this.tipo = tipo;
    }

    @Transient
    public String getDataAtualizacaoFormatada() {
        return getUltimaAtualizacao() == null ? "" : getUltimaAtualizacao().toString(Constantes.FORMATO_DATA_HORA);
    }

}
