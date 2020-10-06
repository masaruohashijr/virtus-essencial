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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "DEL_DELEGACAO_ARC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Delegacao.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "DEL_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "DEL_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(30)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "DEL_NU_VERSAO", nullable = false))})
public class Delegacao extends ObjetoPersistenteAuditavelVersionado<Integer> {
    
    public static final String CAMPO_ID = "DEL_ID";
    
    private String matriculaServidor;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    
    @Column(name = "DEL_CD_MATRICULA", nullable = false, columnDefinition = "character(8)")
    public String getMatriculaServidor() {
        return matriculaServidor;
    }
    
    public void setMatriculaServidor(String matriculaServidor) {
        this.matriculaServidor = matriculaServidor;
    }
    
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = AvaliacaoRiscoControle.CAMPO_ID, nullable = false)
    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }
    
    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }
    
}
