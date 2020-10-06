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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "PAQ_PESO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = PesoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PAQ_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PAQ_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "PAQ_NU_VERSAO"))})
public class PesoAQT extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "PAQ_ID";
    private ParametroAQT parametroAQT;
    private Short valor;
    private Ciclo ciclo;
    private VersaoPerfilRisco versaoPerfilRisco;
    private AnaliseQuantitativaAQT anefRascunho;

    /**
     * Construtor padrão
     */
    public PesoAQT() {
        // não precisa ser implementado
    }

    public PesoAQT(Integer pk) {
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


    @Column(name = "PAQ_QT_PESO", nullable = false)
    public Short getValor() {
        return valor;
    }

    public void setValor(Short valor) {
        this.valor = valor;
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

    @Override
    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }

    @Transient
    public AnaliseQuantitativaAQT getAnefRascunho() {
        return anefRascunho;
    }

    public void setAnefRascunho(AnaliseQuantitativaAQT anefRascunho) {
        this.anefRascunho = anefRascunho;
    }

}
