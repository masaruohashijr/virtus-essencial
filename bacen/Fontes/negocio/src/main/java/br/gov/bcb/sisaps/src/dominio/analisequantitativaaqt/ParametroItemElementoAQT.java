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
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PIA_PAR_ITEM_ELEMENTO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroItemElementoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PIA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PIA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PIA_NU_VERSAO", nullable = false))})
public class ParametroItemElementoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PIA_ID";
    private static final int TAMANHO_NOME_END_MANUAL = 200;
    private String descricao;
    private ParametroElementoAQT parametroElemento;
    private Metodologia metodologia;
    private Short ordem;
    
    @Column(name = "PIA_DS", length = TAMANHO_NOME_END_MANUAL, nullable = false)
    public String getNome() {
        return descricao;
    }
    
    public void setNome(String nome) {
        this.descricao = nome;
    }
    
    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroElementoAQT.CAMPO_ID, nullable = false)
    public ParametroElementoAQT getParametroElemento() {
        return parametroElemento;
    }
    
    public void setParametroElemento(ParametroElementoAQT parametroElemento) {
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
    
    @Column(name = "PIA_NU_ORDEM", nullable = false)
    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }
    
}
