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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMetodologiaEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "MET_METODOLOGIA_SRC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Metodologia.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "MET_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "MET_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "MET_NU_VERSAO"))})
public class Metodologia extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "MET_ID";
    public static final int TAMANHO_DESCRICAO = 255;
    public static final int TAMANHO_TITULO = 40;
    private static final String PROP_METODOLOGIA = "metodologia";
    private static final String DEFINICAO_SMALLINT = "smallint";
    private static final long serialVersionUID = 1L;
    private String descricao;
    private String titulo;
    private EstadoMetodologiaEnum estado;
    private SimNaoEnum metodologiaDefault;
    private SimNaoEnum isCalculoMedia;
    private Date dataInclusao;

    private List<ParametroPeso> parametrosPeso;
    private List<ParametroAQT> parametrosAQT;
    private List<ParametroTipoAtividadeNegocio> parametrosTipoAtividadeNegocio;
    private List<ParametroGrupoRiscoControle> parametrosGrupoRiscoControle;
    private List<ParametroFatorRelevanciaRiscoControle> parametrosFatorRelevancia;
    private List<ParametroNota> parametrosNota;
    private List<ParametroNotaAQT> parametrosNotaAQT;
    private List<ParametroTendencia> parametrosTendencia;
    private List<ParametroPerspectiva> parametrosPerspectiva;
    private List<ParametroSituacao> parametrosSituacao;

    @Column(name = "MET_DS", length = TAMANHO_DESCRICAO)
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Column(name = "MET_DS_TITULO", length = TAMANHO_TITULO)
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Column(name = "MET_CD_ESTADO", nullable = false, columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = EstadoMetodologiaEnum.CLASS_NAME)})
    public EstadoMetodologiaEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoMetodologiaEnum estado) {
        this.estado = estado;
    }

    @Column(name = "MET_IB_DEFAULT", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getMetodologiaDefault() {
        return metodologiaDefault;
    }

    public void setMetodologiaDefault(SimNaoEnum metodologiaDefault) {
        this.metodologiaDefault = metodologiaDefault;
    }

    @Column(name = "MET_IB_CALC_MEDIA", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getIsCalculoMedia() {
        return isCalculoMedia;
    }

    public void setIsCalculoMedia(SimNaoEnum isCalculoMedia) {
        this.isCalculoMedia = isCalculoMedia;
    }

    @Column(name = "MET_DT_INCLUSAO", nullable = false, columnDefinition = "date")
    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroPeso> getParametrosPeso() {
        return parametrosPeso;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroAQT> getParametrosAQT() {
        return parametrosAQT;
    }

    public void setParametrosAQT(List<ParametroAQT> parametrosAQT) {
        this.parametrosAQT = parametrosAQT;
    }

    public void setParametrosPeso(List<ParametroPeso> parametrosPeso) {
        this.parametrosPeso = parametrosPeso;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroTipoAtividadeNegocio> getParametrosTipoAtividadeNegocio() {
        return parametrosTipoAtividadeNegocio;
    }

    public void setParametrosTipoAtividadeNegocio(List<ParametroTipoAtividadeNegocio> parametrosTipoAtividadeNegocio) {
        this.parametrosTipoAtividadeNegocio = parametrosTipoAtividadeNegocio;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroGrupoRiscoControle> getParametrosGrupoRiscoControle() {
        return parametrosGrupoRiscoControle;
    }

    public void setParametrosGrupoRiscoControle(List<ParametroGrupoRiscoControle> parametrosGrupoRiscoControle) {
        this.parametrosGrupoRiscoControle = parametrosGrupoRiscoControle;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroFatorRelevanciaRiscoControle> getParametrosFatorRelevancia() {
        return parametrosFatorRelevancia;
    }

    public void setParametrosFatorRelevancia(List<ParametroFatorRelevanciaRiscoControle> parametrosFatorRelevancia) {
        this.parametrosFatorRelevancia = parametrosFatorRelevancia;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroNota> getParametrosNota() {
        return parametrosNota;
    }

    public void setParametrosNota(List<ParametroNota> parametrosNota) {
        this.parametrosNota = parametrosNota;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroNotaAQT> getParametrosNotaAQT() {
        return parametrosNotaAQT;
    }

    public void setParametrosNotaAQT(List<ParametroNotaAQT> parametrosNotaAQT) {
        this.parametrosNotaAQT = parametrosNotaAQT;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroTendencia> getParametrosTendencia() {
        return parametrosTendencia;
    }

    public void setParametrosTendencia(List<ParametroTendencia> parametrosTendencia) {
        this.parametrosTendencia = parametrosTendencia;
    }
    
    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroPerspectiva> getParametrosPerspectiva() {
        return parametrosPerspectiva;
    }

    public void setParametrosPerspectiva(List<ParametroPerspectiva> parametrosPerspectiva) {
        this.parametrosPerspectiva = parametrosPerspectiva;
    }

    @OneToMany(mappedBy = PROP_METODOLOGIA, fetch = FetchType.LAZY)
    public List<ParametroSituacao> getParametrosSituacao() {
        return parametrosSituacao;
    }

    public void setParametrosSituacao(List<ParametroSituacao> parametrosSituacao) {
        this.parametrosSituacao = parametrosSituacao;
    }
    
    @Transient
    public List<ParametroGrupoRiscoControle> getParametrosGrupoRiscoControleMatriz() {
        List<ParametroGrupoRiscoControle> retorno = new ArrayList<ParametroGrupoRiscoControle>();
        for (ParametroGrupoRiscoControle parametroRC : getParametrosGrupoRiscoControle()) {
            if (parametroRC.getTipoGrupo().equals(TipoParametroGrupoRiscoControleEnum.MATRIZ)) {
                retorno.add(parametroRC);
            }
        }
        return retorno;
    }

    @Transient
    public List<ParametroNota> getNotasArc() {
        ordenarNotas();
        if (getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            List<ParametroNota> notasElementos = new ArrayList<ParametroNota>();
            for (ParametroNota nota : getParametrosNota()) {
                if (nota.getIsNotaElemento() != null && nota.getIsNotaElemento().equals(SimNaoEnum.SIM)) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        } else {
            return getParametrosNota();
        }
    }

    @Transient
    public List<ParametroNotaAQT> getNotasAnef() {
        ordenarNotasAQT();
        if (getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            List<ParametroNotaAQT> notasElementos = new ArrayList<ParametroNotaAQT>();
            for (ParametroNotaAQT nota : getParametrosNotaAQT()) {
                if (nota.getIsNotaElemento() != null && nota.getIsNotaElemento().equals(SimNaoEnum.SIM)) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        } else {
            return getParametrosNotaAQT();
        }
    }

    @Transient
    public List<ParametroNotaAQT> getNotasAnefSemNA() {
        ordenarNotasAQT();
        if (getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            List<ParametroNotaAQT> notasElementos = new ArrayList<ParametroNotaAQT>();
            for (ParametroNotaAQT nota : getParametrosNotaAQT()) {
                if (nota.getIsNotaElemento() != null && nota.getIsNotaElemento().equals(SimNaoEnum.SIM)
                        && nota.getValor().compareTo(BigDecimal.ZERO) == 1) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        } else {
            List<ParametroNotaAQT> notasElementos = new ArrayList<ParametroNotaAQT>();
            for (ParametroNotaAQT nota : getParametrosNotaAQT()) {
                if (nota.getValor().compareTo(BigDecimal.ZERO) == 1) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        }
    }

    @Transient
    public List<ParametroNota> getNotasArcAjuste() {
        if (getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            List<ParametroNota> notasElementos = new ArrayList<ParametroNota>();
            for (ParametroNota nota : getParametrosNota()) {
                if (nota.getIsNotaElemento() != null && nota.getIsNotaElemento().equals(SimNaoEnum.NAO)) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        } else {
            return getParametrosNota();
        }
    }

    @Transient
    public List<ParametroNotaAQT> getNotasAnefAjuste() {
        if (getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            List<ParametroNotaAQT> notasElementos = new ArrayList<ParametroNotaAQT>();
            for (ParametroNotaAQT nota : getParametrosNotaAQT()) {
                if (nota.getIsNotaElemento() != null && nota.getIsNotaElemento().equals(SimNaoEnum.NAO)) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        } else {
            List<ParametroNotaAQT> notasElementos = new ArrayList<ParametroNotaAQT>();
            for (ParametroNotaAQT nota : getParametrosNotaAQT()) {
                if (nota.getValor().compareTo(BigDecimal.ZERO) == 1) {
                    notasElementos.add(nota);
                }
            }
            return notasElementos;
        }
    }

    @SuppressWarnings("unchecked")
    private void ordenarNotas() {
        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("pk"));
        Collections.sort(getParametrosNota(), cc);
    }

    @SuppressWarnings("unchecked")
    private void ordenarNotasAQT() {
        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("pk"));
        Collections.sort(getParametrosNotaAQT(), cc);
    }
    
    @Transient
    public boolean getIsMetodologiaNova() {
        return getIsCalculoMedia() != null && getIsCalculoMedia().equals(SimNaoEnum.SIM);
    }

}
