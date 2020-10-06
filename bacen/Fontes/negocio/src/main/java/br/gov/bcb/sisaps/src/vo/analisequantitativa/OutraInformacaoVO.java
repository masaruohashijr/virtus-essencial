package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEdicaoInformacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class OutraInformacaoVO implements Serializable {
    
    private Integer pk;
    private TipoInformacaoEnum tipoInformacaoEnum;
    private TipoEdicaoInformacaoEnum tipoEdicaoInformacaoEnum;
    private SimNaoEnum exibirNulo;
    private String descricao;
    private BigDecimal valor;
    private BigDecimal valorEditado;
    private Integer numeroInteiros;
    private Integer numeroDecimais;
    private Integer periodo;
    
    public OutraInformacaoVO() {
        // TODO Auto-generated constructor stub
    }
    
    public OutraInformacaoVO(Integer pk, Integer periodo, TipoInformacaoEnum tipoInformacaoEnum, 
            TipoEdicaoInformacaoEnum tipoEdicaoInformacaoEnum, SimNaoEnum exibirNulo, String descricao, 
            BigDecimal valor, BigDecimal valorEditado, Integer numeroCasasInteiras, Integer numeroCasasDecimais) {
        super();
        this.pk = pk;
        this.periodo = periodo;
        this.tipoInformacaoEnum = tipoInformacaoEnum;
        this.tipoEdicaoInformacaoEnum = tipoEdicaoInformacaoEnum;
        this.exibirNulo = exibirNulo;
        this.descricao = descricao;
        this.valor = valor;
        this.valorEditado = valorEditado;
        this.numeroInteiros = numeroCasasInteiras;
        this.numeroDecimais = numeroCasasDecimais;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public TipoInformacaoEnum getTipoInformacaoEnum() {
        return tipoInformacaoEnum;
    }
    
    public void setTipoInformacaoEnum(TipoInformacaoEnum tipoInformacaoEnum) {
        this.tipoInformacaoEnum = tipoInformacaoEnum;
    }
    
    public TipoEdicaoInformacaoEnum getTipoEdicaoInformacaoEnum() {
        return tipoEdicaoInformacaoEnum;
    }
    
    public void setTipoEdicaoInformacaoEnum(TipoEdicaoInformacaoEnum tipoEdicaoInformacaoEnum) {
        this.tipoEdicaoInformacaoEnum = tipoEdicaoInformacaoEnum;
    }
    
    public SimNaoEnum getExibirNulo() {
        return exibirNulo;
    }

    public void setExibirNulo(SimNaoEnum exibirNulo) {
        this.exibirNulo = exibirNulo;
    }

    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public BigDecimal getValorEditado() {
        return valorEditado;
    }
    
    public void setValorEditado(BigDecimal valorEditado) {
        this.valorEditado = valorEditado;
    }
    
    public Integer getNumeroInteiros() {
        return numeroInteiros;
    }

    public void setNumeroInteiros(Integer numeroInteiros) {
        this.numeroInteiros = numeroInteiros;
    }

    public Integer getNumeroDecimais() {
        return numeroDecimais;
    }

    public void setNumeroDecimais(Integer numeroDecimais) {
        this.numeroDecimais = numeroDecimais;
    }
    
    public String getDescricaoPeriodo() {
        return descricao + periodo;
    }

    public BigDecimal getValorAjustado() {
        BigDecimal valorAjustado = null;
        if (tipoEdicaoInformacaoEnum.equals(TipoEdicaoInformacaoEnum.AJUSTE)) {
            if (valor != null && valorEditado != null) {
                valorAjustado = valor.subtract(valorEditado);
            } else if (valor == null && valorEditado != null) {
                valorAjustado = valorEditado;
            }
        } else {
            valorAjustado = valorEditado;
        }
        return valorAjustado;
    }
    
    public String getValorAjustadoFormatado() {
        String valorFormatado = "";
        BigDecimal valor = getValorAjustado();
        if (valor != null) {
            valorFormatado = formatarValor(valor);
        }
        return valorFormatado;
    }
    
    public String getValorFormatado() {
        String valorFormatado = "";
        if (valor != null) {
            valorFormatado = formatarValor(valor);
        }
        return valorFormatado;
    }
    
    public String getValorEditadoFormatado() {
        String valorFormatado = "";
        if (valorEditado != null) {
            valorFormatado = formatarValor(valorEditado);
        }
        return valorFormatado;
    }

    private String formatarValor(BigDecimal valor) {
        return ajustar(valor);
    }
    
    private String ajustar(BigDecimal valor) {
        valor.setScale(4, RoundingMode.HALF_EVEN);
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(numeroDecimais);
        df.setDecimalFormatSymbols(criarDecimalFormatSymbols());
        return df.format(valor);
    }
    
    private DecimalFormatSymbols criarDecimalFormatSymbols() {
        return new DecimalFormatSymbols(new Locale("pt", "BR"));
    }
    
    public String obterFormato(boolean isBigDecimal) {
        StringBuilder formato = new StringBuilder();
        
        String separadorInteiro = null;
        String separadorDecimal = null;
        
        if (isBigDecimal) {
            separadorInteiro = Constantes.VIRGULA;
            separadorDecimal = Constantes.PONTO;
        } else {
            separadorInteiro = Constantes.PONTO;
            separadorDecimal = Constantes.VIRGULA;
        }
        
        for (int i = 0; i < numeroInteiros; i++) {
            if (formato.length() != 0 && ((formato.toString()
                    .replaceAll(isBigDecimal ? separadorInteiro : "\\.", "").length() % 3) == 0)) {
                formato.insert(0, separadorInteiro);
            }
            formato.insert(0, Constantes.MARCADOR_NOME_CAMPO);
        }
        
        if (numeroDecimais > 0) {
            formato.append(separadorDecimal);
            for (int i = 0; i < numeroDecimais; i++) {
                formato.append(Constantes.MARCADOR_NOME_CAMPO);
            }
        }
        
        return formato.toString();
    }
    
    public String getPeriodoFormatado() {
        return DataUtil.formatoMesAno(String.valueOf(periodo));
    }
    
}
