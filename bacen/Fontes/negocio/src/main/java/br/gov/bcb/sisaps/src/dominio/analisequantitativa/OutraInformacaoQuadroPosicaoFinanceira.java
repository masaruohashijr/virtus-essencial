package br.gov.bcb.sisaps.src.dominio.analisequantitativa;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "OQP_OUTRA_INFO_QUADRO_POSICAO_FINAN", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ID,
                column = @Column(name = OutraInformacaoQuadroPosicaoFinanceira.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_OPERADOR_ATUALIZACAO,
                column = @Column(name = "OQP_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_ULTIMA_ATUALIZACAO,
                column = @Column(name = "OQP_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO,
                column = @Column(name = "OQP_NU_VERSAO", nullable = false))})
public class OutraInformacaoQuadroPosicaoFinanceira extends ObjetoPersistenteAuditavelVersionado<Integer> {
    
    public static final String CAMPO_ID = "OQP_ID";
    public static final String QUADRO_POSICAO_FINANCEIRA = "quadroPosicaoFinanceira";
    private static final String DECIMAL_COLUMN_DEFINITION = "DECIMAL(7,2)";
    
    private QuadroPosicaoFinanceira quadroPosicaoFinanceira;
    private LayoutOutraInformacaoAnaliseQuantitativa layoutOutraInformacaoAnaliseQuantitativa;
    private Integer periodo;
    private BigDecimal valor;
    private BigDecimal valorEditado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = QuadroPosicaoFinanceira.CAMPO_ID, nullable = false)
    public QuadroPosicaoFinanceira getQuadroPosicaoFinanceira() {
        return quadroPosicaoFinanceira;
    }

    public void setQuadroPosicaoFinanceira(QuadroPosicaoFinanceira quadroPosicaoFinanceira) {
        this.quadroPosicaoFinanceira = quadroPosicaoFinanceira;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LayoutOutraInformacaoAnaliseQuantitativa.CAMPO_ID, nullable = false)
    public LayoutOutraInformacaoAnaliseQuantitativa getLayoutOutraInformacaoAnaliseQuantitativa() {
        return layoutOutraInformacaoAnaliseQuantitativa;
    }
    
    public void setLayoutOutraInformacaoAnaliseQuantitativa(
            LayoutOutraInformacaoAnaliseQuantitativa layoutOutraInformacaoAnaliseQuantitativa) {
        this.layoutOutraInformacaoAnaliseQuantitativa = layoutOutraInformacaoAnaliseQuantitativa;
    }

    @Column(name = "OQP_NU_PERIODO")
    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    @Column(name = "OQP_VL_VALOR", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Column(name = "OQP_VL_VALOR_EDITADO", columnDefinition = DECIMAL_COLUMN_DEFINITION)
    public BigDecimal getValorEditado() {
        return valorEditado;
    }

    public void setValorEditado(BigDecimal valorEditado) {
        this.valorEditado = valorEditado;
    }
    
}
