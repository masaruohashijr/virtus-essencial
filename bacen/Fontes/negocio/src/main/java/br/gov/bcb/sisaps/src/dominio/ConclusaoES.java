/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
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
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "ESC_ES_CONCLUSAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ConclusaoES.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ESC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ESC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ESC_NU_VERSAO"))})
public class ConclusaoES extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "ESC_ID";

    private Ciclo ciclo;
    private Documento documento;
    private ConclusaoES conclusaoESAnterior;
    private SimNaoEnum pendente;
    private DateTime dataEncaminhamento;
    private String operadorEncaminhamento;

    @Column(name = "ESC_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public void setDataEncaminhamento(DateTime dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    @Column(name = "ESC_CD_OPER_ENCAMINHAMENTO")
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    public void setOperadorEncaminhamento(String operadorEncaminhamento) {
        this.operadorEncaminhamento = operadorEncaminhamento;
    }

    @Column(name = "ESC_IB_PENDENTE", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPendente() {
        return pendente;
    }

    public void setPendente(SimNaoEnum pendente) {
        this.pendente = pendente;
    }

    @OneToOne
    @JoinColumn(name = "ESC_ID_ANTERIOR")
    public ConclusaoES getConclusaoESAnterior() {
        return conclusaoESAnterior;
    }

    public void setConclusaoESAnterior(ConclusaoES conclusaoESAnterior) {
        this.conclusaoESAnterior = conclusaoESAnterior;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }
    
    @ManyToOne
    @JoinColumn(name = "DOC_ID_CONCLUSAO")
    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }
    
    
    @Transient
    public String getNomeOperadorEncaminhamentoDataHora() {
        return SisapsUtil.getNomeOperadorDataHora(getOperadorEncaminhamento(), getDataEncaminhamento());
    }
    
    @Transient
    public String getNomeOperadorEncaminhamentoDataHoraPublicacao() {
        return SisapsUtil.getNomeOperadorDataHora(getOperadorEncaminhamento(), getUltimaAtualizacao());
    }

}
