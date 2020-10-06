package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "OBA_OBSERVACAO_AGENDA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ObservacaoAgendaCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "OBA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "OBA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "OBA_NU_VERSAO", nullable = false))})
public class ObservacaoAgendaCorec extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "OBA_ID";

    private AgendaCorec agenda;
    private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = AgendaCorec.CAMPO_ID, nullable = false)
    public AgendaCorec getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaCorec agenda) {
        this.agenda = agenda;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "OBA_AN")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Transient
    public String getNomeOperador() {
        return super.getNomeOperador();
    }

    @Transient
    public String getDataHoraFormatada() {
        return super.getDataHoraFormatada();
    }

}
