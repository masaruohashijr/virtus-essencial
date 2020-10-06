/*
 * Sistema APS
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelConcorrente;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PFR_PAR_FATOR_RELEV_RIS_CON", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroFatorRelevanciaRiscoControle.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PFR_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PFR_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelConcorrente.PROP_VERSAO, column = @Column(name = "PFR_NU_VERSAO", nullable = false))})
public class ParametroFatorRelevanciaRiscoControle extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "PFR_ID";
    private static final String TAMANHO_ALFA_BETA = "decimal(5,2)";
    private BigDecimal valorAlfa;
    private BigDecimal valorBeta;
    private Metodologia metodologia;

    @Column(name = "PFR_VL_ALFA", nullable = false, columnDefinition = TAMANHO_ALFA_BETA)
    public BigDecimal getValorAlfa() {
        return valorAlfa;
    }

    public void setValorAlfa(BigDecimal valorAlfa) {
        this.valorAlfa = valorAlfa;
    }

    @Column(name = "PFR_VL_BETA", nullable = false, columnDefinition = TAMANHO_ALFA_BETA)
    public BigDecimal getValorBeta() {
        return valorBeta;
    }

    public void setValorBeta(BigDecimal valorBeta) {
        this.valorBeta = valorBeta;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }
}
