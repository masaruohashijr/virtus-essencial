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

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "AJC_AJUSTE_COREC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AjusteCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AJC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AJC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "AJC_NU_VERSAO"))})
public class AjusteCorec extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "AJC_ID";

    private Ciclo ciclo;
    private ParametroNota notaQualitativa;
    private ParametroNotaAQT notaQuantitativa;
    private ParametroGrauPreocupacao grauPreocupacao;
    private ParametroNota notaFinal;
    private ParametroPerspectiva perspectiva;
    private boolean alterouGrau;
    private boolean alterouQualitativa;
    private boolean alterouQuantitativa;
    private boolean alterouPerspectiva;
    private String outrasDeliberacoes;
    
    
    
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "AJC_AN_DELIBERACOES", length = 100000)
    public String getOutrasDeliberacoes() {
        return outrasDeliberacoes;
    }

    public void setOutrasDeliberacoes(String outrasDeliberacoes) {
        this.outrasDeliberacoes = outrasDeliberacoes;
    }

    @ManyToOne
    @JoinColumn(name = "PNO_ID_QUALITATIVA")
    public ParametroNota getNotaQualitativa() {
        return notaQualitativa;
    }

    public void setNotaQualitativa(ParametroNota notaQualitativa) {
        this.notaQualitativa = notaQualitativa;
    }

    @ManyToOne
    @JoinColumn(name = "PNA_ID_QUANTITATIVA")
    public ParametroNotaAQT getNotaQuantitativa() {
        return notaQuantitativa;
    }

    public void setNotaQuantitativa(ParametroNotaAQT notaQuatitativa) {
        this.notaQuantitativa = notaQuatitativa;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @OneToOne
    @JoinColumn(name = "PGP_ID")
    public ParametroGrauPreocupacao getGrauPreocupacao() {
        return grauPreocupacao;
    }

    public void setGrauPreocupacao(ParametroGrauPreocupacao grauPreocupacao) {
        this.grauPreocupacao = grauPreocupacao;
    }

    @ManyToOne
    @JoinColumn(name = "PNO_ID_FINAL")
    public ParametroNota getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(ParametroNota notaFinal) {
        this.notaFinal = notaFinal;
    }

    @OneToOne
    @JoinColumn(name = ParametroPerspectiva.CAMPO_ID)
    public ParametroPerspectiva getPerspectiva() {
        return perspectiva;
    }

    public void setPerspectiva(ParametroPerspectiva perspectiva) {
        this.perspectiva = perspectiva;
    }

    @Transient
    public boolean isAlterouGrau() {
        return alterouGrau;
    }

    public void setAlterouGrau(boolean alterouGrau) {
        this.alterouGrau = alterouGrau;
    }

    @Transient
    public boolean isAlterouQualitativa() {
        return alterouQualitativa;
    }

    public void setAlterouQualitativa(boolean alterouQualitativa) {
        this.alterouQualitativa = alterouQualitativa;
    }

    @Transient
    public boolean isAlterouQuantitativa() {
        return alterouQuantitativa;
    }

    public void setAlterouQuantitativa(boolean alterouQuantitativa) {
        this.alterouQuantitativa = alterouQuantitativa;
    }

    @Transient
    public boolean isAlterouPerspectiva() {
        return alterouPerspectiva;
    }

    public void setAlterouPerspectiva(boolean alterouPerspectiva) {
        this.alterouPerspectiva = alterouPerspectiva;
    }
    
    @Transient
    public String getDescricaoNotaFinal() {
        return getGrauPreocupacao() == null && getNotaFinal() == null ? ""
                : getGrauPreocupacao() != null ? getGrauPreocupacao().getDescricao()
                : getNotaFinal().getDescricao();
    }
}
