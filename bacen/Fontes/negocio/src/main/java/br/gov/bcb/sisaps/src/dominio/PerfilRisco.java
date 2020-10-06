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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.Constantes;

@Entity
@Table(name = "PER_PERFIL_RISCO", schema = "SUP")
@AttributeOverrides(value = {@AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = PerfilRisco.CAMPO_ID))})
public class PerfilRisco extends ObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "PER_ID";

    private Ciclo ciclo;
    private List<VersaoPerfilRisco> versoesPerfilRisco;
    private DateTime dataCriacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "PVE_PER_VER", uniqueConstraints = {@UniqueConstraint(columnNames = {CAMPO_ID,
            VersaoPerfilRisco.CAMPO_ID})}, schema = "SUP", joinColumns = {@JoinColumn(name = CAMPO_ID)}, inverseJoinColumns = {@JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = false)})
    public List<VersaoPerfilRisco> getVersoesPerfilRisco() {
        return versoesPerfilRisco;
    }

    public void setVersoesPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRisco) {
        this.versoesPerfilRisco = versoesPerfilRisco;
    }

    public void addVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        if (versoesPerfilRisco == null) {
            List<VersaoPerfilRisco> versoes = new ArrayList<VersaoPerfilRisco>();
            versoes.add(versaoPerfilRisco);
            this.setVersoesPerfilRisco(versoes);
        } else {
            versoesPerfilRisco.add(versaoPerfilRisco);
        }
    }

    public void removeVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        if (versoesPerfilRisco != null) {
            versoesPerfilRisco.remove(versaoPerfilRisco);
        }
    }

    @Column(nullable = false, name = "PER_DH_CRIACAO")
    public DateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(DateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Transient
    public String getDataCriacaoFormatada() {
        return this.getDataCriacao() == null ? "" : this.getDataCriacao().toString(Constantes.FORMATO_DATA_HORA);
    }

    @Transient
    public String getDataCriacaoFormatadaSemHora() {
        return this.getDataCriacao() == null ? "" : this.getDataCriacao().toString(Constantes.FORMATO_DATA_COM_BARRAS);
    }

}
