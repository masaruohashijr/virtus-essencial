/*
 * Sistema APS.
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
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

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "SIR_SINTESE_RISCO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = SinteseDeRisco.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "SIR_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "SIR_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "SIR_NU_VERSAO"))})
public class SinteseDeRisco extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "SIR_ID";

    private SinteseDeRisco sinteseAnterior;
    private ParametroNota risco;
    private String descricaoSintese;
    private ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private Ciclo ciclo;
    private VersaoPerfilRisco versaoPerfilRisco;


    /**
     * Construtor padr�o
     */
    public SinteseDeRisco() {
        //TODO n�o precisa ser implementado
    }

    public SinteseDeRisco(Integer pk) {
        setPk(pk);
    }

    @OneToOne
    @JoinColumn(name = "SIR_ID_ANTERIOR")
    public SinteseDeRisco getSinteseAnterior() {
        return sinteseAnterior;
    }

    public void setSinteseAnterior(SinteseDeRisco sinteseAnterior) {
        this.sinteseAnterior = sinteseAnterior;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroNota.CAMPO_ID)
    public ParametroNota getRisco() {
        return risco;
    }

    public void setRisco(ParametroNota risco) {
        this.risco = risco;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SIR_AN_SINTESE")
    public String getDescricaoSintese() {
        return descricaoSintese;
    }

    public void setDescricaoSintese(String descricaoSintese) {
        this.descricaoSintese = descricaoSintese;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroGrupoRiscoControle.CAMPO_ID, nullable = false)
    public ParametroGrupoRiscoControle getParametroGrupoRiscoControle() {
        return parametroGrupoRiscoControle;
    }

    public void setParametroGrupoRiscoControle(ParametroGrupoRiscoControle parametroGrupoRiscoControle) {
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = true)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @Override
    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }
    
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

}
