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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PIE_PAR_ITEM_ELEMENTO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroItemElemento.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PIE_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PIE_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PIE_NU_VERSAO", nullable = false))})
public class ParametroItemElemento extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PIE_ID";
    private static final int TAMANHO_NOME_END_MANUAL = 200;
    private String nome;
    private String enderecoManual;
    private ParametroElemento parametroElemento;
    private Metodologia metodologia;
    private Short ordem;
    
    @Column(name = "PIE_NM", length = TAMANHO_NOME_END_MANUAL, nullable = false)
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    @Column(name = "PIE_DS_END_MANUAL", length = 100, nullable = false)
    public String getEnderecoManual() {
        return enderecoManual;
    }
    
    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }
    
    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroElemento.CAMPO_ID, nullable = false)
    public ParametroElemento getParametroElemento() {
        return parametroElemento;
    }
    
    public void setParametroElemento(ParametroElemento parametroElemento) {
        this.parametroElemento = parametroElemento;
    }
    
    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }
    
    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }
    
    @Column(name = "PIE_NU_ORDEM", nullable = false)
    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }
    
}
