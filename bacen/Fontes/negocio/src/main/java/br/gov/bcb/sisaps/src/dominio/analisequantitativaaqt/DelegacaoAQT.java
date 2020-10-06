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
package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "DEA_DELEGACAO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = DelegacaoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "DEA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "DEA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(30)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "DEA_NU_VERSAO", nullable = false))})
public class DelegacaoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    
    public static final String CAMPO_ID = "DEA_ID";
    
    private String matriculaServidor;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    
    @Column(name = "DEA_CD_MATRICULA", nullable = false, columnDefinition = "character(8)")
    public String getMatriculaServidor() {
        return matriculaServidor;
    }
    
    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }
    
    @ManyToOne(optional = false)
    @JoinColumn(name = AnaliseQuantitativaAQT.CAMPO_ID, nullable = false)
    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    
    
}
