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
@Table(name = "CRC_CELULA_RISCO_CONTROLE", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = CelulaRiscoControle.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "CRC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "CRC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "CRC_NU_VERSAO", nullable = false))})
public class CelulaRiscoControle extends ObjetoVersionadorAuditavelVersionado {
    public static final String CAMPO_ID = "CRC_ID";
    public static final String DESCRICAO_GRUPO_RISCO_CONTROLE = "Grupo de Risco e Controle";
    public static final String DESCRICAO_PESO = "Peso";

    private static final long serialVersionUID = 1L;

    private Atividade atividade;
    private ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private ParametroPeso parametroPeso;
    private AvaliacaoRiscoControle arcRisco;
    private AvaliacaoRiscoControle arcControle;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = Atividade.CAMPO_ID, nullable = false)
    public Atividade getAtividade() {
        return atividade;
    }

    public void setAtividade(Atividade atividade) {
        this.atividade = atividade;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = ParametroGrupoRiscoControle.CAMPO_ID, nullable = false)
    public ParametroGrupoRiscoControle getParametroGrupoRiscoControle() {
        return parametroGrupoRiscoControle;
    }

    public void setParametroGrupoRiscoControle(ParametroGrupoRiscoControle parametroGrupoRiscoControle) {
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = ParametroPeso.CAMPO_ID, nullable = false)
    public ParametroPeso getParametroPeso() {
        return parametroPeso;
    }

    public void setParametroPeso(ParametroPeso parametroPeso) {
        this.parametroPeso = parametroPeso;
    }

    @OneToOne
    @JoinColumn(name = "ARC_ID_RISCO", nullable = false)
    public AvaliacaoRiscoControle getArcRisco() {
        return arcRisco;
    }

    public void setArcRisco(AvaliacaoRiscoControle arcRisco) {
        this.arcRisco = arcRisco;
    }

    @OneToOne
    @JoinColumn(name = "ARC_ID_CONTROLE", nullable = false)
    public AvaliacaoRiscoControle getArcControle() {
        return arcControle;
    }

    public void setArcControle(AvaliacaoRiscoControle arcControle) {
        this.arcControle = arcControle;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

}
