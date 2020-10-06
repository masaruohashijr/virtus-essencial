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

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PEA_PAR_ELEMENTO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroElementoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PEA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PEA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PEA_NU_VERSAO", nullable = false))})
public class ParametroElementoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PEA_ID";
    private String descricao;
    private Short ordem;
    private ParametroAQT paramentoAqt;
    private Metodologia metodologia;
    private List<ParametroItemElementoAQT> itensElemento;

    @Column(name = "PEA_DS", nullable = false)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    @Column(name = "PEA_NU_ORDEM", nullable = false)
    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroAQT.CAMPO_ID, nullable = false)
    public ParametroAQT getParamentoAqt() {
        return paramentoAqt;
    }

    public void setParamentoAqt(ParametroAQT paramentoAqt) {
        this.paramentoAqt = paramentoAqt;
    }


    @OneToMany(mappedBy = "parametroElemento", fetch = FetchType.LAZY)
    public List<ParametroItemElementoAQT> getItensElemento() {
        return itensElemento;
    }

    public void setItensElemento(List<ParametroItemElementoAQT> itensElemento) {
        this.itensElemento = itensElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

}
