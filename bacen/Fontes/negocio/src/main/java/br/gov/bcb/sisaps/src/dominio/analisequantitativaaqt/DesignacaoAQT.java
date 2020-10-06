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
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "DAQ_DESIGNACAO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = DesignacaoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "DAQ_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "DAQ_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(30)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "DAQ_NU_VERSAO", nullable = false))})
public class DesignacaoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    
    public static final String CAMPO_ID = "DAQ_ID";
    
    private String matriculaServidor;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    
    @Column(name = "DAQ_CD_MATRICULA", nullable = false, columnDefinition = "character(8)")
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

    
    @Transient
    public String getNomeResponsavel() {
        ServidorVO servidorvo = BcPessoaAdapter.get().buscarServidor(getMatriculaServidor());
        return servidorvo.getNome();
    }
    
}
