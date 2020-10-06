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

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "ESG_ES_GRAU_PREOCUPACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = GrauPreocupacaoES.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ESG_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ESG_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ESG_NU_VERSAO"))})
public class GrauPreocupacaoES extends ObjetoVersionadorAuditavelVersionado {
    public static final String CAMPO_ID = "ESG_ID";
    private static final String SMALLINT = "smallint";
    private static final String ENUM_CLASS = "enumClass";
    private static final String PERCENTUAL = "%";

    private GrauPreocupacaoES grauPreocupacaoESAnterior;
    private ParametroGrauPreocupacao parametroGrauPreocupacao;
    private Ciclo ciclo;
    private SimNaoEnum pendente;
    private DateTime dataEncaminhamento;
    private String operadorEncaminhamento;
    private ParametroNota parametroNota;
    private String justificativa;
    private BigDecimal numeroFatorRelevanciaAnef;

    @Column(name = "ESG_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public void setDataEncaminhamento(DateTime dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    @Column(name = "ESG_CD_OPER_ENCAMINHAMENTO")
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    public void setOperadorEncaminhamento(String operadorEncaminhamento) {
        this.operadorEncaminhamento = operadorEncaminhamento;
    }

    @Column(name = "ESG_IB_PENDENTE", columnDefinition = SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPendente() {
        return pendente;
    }

    public void setPendente(SimNaoEnum pendente) {
        this.pendente = pendente;
    }

    @OneToOne
    @JoinColumn(name = "ESG_ID_ANTERIOR")
    public GrauPreocupacaoES getGrauPreocupacaoESAnterior() {
        return grauPreocupacaoESAnterior;
    }

    public void setGrauPreocupacaoESAnterior(GrauPreocupacaoES grauPreocupacaoESAnterior) {
        this.grauPreocupacaoESAnterior = grauPreocupacaoESAnterior;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroGrauPreocupacao.CAMPO_ID)
    public ParametroGrauPreocupacao getParametroGrauPreocupacao() {
        return parametroGrauPreocupacao;
    }

    public void setParametroGrauPreocupacao(ParametroGrauPreocupacao parametroGrauPreocupacao) {
        this.parametroGrauPreocupacao = parametroGrauPreocupacao;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroNota.CAMPO_ID)
    public ParametroNota getParametroNota() {
        return parametroNota;
    }

    public void setParametroNota(ParametroNota parametroNota) {
        this.parametroNota = parametroNota;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "ESG_AN_JUSTIFICATIVA")
    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @Column(name = "ESG_NU_FT_RELEV_ANEF", nullable = true, columnDefinition = "decimal(5,2)")
    public BigDecimal getNumeroFatorRelevanciaAnef() {
        return numeroFatorRelevanciaAnef;
    }

    public void setNumeroFatorRelevanciaAnef(BigDecimal numeroFatorRelevanciaAnef) {
        this.numeroFatorRelevanciaAnef = numeroFatorRelevanciaAnef;
    }

    @Transient
    public String getNomeOperadorEncaminhamentoDataHora() {
        return SisapsUtil.getNomeOperadorDataHora(getOperadorEncaminhamento(), getDataEncaminhamento());
    }

    @Transient
    public String getNomeOperadorEncaminhamentoDataHoraPublicacao() {
        return SisapsUtil.getNomeOperadorDataHora(getOperadorEncaminhamento(), getUltimaAtualizacao());
    }
    
    @Transient
    public String getNomeOperadorAtualizacaoDataHora() {
        return SisapsUtil.getNomeOperadorDataHora(getOperadorAtualizacao(), getUltimaAtualizacao());
    }

    @Transient
    public String getDescricaoNotaFinal() {
        return getParametroGrauPreocupacao() == null && getParametroNota() == null ? ""
                : getParametroGrauPreocupacao() != null ? getParametroGrauPreocupacao().getDescricao()
                : getParametroNota().getDescricao();
    }
    
    @Transient
    public Integer getPercentualAnefInt() {
        return numeroFatorRelevanciaAnef != null ? Integer.valueOf(numeroFatorRelevanciaAnef
                .multiply(new BigDecimal(Constantes.CEM)).toBigInteger().toString()) : null;
    }

    @Transient
    public String getPercentualAnef() {
        return getPercentualAnefInt() != null ? getPercentualAnefInt().toString() + PERCENTUAL : "";
    }
    
    @Transient
    public Integer getPercentualMatrizInt() {
        return numeroFatorRelevanciaAnef != null ? Integer
                .valueOf(new BigDecimal(Constantes.CEM)
                        .subtract(numeroFatorRelevanciaAnef.multiply(new BigDecimal(Constantes.CEM))).toBigInteger()
                        .toString()) : null;
    }

    @Transient
    public String getPercentualMatriz() {
        return getPercentualMatrizInt() != null ? getPercentualMatrizInt().toString() + PERCENTUAL : "";
    }
    
}
