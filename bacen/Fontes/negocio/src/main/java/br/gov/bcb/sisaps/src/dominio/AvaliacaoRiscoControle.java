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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "ARC_AVAL_RIS_CON", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AvaliacaoRiscoControle.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ARC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ARC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ARC_NU_VERSAO"))})
public class AvaliacaoRiscoControle extends ObjetoVersionadorAuditavelVersionado {

    public static final String CAMPO_ID = "ARC_ID";
    public static final String PROP_AVALIACAO_RISCO_CONTROLE = "avaliacaoRiscoControle";
    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    private static final int TAMANHO_END_MANUAL = 100;

    private BigDecimal percentualParticipacao;
    private String enderecoManual;
    private EstadoARCEnum estado;
    private Designacao designacao;
    private Delegacao delegacao;
    private ParametroNota notaSupervisor;
    private BigDecimal valorNota;
    private AvaliacaoRiscoControle avaliacaoRiscoControleVigente;
    private TipoGrupoEnum tipo;
    private List<Elemento> elementos = new ArrayList<Elemento>();
    private List<AnexoARC> anexosArc;
    private List<AvaliacaoARC> avaliacoesArc = new ArrayList<AvaliacaoARC>();
    private List<TendenciaARC> tendenciasArc = new ArrayList<TendenciaARC>();
    private ParametroNota notaCorec;
    private DateTime dataConclusao;
    private DateTime dataPreenchido;
    private DateTime dataAnalise;
    private String operadorConclusao;
    private String operadorPreenchido;
    private String operadorAnalise;
    private AvaliacaoRiscoControleExterno avaliacaoRiscoControleExterno;

    /**
     * Construtor padrão
     */
    public AvaliacaoRiscoControle() {
        // TODO não precisa ser implementado
    }

    public AvaliacaoRiscoControle(Integer pk) {
        setPk(pk);
    }

    @OneToMany(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE, fetch = FetchType.LAZY)
    public List<AvaliacaoARC> getAvaliacoesArc() {
        return avaliacoesArc;
    }

    public void setAvaliacoesArc(List<AvaliacaoARC> avaliacoesArc) {
        this.avaliacoesArc = avaliacoesArc;
    }

    @OneToMany(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE, fetch = FetchType.LAZY)
    public List<AnexoARC> getAnexosArc() {
        return anexosArc;
    }

    public void setAnexosArc(List<AnexoARC> anexosArc) {
        this.anexosArc = anexosArc;
    }

    @Column(name = "ARC_PC_PARTICIPACAO", columnDefinition = "decimal(5,2)")
    public BigDecimal getPercentualParticipacao() {
        return percentualParticipacao;
    }

    public void setPercentualParticipacao(BigDecimal percentualParticipacao) {
        this.percentualParticipacao = percentualParticipacao;
    }

    @Column(name = "ARC_DS_END_MANUAL", length = TAMANHO_END_MANUAL)
    public String getEnderecoManual() {
        return enderecoManual;
    }

    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    @Column(name = "ARC_CD_ESTADO", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = EstadoARCEnum.CLASS_NAME)})
    public EstadoARCEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoARCEnum estado) {
        this.estado = estado;
    }

    @OneToOne(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE)
    public Designacao getDesignacao() {
        return designacao;
    }

    public void setDesignacao(Designacao designacao) {
        this.designacao = designacao;
    }

    @OneToOne(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE)
    public Delegacao getDelegacao() {
        return delegacao;
    }

    public void setDelegacao(Delegacao delegacao) {
        this.delegacao = delegacao;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PNO_ID_SUPERVISOR")
    public ParametroNota getNotaSupervisor() {
        return notaSupervisor;
    }

    public void setNotaSupervisor(ParametroNota notaSupervisor) {
        this.notaSupervisor = notaSupervisor;
    }

    @Column(name = "ARC_VL_NOTA", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getValorNota() {
        return valorNota;
    }

    public void setValorNota(BigDecimal valorNota) {
        this.valorNota = valorNota;
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @OneToMany(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(List<Elemento> elementos) {
        this.elementos = elementos;
    }

    @OneToMany(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE, fetch = FetchType.LAZY)
    public List<TendenciaARC> getTendenciasArc() {
        return tendenciasArc;
    }

    public void setTendenciasArc(List<TendenciaARC> tendenciasArc) {
        this.tendenciasArc = tendenciasArc;
    }

    @OneToOne
    @JoinColumn(name = "ARC_ID_ANTERIOR", nullable = true)
    public AvaliacaoRiscoControle getAvaliacaoRiscoControleVigente() {
        return avaliacaoRiscoControleVigente;
    }

    public void setAvaliacaoRiscoControleVigente(AvaliacaoRiscoControle avaliacaoRiscoControleVigente) {
        this.avaliacaoRiscoControleVigente = avaliacaoRiscoControleVigente;
    }

    @Column(name = "ARC_CD_TIPO", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = "enumClass", value = TipoGrupoEnum.CLASS_NAME)})
    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }

    @ManyToOne
    @JoinColumn(name = "PNO_ID_COREC")
    public ParametroNota getNotaCorec() {
        return notaCorec;
    }

    public void setNotaCorec(ParametroNota notaCorec) {
        this.notaCorec = notaCorec;
    }

    @Column(name = "ARC_DH_CONCLUSAO")
    public DateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(DateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    @Column(name = "ARC_DH_PREENCHIDO")
    public DateTime getDataPreenchido() {
        return dataPreenchido;
    }

    public void setDataPreenchido(DateTime dataPreenchido) {
        this.dataPreenchido = dataPreenchido;
    }

    @Column(name = "ARC_DH_ANALISE")
    public DateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(DateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    @Column(name = "ARC_CD_OPER_CONCLUSAO")
    public String getOperadorConclusao() {
        return operadorConclusao;
    }

    public void setOperadorConclusao(String operadorConclusao) {
        this.operadorConclusao = operadorConclusao;
    }

    @Column(name = "ARC_CD_OPER_PREENCHIDO")
    public String getOperadorPreenchido() {
        return operadorPreenchido;
    }

    public void setOperadorPreenchido(String operadorPreenchido) {
        this.operadorPreenchido = operadorPreenchido;
    }

    @Column(name = "ARC_CD_OPER_ANALISE")
    public String getOperadorAnalise() {
        return operadorAnalise;
    }

    public void setOperadorAnalise(String operadorAnalise) {
        this.operadorAnalise = operadorAnalise;
    }

    @Transient
    public boolean possuiNotaElementosSupervisor() {
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaSupervisor() != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Transient
    public boolean possuiNotaElementosInspetor() {
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaInspetor() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Transient
    public String getNotaArrastoInspetor() {
        ParametroNota parametroMenorNota = getParametroMenorNotaElementosInspetor();

        if (parametroMenorNota == null) {
            return "";
        } else {
            return parametroMenorNota.getDescricaoValor();
        }
    }

    @Transient
    public ParametroNota getParametroMenorNotaElementosInspetor() {
        ParametroNota parametroPiorNota = null;

        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaInspetor() != null) {
                    if (parametroPiorNota == null
                            && elemento.getParametroNotaInspetor().getValor().compareTo(BigDecimal.ZERO) >= 0) {
                        parametroPiorNota = elemento.getParametroNotaInspetor();
                    } else if (parametroPiorNota != null
                            && elemento.getParametroNotaInspetor().getValor().compareTo(parametroPiorNota.getValor()) == 1) {
                        parametroPiorNota = elemento.getParametroNotaInspetor();
                    }
                }
            }
        }
        return parametroPiorNota;
    }

    @Transient
    public ParametroNota getParametroMenorNotaElementosSupervisorInspetor() {
        //A pior nota e a nota de maior valor
        ParametroNota parametroPiorNota = null;

        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                ParametroNota notaElemento =
                        elemento.getParametroNotaSupervisor() == null ? elemento.getParametroNotaInspetor() : elemento
                                .getParametroNotaSupervisor();
                if (parametroPiorNota == null && notaElemento != null
                        && notaElemento.getValor().compareTo(BigDecimal.ZERO) >= 0) {
                    parametroPiorNota = notaElemento;
                } else if (parametroPiorNota != null && notaElemento != null
                        && notaElemento.getValor().compareTo(parametroPiorNota.getValor()) == 1) {
                    parametroPiorNota = notaElemento;
                }
            }
        }

        if (parametroPiorNota == null) {
            parametroPiorNota = getParametroMenorNotaElementosInspetor();
        }

        return parametroPiorNota;
    }

    @Transient
    public String getNotaCalculadaSupervisor() {
        ParametroNota parametroMenorNota = getParametroMenorNotaElementosSupervisorInspetor();

        if (parametroMenorNota == null) {
            return "";
        } else {
            return parametroMenorNota.getDescricaoValor();
        }

    }

    @Transient
    public String getNotaCalculadaFinal() {
        return "".equals(getNotaCalculadaSupervisor()) ? getNotaArrastoInspetor() : getNotaCalculadaSupervisor();
    }

    @Transient
    public AvaliacaoARC getAvaliacaoARC() {
        AvaliacaoARC supervisor = avaliacaoSupervisor();

        if (supervisor == null || (supervisor != null && supervisor.getParametroNota() == null)) {
            supervisor = getAvaliacaoARCInspetor();
        }

        return supervisor;
    }

    @Transient
    public String getAvaliacaoArcDescricaoValor() {
        return getAvaliacaoARC() == null || getAvaliacaoARC().getParametroNota() == null ? "" : getAvaliacaoARC()
                .getParametroNota().getDescricaoValor();
    }

    @Transient
    public AvaliacaoARC avaliacaoSupervisor() {
        AvaliacaoARC supervisor = null;
        if (CollectionUtils.isNotEmpty(avaliacoesArc)) {
            for (AvaliacaoARC avaliacaoARC : avaliacoesArc) {
                if (avaliacaoARC.getPerfil().equals(PerfisNotificacaoEnum.SUPERVISOR)) {
                    supervisor = avaliacaoARC;
                }
            }
        }
        return supervisor;
    }

    @Transient
    public String avaliacaoSupervisorDescricao() {

        return avaliacaoSupervisor() == null || avaliacaoSupervisor().getParametroNota() == null ? ""
                : avaliacaoSupervisor().getParametroNota().getDescricaoValor();
    }

    @Transient
    public AvaliacaoARC getAvaliacaoARCVigente() {
        if (getAvaliacaoRiscoControleVigente() != null) {
            return getAvaliacaoRiscoControleVigente().getAvaliacaoARC();
        }
        return null;
    }

    @Transient
    public String getAvaliacaoVigenteDescricao() {

        return getAvaliacaoARCVigente() == null || getAvaliacaoARCVigente().getParametroNota() == null ? ""
                : getAvaliacaoARCVigente().getParametroNota().getValorString();
    }

    @Transient
    public AvaliacaoARC getAvaliacaoARCInspetor() {
        if (CollectionUtils.isNotEmpty(avaliacoesArc)) {
            for (AvaliacaoARC avaliacaoARC : avaliacoesArc) {
                if (avaliacaoARC.getPerfil().equals(PerfisNotificacaoEnum.INSPETOR)) {
                    return avaliacaoARC;
                }
            }
        }
        return null;
    }

    @Transient
    public String getAvaliacaoInspetorDescricao() {

        return getAvaliacaoARCInspetor() == null || getAvaliacaoARCInspetor().getParametroNota() == null ? ""
                : getAvaliacaoARCInspetor().getParametroNota().getDescricaoValor();
    }

    @Transient
    public TendenciaARC getTendenciaARCInspetorOuSupervisor() {
        TendenciaARC supervisor = getTendenciaARCSupervisor();

        if (supervisor == null) {
            supervisor = getTendenciaARCInspetor();
        }

        return supervisor;
    }

    @Transient
    public String getTendenciaARCInspetorOuSupervisorValor() {
        if (getTendenciaARCInspetorOuSupervisor() != null
                && getTendenciaARCInspetorOuSupervisor().getParametroTendencia() != null) {
            return getTendenciaARCInspetorOuSupervisor().getParametroTendencia().getNome();
        }
        return "";
    }

    @Transient
    public String getTendenciaARCInspetorOuSupervisorJustificativa() {
        if (getTendenciaARCInspetorOuSupervisor() != null
                && getTendenciaARCInspetorOuSupervisor().getJustificativa() != null) {
            return getTendenciaARCInspetorOuSupervisor().getJustificativa();
        }

        return "";
    }

    @Transient
    public TendenciaARC getTendenciaARCSupervisor() {
        TendenciaARC supervisor = null;
        if (CollectionUtils.isNotEmpty(tendenciasArc)) {
            ordenarTendencia();
            for (TendenciaARC tendenciaARC : tendenciasArc) {
                if (tendenciaARC.getPerfil().equals(PerfisNotificacaoEnum.SUPERVISOR)) {
                    supervisor = tendenciaARC;
                }
            }
        }
        return supervisor;
    }

    @Transient
    public String getTendenciaARCSupervisorValor() {
        if (getTendenciaARCSupervisor() != null && getTendenciaARCSupervisor().getParametroTendencia() != null) {
            return getTendenciaARCSupervisor().getParametroTendencia().getNome();
        }

        return "";
    }

    @Transient
    public TendenciaARC getTendenciaARCVigente() {
        if (EstadoARCEnum.CONCLUIDO.equals(estado)) {
            return getTendenciaARCInspetorOuSupervisor();
        } else if (getAvaliacaoRiscoControleVigente() != null) {
            return getAvaliacaoRiscoControleVigente().getTendenciaARCInspetorOuSupervisor();
        }
        return null;
    }

    @Transient
    public String getTendenciaARCVigenteValor() {
        if (getTendenciaARCVigente() != null && getTendenciaARCVigente().getParametroTendencia() != null) {
            return getTendenciaARCVigente().getParametroTendencia().getNome();
        }

        return "";
    }

    @Transient
    public TendenciaARC getTendenciaARCInspetor() {
        if (CollectionUtils.isNotEmpty(tendenciasArc)) {
            ordenarTendencia();
            for (TendenciaARC tendenciaARC : tendenciasArc) {
                if (tendenciaARC.getPerfil().equals(PerfisNotificacaoEnum.INSPETOR)) {
                    return tendenciaARC;
                }
            }
        }
        return null;
    }

    @Transient
    public String getTendenciaARCInspetorValor() {
        if (getTendenciaARCInspetor() != null && getTendenciaARCInspetor().getParametroTendencia() != null) {
            return getTendenciaARCInspetor().getParametroTendencia().getNome();
        }

        return "";
    }

    @SuppressWarnings("unchecked")
    private void ordenarTendencia() {
        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("pk"));
        cc.setReverseSort(0);
        Collections.sort(tendenciasArc, cc);
    }

    @Transient
    public ParametroNota getNotaVigente() {

        if (getAvaliacaoRiscoControleVigente() != null) {
            return getAvaliacaoRiscoControleVigente().getNotaSupervisor();
        }
        return null;
    }

    @Transient
    public BigDecimal getNovaNotaVigente() {
        if (getAvaliacaoRiscoControleVigente() != null) {
            return getAvaliacaoRiscoControleVigente().getValorNota();
        }
        return null;
    }

    @Transient
    public String getNotaVisivel() {
        if (getAvaliacaoARCInspetor() != null && getAvaliacaoARCInspetor().getParametroNota() != null) {
            return getAvaliacaoARCInspetor().getParametroNota().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor();

    }

    @Transient
    public String getNotaEmAnaliseDescricaoValor() {
        if (EstadoARCEnum.ANALISADO.equals(estado)) {
            return getNotaSupervisor() != null ? getNotaSupervisor().getValor().toString() : getValorNota().toString();
        }
        if (getAvaliacaoRiscoControleVigente() != null
                && getAvaliacaoRiscoControleVigente().getNotaCorec() != null) {
            return getAvaliacaoRiscoControleVigente().getNotaCorec().getDescricaoValor();
        }
        return getNotaVigenteDescricaoValor();
    }

    @Transient
    public String getNotaVigenteDescricaoValor() {
        ParametroNota notavigente = getNotaVigente();
        BigDecimal notaVigente = getNovaNotaVigente();
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        } else if (notaVigente != null) {
            return notaVigente.compareTo(BigDecimal.ZERO) == 1 ? notaVigente.toString().replace('.', ',') : "N/A";
        }
        return Constantes.ASTERISCO_A;
    }
    
    @Transient
    public BigDecimal getNotaVigenteValor() {
        ParametroNota notavigente = getNotaVigente();
        BigDecimal notaVigente = getNovaNotaVigente();
        if (notavigente != null) {
            return notavigente.getValor();
        } else if (notaVigente != null) {
            return notaVigente;
        }
        return BigDecimal.ZERO;
    }

    @Transient
    public String getNovaNotaVigenteDescricaoValor() {
        BigDecimal notaVigente = getNovaNotaVigente();
        if (notaVigente != null) {
            return notaVigente.compareTo(BigDecimal.ZERO) == 1 ? notaVigente.toString().replace('.', ',') : "N/A";
        }
        return Constantes.ASTERISCO_A;
    }

    @Transient
    public Integer getPkDesignacao() {
        if (getDesignacao() == null) {
            return null;
        } else {
            return getDesignacao().getPk();
        }

    }

    @Transient
    public String getNotaAvaliacaoSupervisor() {
        String notaAjustada = "";
        String notaAjustadaSupervisor = avaliacaoSupervisorDescricao();
        if (StringUtils.isNotBlank(notaAjustadaSupervisor)) {
            notaAjustada = notaAjustadaSupervisor;
        } else if (StringUtil.isVazioOuNulo(notaAjustadaSupervisor) && this.possuiNotaElementosSupervisor()) {
            notaAjustada = "";
        } else {
            notaAjustada = getAvaliacaoInspetorDescricao();
        }
        return notaAjustada;
    }
    
    @Transient
    public String getNotaCorecDescricao() {
        return notaCorec == null ? "" : notaCorec.getDescricaoValor(); 
    }

    @OneToOne(mappedBy = PROP_AVALIACAO_RISCO_CONTROLE)
    public AvaliacaoRiscoControleExterno getAvaliacaoRiscoControleExterno() {
        return avaliacaoRiscoControleExterno;
    }

    public void setAvaliacaoRiscoControleExterno(AvaliacaoRiscoControleExterno avaliacaoRiscoControleExterno) {
        this.avaliacaoRiscoControleExterno = avaliacaoRiscoControleExterno;
    }
    
    @Transient
    public String getMediaNotaElementosInspetor() {
        BigDecimal somaNotas = BigDecimal.ZERO;
        BigDecimal mediaNotas = BigDecimal.ZERO;
        BigDecimal qtdNotas = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaInspetor() != null) {
                    if (elemento.getParametroNotaInspetor().getValor().compareTo(BigDecimal.ZERO) == 1) {
                        somaNotas = somaNotas.add(elemento.getParametroNotaInspetor().getValor());
                        qtdNotas = qtdNotas.add(BigDecimal.ONE);
                    }
                }
            }
            if (somaNotas.compareTo(BigDecimal.ZERO) == 1) {
                mediaNotas = somaNotas.divide(qtdNotas, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
                mediaNotas = mediaNotas.setScale(2, RoundingMode.HALF_UP);
            }
        }
        return mediaNotas == BigDecimal.ZERO ? "" : mediaNotas.toString().replace('.', ',');
    }

    @Transient
    public String getMediaNotaElementosSupervisor() {
        BigDecimal somaNotas = BigDecimal.ZERO;
        BigDecimal mediaNotas = BigDecimal.ZERO;
        BigDecimal qtdNotas = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaSupervisor() != null) {
                    if (elemento.getParametroNotaSupervisor().getValor().compareTo(BigDecimal.ZERO) == 1) {
                        somaNotas = somaNotas.add(elemento.getParametroNotaSupervisor().getValor());
                        qtdNotas = qtdNotas.add(BigDecimal.ONE);
                    }
                } else if (elemento.getParametroNotaInspetor() != null) {
                    if (elemento.getParametroNotaInspetor().getValor().compareTo(BigDecimal.ZERO) == 1) {
                        somaNotas = somaNotas.add(elemento.getParametroNotaInspetor().getValor());
                        qtdNotas = qtdNotas.add(BigDecimal.ONE);
                    }
                }
            }
            if (somaNotas.compareTo(BigDecimal.ZERO) == 1) {
                mediaNotas = somaNotas.divide(qtdNotas, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_UP);
                mediaNotas = mediaNotas.setScale(2, RoundingMode.HALF_UP);
            }
        }
        return mediaNotas.compareTo(BigDecimal.ZERO) == 0 ? getMediaNotaElementosInspetor() : mediaNotas.toString()
                .replace('.', ',');
    }

    @Transient
    public String getMediaCalculadaFinal() {
        return "".equals(getMediaNotaElementosSupervisor()) ? getMediaNotaElementosInspetor()
                : getMediaNotaElementosSupervisor();
    }

}
