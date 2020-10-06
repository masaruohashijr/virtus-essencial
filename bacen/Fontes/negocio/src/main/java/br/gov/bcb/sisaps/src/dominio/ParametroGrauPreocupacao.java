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
import javax.persistence.Transient;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelConcorrente;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@SuppressWarnings("serial")
@Entity
@Table(name = "PGP_PAR_GRAU_PREOCUPACAO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParametroGrauPreocupacao.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PGP_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PGP_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelConcorrente.PROP_VERSAO, column = @Column(name = "PGP_NU_VERSAO", nullable = false))})
public class ParametroGrauPreocupacao extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "PGP_ID";
    public static final String NAO_APLICAVEL = "N/A";

    private Short valor;
    private String descricao;
    private Metodologia metodologia;

    @Column(name = "PGP_CD_VALOR", nullable = false)
    public Short getValor() {
        return valor;
    }

    public void setValor(Short valor) {
        this.valor = valor;
    }

    @Column(name = "PGP_DS", nullable = false, length = 20)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    @Transient
    public String getDescricaoValor() {
        String descricao = null;

        if (valor != null) {
            if (valor <= 0) {
                descricao = NAO_APLICAVEL;
            } else {
                descricao = valor.toString();
            }
        }

        return descricao;
    }
}
