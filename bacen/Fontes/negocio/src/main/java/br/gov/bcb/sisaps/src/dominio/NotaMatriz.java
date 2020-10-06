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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "NMA_NOTA_MATRIZ", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = NotaMatriz.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "NMA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "NMA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "NMA_NU_VERSAO"))})
public class NotaMatriz extends ObjetoVersionadorAuditavelVersionado {
    
    public static final String CAMPO_ID = "NMA_ID";
    
    private ParametroNota notaFinalMatriz;
    private String justificativaNota;
    private Matriz matriz;
    private NotaMatriz notaMatrizAnterior;
    
    @ManyToOne
    @JoinColumn(name = ParametroNota.CAMPO_ID)
    public ParametroNota getNotaFinalMatriz() {
        return notaFinalMatriz;
    }

    public void setNotaFinalMatriz(ParametroNota notaFinalMatriz) {
        this.notaFinalMatriz = notaFinalMatriz;
    }

    @Column(name = "NMA_DS_NOTA_MAT", length = 20000)
    public String getJustificativaNota() {
        return justificativaNota;
    }

    public void setJustificativaNota(String justificativaNota) {
        this.justificativaNota = justificativaNota;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Matriz.CAMPO_ID, nullable = false)
    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    @OneToOne
    @JoinColumn(name = "NMA_ID_ANTERIOR")
    public NotaMatriz getNotaMatrizAnterior() {
        return notaMatrizAnterior;
    }

    public void setNotaMatrizAnterior(NotaMatriz notaMatrizAnterior) {
        this.notaMatrizAnterior = notaMatrizAnterior;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

}
