package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.math.BigDecimal;
import java.util.LinkedList;
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

import org.hibernate.annotations.ForeignKey;

import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "QPF_QUADRO_POSICAO_FINANCEIRA", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoVersionadorAuditavelVersionado.PROP_ID,
                column = @Column(name = QuadroPosicaoFinanceira.CAMPO_ID)),
        @AttributeOverride(name = ObjetoVersionadorAuditavelVersionado.PROP_OPERADOR_ATUALIZACAO,
                column = @Column(name = "QPF_CD_OPERADOR", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoVersionadorAuditavelVersionado.PROP_ULTIMA_ATUALIZACAO,
                column = @Column(name = "QPF_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoVersionadorAuditavelVersionado.PROP_VERSAO,
                column = @Column(name = "QPF_CD_VERSAO", nullable = false))})
public class QuadroPosicaoFinanceira extends ObjetoVersionadorAuditavelVersionado {

    public static final String PROP_QUADRO_POSICAO_FINANCEIRA = "quadroPosicaoFinanceira";
    public static final String CAMPO_ID = "QPF_ID";
    private static final String DECIMAL_COLUMN_DEFINITION = "decimal(5,2)";
    private Integer codigoDataBase;
    private Ciclo ciclo;
    private VersaoPerfilRisco versaoPerfilRisco;

    private Integer prNivelUm;
    private Integer ajustePrNivelUm;
    private Integer capitalPrincipal;
    private Integer ajusteCapitalPrincipal;

    private Integer capitalComplementar;
    private Integer ajusteCapitalComplementar;
    private Integer prNivelDois;
    private Integer ajustePrNivelDois;

    private BigDecimal indiceBaseleia;
    private BigDecimal indiceBaseleiaAjustado;
    private BigDecimal indiceBaseleiaAmplo;
    private BigDecimal indiceBaseleiaAmploAjustado;
    private BigDecimal indiceImobilizacao;
    private BigDecimal indiceImobilizacaoAjustado;
    
    private List<OutraInformacaoQuadroPosicaoFinanceira> outrasInformacoesQuadro = 
            new LinkedList<OutraInformacaoQuadroPosicaoFinanceira>();

    private List<ContaQuadroPosicaoFinanceira> contas = new LinkedList<ContaQuadroPosicaoFinanceira>();
    private List<ResultadoQuadroPosicaoFinanceira> resultados = new LinkedList<ResultadoQuadroPosicaoFinanceira>();
    
    private List<AnexoQuadroPosicaoFinanceira> anexosQuadro;
    
    
    
    
    @OneToMany(mappedBy = PROP_QUADRO_POSICAO_FINANCEIRA, fetch = FetchType.LAZY)
    public List<AnexoQuadroPosicaoFinanceira> getAnexosQuadro() {
        return anexosQuadro;
    }

    public void setAnexosQuadro(List<AnexoQuadroPosicaoFinanceira> anexosQuadro) {
        this.anexosQuadro = anexosQuadro;
    }


    @Column(name = "QPF_CD_DTB")
    public Integer getCodigoDataBase() {
        return codigoDataBase;
    }

    public void setCodigoDataBase(Integer codigoDataBase) {
        this.codigoDataBase = codigoDataBase;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "IRQPFCIC")
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "IRQPFVER")
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = true)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }

    @Column(name = "QPF_VL_PR_NIVEL_1")
    public Integer getPrNivelUm() {
        return prNivelUm;
    }

    public void setPrNivelUm(Integer prNivelUm) {
        this.prNivelUm = prNivelUm;
    }

    @Column(name = "QPF_VL_AJUSTE_PR_NIVEL_1")
    public Integer getAjustePrNivelUm() {
        return ajustePrNivelUm;
    }

    public void setAjustePrNivelUm(Integer ajustePrNivelUm) {
        this.ajustePrNivelUm = ajustePrNivelUm;
    }

    @Column(name = "QPF_VL_CAPITAL_PRINCIPAL")
    public Integer getCapitalPrincipal() {
        return capitalPrincipal;
    }

    public void setCapitalPrincipal(Integer capitalPrincipal) {
        this.capitalPrincipal = capitalPrincipal;
    }

    @Column(name = "QPF_VL_AJUSTE_CAPITAL_PRINCIPAL")
    public Integer getAjusteCapitalPrincipal() {
        return ajusteCapitalPrincipal;
    }

    public void setAjusteCapitalPrincipal(Integer ajusteCapitalPrincipal) {
        this.ajusteCapitalPrincipal = ajusteCapitalPrincipal;
    }

    @Column(name = "QPF_VL_CAPITAL_COMPLEMENTAR")
    public Integer getCapitalComplementar() {
        return capitalComplementar;
    }

    public void setCapitalComplementar(Integer capitalComplementar) {
        this.capitalComplementar = capitalComplementar;
    }

    @Column(name = "QPF_VL_AJUSTE_CAPITAL_COMPLEMENTAR")
    public Integer getAjusteCapitalComplementar() {
        return ajusteCapitalComplementar;
    }

    public void setAjusteCapitalComplementar(Integer ajusteCapitalComplementar) {
        this.ajusteCapitalComplementar = ajusteCapitalComplementar;
    }

    @Column(name = "QPF_VL_PR_NIVEL_2")
    public Integer getPrNivelDois() {
        return prNivelDois;
    }

    public void setPrNivelDois(Integer prNivelDois) {
        this.prNivelDois = prNivelDois;
    }

    @Column(name = "QPF_VL_AJUSTE_PR_NIVEL_2")
    public Integer getAjustePrNivelDois() {
        return ajustePrNivelDois;
    }

    public void setAjustePrNivelDois(Integer ajustePrNivelDois) {
        this.ajustePrNivelDois = ajustePrNivelDois;
    }

    @Column(name = "QPF_VL_IND_BASILEIA", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceBaseleia() {
        return indiceBaseleia;
    }

    public void setIndiceBaseleia(BigDecimal indiceBaseleia) {
        this.indiceBaseleia = indiceBaseleia;
    }

    @Column(name = "QPF_VL_IND_BASILEIA_AJUSTADO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceBaseleiaAjustado() {
        return indiceBaseleiaAjustado;
    }

    public void setIndiceBaseleiaAjustado(BigDecimal indiceBaseleiaAjustado) {
        this.indiceBaseleiaAjustado = indiceBaseleiaAjustado;
    }

    @Column(name = "QPF_VL_IND_BASILEIA_AMPL", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceBaseleiaAmplo() {
        return indiceBaseleiaAmplo;
    }

    public void setIndiceBaseleiaAmplo(BigDecimal indiceBaseleiaAmplo) {
        this.indiceBaseleiaAmplo = indiceBaseleiaAmplo;
    }

    @Column(name = "QPF_VL_IND_BASILEIA_AMPL_AJUSTADO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceBaseleiaAmploAjustado() {
        return indiceBaseleiaAmploAjustado;
    }

    public void setIndiceBaseleiaAmploAjustado(BigDecimal indiceBaseleiaAmploAjustado) {
        this.indiceBaseleiaAmploAjustado = indiceBaseleiaAmploAjustado;
    }

    @Column(name = "QPF_VL_IND_IMOBILIZACAO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceImobilizacao() {
        return indiceImobilizacao;
    }

    public void setIndiceImobilizacao(BigDecimal indiceImobilizacao) {
        this.indiceImobilizacao = indiceImobilizacao;
    }

    @Column(name = "QPF_VL_IND_IMOBILIZACAO_AJUSTADO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getIndiceImobilizacaoAjustado() {
        return indiceImobilizacaoAjustado;
    }

    public void setIndiceImobilizacaoAjustado(BigDecimal indiceImobilizacaoAjustado) {
        this.indiceImobilizacaoAjustado = indiceImobilizacaoAjustado;
    }

    @OneToMany(mappedBy = ContaQuadroPosicaoFinanceira.QUADRO_POSICAO_FINANCEIRA,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    public List<ContaQuadroPosicaoFinanceira> getContas() {
        return contas;
    }

    public void setContas(List<ContaQuadroPosicaoFinanceira> contas) {
        this.contas = contas;
    }

    @OneToMany(mappedBy = ResultadoQuadroPosicaoFinanceira.QUADRO_POSICAO_FINANCEIRA,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    public List<ResultadoQuadroPosicaoFinanceira> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoQuadroPosicaoFinanceira> resultados) {
        this.resultados = resultados;
    }

    @OneToMany(mappedBy = OutraInformacaoQuadroPosicaoFinanceira.QUADRO_POSICAO_FINANCEIRA,
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    public List<OutraInformacaoQuadroPosicaoFinanceira> getOutrasInformacoesQuadro() {
        return outrasInformacoesQuadro;
    }

    public void setOutrasInformacoesQuadro(List<OutraInformacaoQuadroPosicaoFinanceira> outrasInformacoesQuadro) {
        this.outrasInformacoesQuadro = outrasInformacoesQuadro;
    }
    
}
