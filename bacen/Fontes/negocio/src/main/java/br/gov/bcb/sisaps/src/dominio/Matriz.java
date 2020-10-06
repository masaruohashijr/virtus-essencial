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
import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "MAT_MATRIZ_SRC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Matriz.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "MAT_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "MAT_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "MAT_NU_VERSAO", nullable = false)),
        @AttributeOverride(name = VersaoPerfilRisco.CAMPO_ID, column = @Column(nullable = true, name = VersaoPerfilRisco.CAMPO_ID))})
public class Matriz extends ObjetoVersionadorAuditavelVersionado {
    public static final String CAMPO_ID = "MAT_ID";
    private static final String PERCENTUAL = "%";
    private static final String PROP_MATRIZ = "matriz";
    private static final long serialVersionUID = 1L;
    private EstadoMatrizEnum estadoMatriz;
    private Ciclo ciclo;

    private List<Unidade> unidades;
    private List<Atividade> atividades;
    private BigDecimal numeroFatorRelevanciaUC;
    private BigDecimal numeroFatorRelevanciaAE;

    /**
     * Construtor padrão
     */
    public Matriz() {
        //TODO não precisa ser implementado
    }

    public Matriz(Integer pk) {
        setPk(pk);
    }

    @Column(name = "MAT_NU_FT_RELEV_UC", nullable = false, columnDefinition = "decimal(5,2)")
    public BigDecimal getNumeroFatorRelevanciaUC() {
        return numeroFatorRelevanciaUC;
    }

    public void setNumeroFatorRelevanciaUC(BigDecimal numeroFatorRelevanciaUC) {
        this.numeroFatorRelevanciaUC = numeroFatorRelevanciaUC;
    }
    
    @Column(name = "MAT_NU_FT_RELEV_AE", nullable = true, columnDefinition = "decimal(5,2)")
    public BigDecimal getNumeroFatorRelevanciaAE() {
        return numeroFatorRelevanciaAE;
    }

    public void setNumeroFatorRelevanciaAE(BigDecimal numeroFatorRelevanciaAE) {
        this.numeroFatorRelevanciaAE = numeroFatorRelevanciaAE;
    }

    @Column(name = "MAT_CD_ESTADO", columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = EstadoMatrizEnum.CLASS_NAME)})
    public EstadoMatrizEnum getEstadoMatriz() {
        return estadoMatriz;
    }

    public void setEstadoMatriz(EstadoMatrizEnum estadoMatriz) {
        this.estadoMatriz = estadoMatriz;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = true)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = true)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @OneToMany(mappedBy = PROP_MATRIZ, fetch = FetchType.LAZY)
    public List<Unidade> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<Unidade> unidades) {
        this.unidades = unidades;
    }

    @OneToMany(mappedBy = PROP_MATRIZ, fetch = FetchType.LAZY)
    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }

  

    @Transient
    public Unidade getUnidadeCorporativa() {
        if (!CollectionUtils.isEmpty(unidades)) {
            for (Unidade unidade : unidades) {
                if (unidade.getTipoUnidade().equals(TipoUnidadeAtividadeEnum.CORPORATIVO)) {
                    return unidade;
                }
            }
        }
        return null;
    }

    @Transient
    public List<Unidade> getUnidadesNegocio() {
        if (!CollectionUtils.isEmpty(unidades)) {
            List<Unidade> unidadesNegocio = new ArrayList<Unidade>();
            for (Unidade unidade : unidades) {
                if (unidade.getTipoUnidade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
                    unidadesNegocio.add(unidade);
                }
            }
            return unidadesNegocio;
        }
        return null;
    }

    @Transient
    public List<Atividade> getAtividadesNegocio() {
        if (!CollectionUtils.isEmpty(atividades)) {
            List<Atividade> atividadesNegocio = new ArrayList<Atividade>();
            for (Atividade atividade : atividades) {
                if (atividade.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
                    atividadesNegocio.add(atividade);
                }
            }
            return atividadesNegocio;
        }
        return null;
    }

    @Transient
    public List<Atividade> getAtividadesCorporativa() {
        if (!CollectionUtils.isEmpty(atividades)) {
            List<Atividade> atividadesCorporativa = new ArrayList<Atividade>();
            for (Atividade atividade : atividades) {
                if (atividade.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.CORPORATIVO)) {
                    atividadesCorporativa.add(atividade);
                }
            }
            return atividadesCorporativa;
        }
        return null;
    }

    @Transient
    public List<Atividade> getAtividadesMatriz() {
        if (!CollectionUtils.isEmpty(atividades)) {
            List<Atividade> atividadesCorporativa = new ArrayList<Atividade>();
            for (Atividade atividade : atividades) {
                if (atividade.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)
                        && atividade.getUnidade() == null) {
                    atividadesCorporativa.add(atividade);
                }
            }
            return atividadesCorporativa;
        }
        return null;
    }

    @Transient
    public Integer getPercentualNegocioInt() {
        return Integer.valueOf(new BigDecimal(Constantes.CEM)
                .subtract(numeroFatorRelevanciaUC.multiply(new BigDecimal(Constantes.CEM))).toBigInteger().toString());
    }

    @Transient
    public String getPercentualNegocio() {
        return getPercentualNegocioInt().toString() + PERCENTUAL;
    }

    @Transient
    public Integer getPercentualCorporativoInt() {
        return Integer.valueOf(numeroFatorRelevanciaUC.multiply(new BigDecimal(Constantes.CEM)).toBigInteger()
                .toString());

    }

    @Transient
    public String getPercentualCorporativo() {
        return getPercentualCorporativoInt().toString() + PERCENTUAL;

    }
    
    //TODO Zito - refatorar métodos para eliminar código duplicado
    @Transient
    public Integer getPercentualBlocoAtividadesInt() {
        if (numeroFatorRelevanciaAE == null) {
            numeroFatorRelevanciaAE = BigDecimal.ZERO;
        }
        return Integer.valueOf(new BigDecimal(Constantes.CEM)
                .subtract(numeroFatorRelevanciaAE.multiply(new BigDecimal(Constantes.CEM))).toBigInteger().toString());
    }

    @Transient
    public String getPercentualBlocoAtividades() {
        return getPercentualBlocoAtividadesInt().toString() + PERCENTUAL;
    }

    @Transient
    public Integer getPercentualGovernancaCorporativoInt() {
        if (numeroFatorRelevanciaAE == null) {
            return Integer.valueOf(BigDecimal.ZERO.toString());
        }
        return Integer.valueOf(numeroFatorRelevanciaAE.multiply(new BigDecimal(Constantes.CEM)).toBigInteger()
                .toString());

    }

    @Transient
    public String getPercentualGovernancaCorporativa() {
        return getPercentualGovernancaCorporativoInt().toString() + PERCENTUAL;

    }
}
