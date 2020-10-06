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
package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

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

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "SRA_SINTESE_RISCO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = SinteseDeRiscoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "SRA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "SRA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "SRA_NU_VERSAO"))})
public class SinteseDeRiscoAQT extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "SRA_ID";
    private ParametroAQT parametroAQT;
    private String justificativa;
    private Ciclo ciclo;

    /**
     * Construtor padrão
     */
    public SinteseDeRiscoAQT() {
        // não precisa ser implementado
    }

    public SinteseDeRiscoAQT(Integer pk) {
        setPk(pk);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroAQT.CAMPO_ID)
    public ParametroAQT getParametroAQT() {
        return parametroAQT;
    }

    public void setParametroAQT(ParametroAQT parametroAQT) {
        this.parametroAQT = parametroAQT;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SRA_AN_JUSTIFICATIVA", nullable = false)
    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @Transient
    public String getDescricaoSintese() {
        return getParametroAQT().getDescricao();
    }

    @Transient
    public DateTime getDataSemSegungos() {

        return getUltimaAtualizacao().toDateTimeISO();

    }

}
