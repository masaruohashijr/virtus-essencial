package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.convert.IConverter;

import br.gov.bcb.sisaps.util.Constantes;

public class FormatarNumericoBigDecimal implements IConverter<BigDecimal> {

    private static final String TRACO = "-";
    private final String formato;
    
    public FormatarNumericoBigDecimal() {
        this.formato = "#,###";
    }
    
    public FormatarNumericoBigDecimal(String formato) {
        this.formato = formato;
    }

    @Override
    public BigDecimal convertToObject(String value, Locale locale) {
        String valor = value.replace(Constantes.PONTO, "");
        valor = valor.replace(",", Constantes.PONTO);
        if (StringUtils.isBlank(valor)) {
            return null;
        } else if (value.startsWith(TRACO) && value.length() == 1) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(valor);
    }

    @Override
    public String convertToString(BigDecimal value, Locale locale) {
        DecimalFormat decimalFormat = new DecimalFormat(formato);
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
        value.setScale(4, RoundingMode.HALF_EVEN);
        return decimalFormat.format(value);
    }

}
