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
package br.gov.bcb.sisaps.src.dominio;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "ATV_ATIVIDADE_MAT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Atividade.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ATV_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ATV_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "ATV_NU_VERSAO", nullable = false))})
public class Atividade extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "ATV_ID";

    private static final int TAMANHO_NOME = 100;

    private String nome;
    private BigDecimal percentualParticipacao;
    private Matriz matriz;
    private ParametroPeso parametroPeso;
    private ParametroTipoAtividadeNegocio parametroTipoAtividadeNegocio;
    private Unidade unidade;
    private TipoUnidadeAtividadeEnum tipoAtividade;
    private List<CelulaRiscoControle> celulasRiscoControle;

    @Column(name = "ATV_NM", length = TAMANHO_NOME)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "ATV_PC_PARTICIPACAO", columnDefinition = "decimal(5,2)")
    public BigDecimal getPercentualParticipacao() {
        return percentualParticipacao;
    }

    public void setPercentualParticipacao(BigDecimal percentualParticipacao) {
        this.percentualParticipacao = percentualParticipacao;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Matriz.CAMPO_ID, nullable = false)
    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = ParametroPeso.CAMPO_ID, nullable = false)
    public ParametroPeso getParametroPeso() {
        return parametroPeso;
    }

    public void setParametroPeso(ParametroPeso parametroPeso) {
        this.parametroPeso = parametroPeso;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroTipoAtividadeNegocio.CAMPO_ID)
    public ParametroTipoAtividadeNegocio getParametroTipoAtividadeNegocio() {
        return parametroTipoAtividadeNegocio;
    }

    public void setParametroTipoAtividadeNegocio(ParametroTipoAtividadeNegocio parametroTipoAtividadeNegocio) {
        this.parametroTipoAtividadeNegocio = parametroTipoAtividadeNegocio;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Unidade.CAMPO_ID)
    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    @Column(name = "ATV_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = TipoUnidadeAtividadeEnum.CLASS_NAME)})
    public TipoUnidadeAtividadeEnum getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(TipoUnidadeAtividadeEnum tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    @OneToMany(mappedBy = "atividade", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<CelulaRiscoControle> getCelulasRiscoControle() {
        return celulasRiscoControle;
    }

    public void setCelulasRiscoControle(List<CelulaRiscoControle> celulasRiscoControle) {
        this.celulasRiscoControle = celulasRiscoControle;
    }
}
