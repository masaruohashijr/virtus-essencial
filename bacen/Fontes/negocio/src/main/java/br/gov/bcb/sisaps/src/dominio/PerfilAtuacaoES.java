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
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "ESP_ES_PERFIL_ATUACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = PerfilAtuacaoES.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ESP_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ESP_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoVersionadorAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ESP_NU_VERSAO"))})
public class PerfilAtuacaoES extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "ESP_ID";

    private Ciclo ciclo;
    private Documento documento;
    private PerfilAtuacaoES perfilAtuacaoESAnterior;
    private SimNaoEnum pendente;
    private DateTime dataEncaminhamento;
    private String operadorEncaminhamento;

    @Column(name = "ESP_DH_ENCAMINHAMENTO")
    public DateTime getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public void setDataEncaminhamento(DateTime dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
    }

    @Column(name = "ESP_CD_OPER_ENCAMINHAMENTO")
    public String getOperadorEncaminhamento() {
        return operadorEncaminhamento;
    }

    public void setOperadorEncaminhamento(String operadorEncaminhamento) {
        this.operadorEncaminhamento = operadorEncaminhamento;
    }

    @Column(name = "ESP_IB_PENDENTE",  columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPendente() {
        return pendente;
    }

    public void setPendente(SimNaoEnum pendente) {
        this.pendente = pendente;
    }
    
    

    @OneToOne
    @JoinColumn(name = "ESP_ID_ANTERIOR")
    public PerfilAtuacaoES getPerfilAtuacaoESAnterior() {
        return perfilAtuacaoESAnterior;
    }

    public void setPerfilAtuacaoESAnterior(PerfilAtuacaoES perfilAtuacaoESAnterior) {
        this.perfilAtuacaoESAnterior = perfilAtuacaoESAnterior;
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
    @JoinColumn(name = "DOC_ID_PERFIL_ATUACAO")
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
