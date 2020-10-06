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
@Table(name = "ESS_ES_SITUACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = SituacaoES.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ESS_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ESS_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ESS_NU_VERSAO"))})
public class SituacaoES extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "ESS_ID";

    private Ciclo ciclo;
    private ParametroSituacao parametroSituacao;
    private SituacaoES situacaoESAnterior;
    private String descricao;
    private SimNaoEnum pendente;
    private DateTime dataEncaminhamento;
    private String operadorEncaminhamento;

    @Column(name = "ESS_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public void setDataEncaminhamento(DateTime dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    @Column(name = "ESS_CD_OPER_ENCAMINHAMENTO")
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    public void setOperadorEncaminhamento(String operadorEncaminhamento) {
        this.operadorEncaminhamento = operadorEncaminhamento;
    }

    @Column(name = "ESS_IB_PENDENTE", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPendente() {
        return pendente;
    }

    public void setPendente(SimNaoEnum pendente) {
        this.pendente = pendente;
    }

    @OneToOne
    @JoinColumn(name = "ESS_ID_ANTERIOR")
    public SituacaoES getSituacaoESAnterior() {
        return situacaoESAnterior;
    }

    public void setSituacaoESAnterior(SituacaoES situacaoESAnterior) {
        this.situacaoESAnterior = situacaoESAnterior;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroSituacao.CAMPO_ID)
    public ParametroSituacao getParametroSituacao() {
        return parametroSituacao;
    }

    public void setParametroSituacao(ParametroSituacao parametroSituacao) {
        this.parametroSituacao = parametroSituacao;
    }
    
    @Column(name = "ESS_DS_JUST_SITUACAO", length = 20000)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
