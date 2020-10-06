package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
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

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "AQT_AVAL_ANALISE_QUANT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AnaliseQuantitativaAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AQT_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AQT_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "AQT_NU_VERSAO"))})
public class AnaliseQuantitativaAQT extends ObjetoVersionadorAuditavelVersionado {

    private static final String DEFINICAO_DECIMAL_5_2 = "Decimal(5,2)";

    public static final String CAMPO_ID = "AQT_ID";
    public static final String PROP_ANALISE_QUANTITATIVA_AQT = "analiseQuantitativaAQT";
    private static final String H2 = "h";
    private EstadoAQTEnum estado;
    private ParametroNotaAQT notaSupervisor;
    private ParametroNotaAQT notaCorecAnterior;
    private ParametroNotaAQT notaCorecAtual;
    private BigDecimal valorNota;
    private ParametroAQT parametroAQT;
    private DesignacaoAQT designacao;
    private DelegacaoAQT delegacao;
    private Ciclo ciclo;
    private VersaoPerfilRisco versaoPerfilRisco;
    private PesoAQT pesoAQT;
    private DateTime dataConclusao;
    private DateTime dataPreenchido;
    private DateTime dataAnalise;
    private String operadorConclusao;
    private String operadorPreenchido;
    private String operadorAnalise;
    private List<AnexoAQT> anexosAqt;
    private List<ElementoAQT> elementos = new ArrayList<ElementoAQT>();
    private List<AvaliacaoAQT> avaliacoesAnef = new ArrayList<AvaliacaoAQT>();
    private boolean alterouNota;

    @Column(name = "AQT_CD_ESTADO", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = EstadoAQTEnum.CLASS_NAME)})
    public EstadoAQTEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoAQTEnum estado) {
        this.estado = estado;
    }

    @ManyToOne
    @JoinColumn(name = "PNA_ID_SUPERVISOR")
    public ParametroNotaAQT getNotaSupervisor() {
        return notaSupervisor;
    }

    public void setNotaSupervisor(ParametroNotaAQT notaSupervisor) {
        this.notaSupervisor = notaSupervisor;
    }

    @ManyToOne
    @JoinColumn(name = "PNA_ID_COREC_ANTERIOR")
    public ParametroNotaAQT getNotaCorecAnterior() {
        return notaCorecAnterior;
    }

    public void setNotaCorecAnterior(ParametroNotaAQT notaCorecAnterior) {
        this.notaCorecAnterior = notaCorecAnterior;
    }

    @ManyToOne
    @JoinColumn(name = "PNA_ID_COREC_ATUAL")
    public ParametroNotaAQT getNotaCorecAtual() {
        return notaCorecAtual;
    }

    public void setNotaCorecAtual(ParametroNotaAQT notaCorecAtual) {
        this.notaCorecAtual = notaCorecAtual;
    }

    @Column(name = "AQT_VL_NOTA", columnDefinition = DEFINICAO_DECIMAL_5_2)
    public BigDecimal getValorNota() {
        return valorNota;
    }

    public void setValorNota(BigDecimal valorNota) {
        this.valorNota = valorNota;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = ParametroAQT.CAMPO_ID)
    public ParametroAQT getParametroAQT() {
        return parametroAQT;
    }

    public void setParametroAQT(ParametroAQT parametroAQT) {
        this.parametroAQT = parametroAQT;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @Override
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = true)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @Override
    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }

    @OneToOne(mappedBy = PROP_ANALISE_QUANTITATIVA_AQT)
    public DesignacaoAQT getDesignacao() {
        return designacao;
    }

    public void setDesignacao(DesignacaoAQT designacao) {
        this.designacao = designacao;
    }

    @OneToOne(mappedBy = PROP_ANALISE_QUANTITATIVA_AQT)
    public DelegacaoAQT getDelegacao() {
        return delegacao;
    }

    public void setDelegacao(DelegacaoAQT delegacao) {
        this.delegacao = delegacao;
    }

    @Column(name = "AQT_DH_CONCLUSAO")
    public DateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(DateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    @Column(name = "AQT_DH_PREENCHIDO")
    public DateTime getDataPreenchido() {
        return dataPreenchido;
    }

    public void setDataPreenchido(DateTime dataPreenchido) {
        this.dataPreenchido = dataPreenchido;
    }

    @Column(name = "AQT_DH_ANALISE")
    public DateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(DateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    @Column(name = "AQT_CD_OPER_CONCLUSAO")
    public String getOperadorConclusao() {
        return operadorConclusao;
    }

    public void setOperadorConclusao(String operadorConclusao) {
        this.operadorConclusao = operadorConclusao;
    }

    @Column(name = "AQT_CD_OPER_PREENCHIDO")
    public String getOperadorPreenchido() {
        return operadorPreenchido;
    }

    public void setOperadorPreenchido(String operadorPreenchido) {
        this.operadorPreenchido = operadorPreenchido;
    }

    @Column(name = "AQT_CD_OPER_ANALISE")
    public String getOperadorAnalise() {
        return operadorAnalise;
    }

    public void setOperadorAnalise(String operadorAnalise) {
        this.operadorAnalise = operadorAnalise;
    }

    @OneToMany(mappedBy = PROP_ANALISE_QUANTITATIVA_AQT, fetch = FetchType.LAZY)
    public List<AnexoAQT> getAnexosAqt() {
        return anexosAqt;
    }

    public void setAnexosAqt(List<AnexoAQT> anexosAqt) {
        this.anexosAqt = anexosAqt;
    }

    @OneToMany(mappedBy = PROP_ANALISE_QUANTITATIVA_AQT, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<ElementoAQT> getElementos() {
        return elementos;
    }

    public void setElementos(List<ElementoAQT> elementos) {
        this.elementos = elementos;
    }

    @Transient
    public String getNotaVigenteDescricaoValor() {
        ParametroNotaAQT notavigente = getNotaVigente();
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        } else if (getNovaNotaVigente() != null) {
            return getValorNota().toString().replace('.', ',');
        }
        return "";
    }

    @Transient
    public ParametroNotaAQT getNotaVigente() {
        if (getNotaSupervisor() != null) {
            return getNotaSupervisor();
        }
        return null;
    }

    @Transient
    public BigDecimal getNovaNotaVigente() {
        if (getValorNota() != null) {
            return getValorNota();
        }
        return null;
    }

    @Transient
    public String getValorNotaDescricao() {
        if (getNovaNotaVigente() != null) {
            return getValorNota().toString().replace('.', ',');
        }
        return "";
    }

    @Transient
    public String getNotaSupervisorDescricaoValor() {
        ParametroNotaAQT notaSupervisor = getNotaSupervisor();
        if (notaSupervisor != null) {
            return notaSupervisor.getDescricaoValor();
        } else if (getValorNota() != null) {
            return getValorNota().compareTo(BigDecimal.ZERO) == 1 ? getValorNota().toString().replace('.', ',') : "N/A";
        }
        return Constantes.ASTERISCO_A;
    }

    @Transient
    public boolean possuiDesignacao() {
        return designacao != null;
    }

    @Transient
    public PesoAQT getPesoAQT() {
        return pesoAQT;
    }

    public void setPesoAQT(PesoAQT pesoAQT) {
        this.pesoAQT = pesoAQT;
    }

    @Transient
    public String getNotaCorecAnteriorOuVigente() {
        ParametroNotaAQT notavigente = getNotaVigente();
        if (notaCorecAnterior != null) {
            return notaCorecAnterior.getDescricaoValor();
        }
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        }
        return Constantes.ASTERISCO_A;
    }

    @Transient
    public boolean getAlterouNota() {
        return alterouNota;
    }

    public void setAlterouNota(boolean alterouNota) {
        this.alterouNota = alterouNota;
    }

    @Override
    @Transient
    public String getData(DateTime data) {
        return data == null ? "" : data.toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS) + H2;
    }

    @Transient
    public String getNotaCalculadaInspetor() {
        if (getCiclo().getMetodologia().getIsCalculoMedia() != null
                && getCiclo().getMetodologia().getIsCalculoMedia().equals(SimNaoEnum.SIM)
                && getNotaSupervisor() == null) {
            return getMediaNotaElementosInspetor();
        } else {
            ParametroNotaAQT parametroMenorNota = getParametroMenorNotaElementosInspetor();

            if (parametroMenorNota == null) {
                return "";
            } else {
                return parametroMenorNota.getDescricaoValor();
            }
        }
    }

    @Transient
    public String getNotaCalculadaSupervisorOuInspetor() {
        if (getCiclo().getMetodologia().getIsCalculoMedia() != null
                && getCiclo().getMetodologia().getIsCalculoMedia().equals(SimNaoEnum.SIM)
                && getNotaSupervisor() == null) {
            return getMediaNotaElementosSupervisor();
        } else {
            ParametroNotaAQT parametroMenorNota = getParametroMenorNotaElementosSupervisorInspetor();

            if (parametroMenorNota == null) {
                return "";
            } else {
                return parametroMenorNota.getDescricaoValor();
            }
        }
    }

    @Transient
    public String getNotaCalculadaSupervisor() {
        ParametroNotaAQT parametroMenorNota = getParametroMenorNotaElementosSupervisor();

        if (parametroMenorNota == null) {
            return "";
        } else {
            return parametroMenorNota.getDescricaoValor();
        }

    }

    @Transient
    public ParametroNotaAQT getParametroMenorNotaElementosSupervisorInspetor() {
        //A pior nota e a nota de maior valor
        ParametroNotaAQT parametroPiorNota = null;

        if (CollectionUtils.isNotEmpty(elementos)) {
            for (ElementoAQT elemento : elementos) {
                ParametroNotaAQT notaElemento =
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
    public ParametroNotaAQT getParametroMenorNotaElementosInspetor() {
        ParametroNotaAQT parametroPiorNota = null;

        if (CollectionUtils.isNotEmpty(elementos)) {
            for (ElementoAQT elemento : elementos) {
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
    public ParametroNotaAQT getParametroMenorNotaElementosSupervisor() {
        //A pior nota e a nota de maior valor
        ParametroNotaAQT parametroPiorNota = null;

        if (CollectionUtils.isNotEmpty(elementos)) {
            for (ElementoAQT elemento : elementos) {
                if (elemento.getParametroNotaSupervisor() != null) {
                    if (parametroPiorNota == null
                            && elemento.getParametroNotaSupervisor().getValor().compareTo(BigDecimal.ZERO) >= 0) {
                        parametroPiorNota = elemento.getParametroNotaSupervisor();
                    } else if (parametroPiorNota != null
                            && elemento.getParametroNotaSupervisor().getValor().compareTo(parametroPiorNota.getValor()) == 1) {
                        parametroPiorNota = elemento.getParametroNotaSupervisor();
                    }
                }
            }
        }

        return parametroPiorNota;
    }

    @Transient
    public AvaliacaoAQT getAvaliacaoANEF() {
        AvaliacaoAQT supervisor = avaliacaoSupervisor();

        if (supervisor == null) {
            supervisor = getAvaliacaoInspetor();
        }

        return supervisor;
    }

    @Transient
    public AvaliacaoAQT avaliacaoSupervisor() {
        AvaliacaoAQT supervisor = null;
        if (CollectionUtils.isNotEmpty(avaliacoesAnef)) {
            for (AvaliacaoAQT avaliacaoAnef : avaliacoesAnef) {
                if (avaliacaoAnef.getPerfil().equals(PerfisNotificacaoEnum.SUPERVISOR)) {
                    supervisor = avaliacaoAnef;
                }
            }
        }
        return supervisor;
    }

    @Transient
    public AvaliacaoAQT getAvaliacaoInspetor() {
        if (CollectionUtils.isNotEmpty(avaliacoesAnef)) {
            for (AvaliacaoAQT avaliacaoAnef : avaliacoesAnef) {
                if (avaliacaoAnef.getPerfil().equals(PerfisNotificacaoEnum.INSPETOR)) {
                    return avaliacaoAnef;
                }
            }
        }
        return null;
    }

    @Transient
    public AvaliacaoAQT getAvaliacaoSupervisor() {
        if (CollectionUtils.isNotEmpty(avaliacoesAnef)) {
            for (AvaliacaoAQT avaliacaoAnef : avaliacoesAnef) {
                if (avaliacaoAnef.getPerfil().equals(PerfisNotificacaoEnum.SUPERVISOR)) {
                    return avaliacaoAnef;
                }
            }
        }
        return null;
    }

    @OneToMany(mappedBy = PROP_ANALISE_QUANTITATIVA_AQT, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<AvaliacaoAQT> getAvaliacoesAnef() {
        return avaliacoesAnef;
    }

    public void setAvaliacoesAnef(List<AvaliacaoAQT> avaliacoesAnef) {
        this.avaliacoesAnef = avaliacoesAnef;
    }

    @Transient
    public String getDataVersaoConsulta(PerfilRisco perfilAtual) {
        if (perfilAtual.getVersoesPerfilRisco().contains(getVersaoPerfilRisco())) {
            return "vigente";
        }
        return getDataFormatada();
    }

    @Transient
    public String getNotaCorec() {
        if (notaCorecAnterior != null) {
            return notaCorecAnterior.getDescricaoValor();
        } else if (notaCorecAtual != null) {
            return notaCorecAtual.getDescricaoValor();
        }
        return "";
    }

    @Transient
    public String getNotaCorecAtualDescricao() {
        if (notaCorecAtual != null) {
            return notaCorecAtual.getDescricaoValor();
        }
        return "";
    }

    @Transient
    public String getNotaCorecOuVigente() {
        if (notaCorecAnterior != null) {
            return notaCorecAnterior.getDescricaoValor();
        } else if (notaCorecAtual != null) {
            return notaCorecAtual.getDescricaoValor();
        }

        ParametroNotaAQT notavigente = getNotaVigente();
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        }
        return Constantes.ASTERISCO_A;
    }

    @Transient
    public boolean possuiNotaElementosSupervisor() {
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (ElementoAQT elemento : elementos) {
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
            for (ElementoAQT elemento : elementos) {
                if (elemento.getParametroNotaInspetor() != null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Transient
    public String getMediaNotaElementosInspetor() {
        BigDecimal somaNotas = BigDecimal.ZERO;
        BigDecimal mediaNotas = BigDecimal.ZERO;
        BigDecimal qtdNotas = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (ElementoAQT elemento : elementos) {
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
            for (ElementoAQT elemento : elementos) {
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
