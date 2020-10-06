package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.convert.IConverter;

public class FormatarNumerico implements IConverter<Integer> {

    private static final String TRACO = "-";

    @Override
    public Integer convertToObject(String value, Locale locale) {
        String valor = value.replace(".", "");
        if (StringUtils.isBlank(valor)) {
            return null;
        } else if (value.startsWith(TRACO) && value.length() == 1) {
            return 0;
        }
        if (StringUtils.isNumeric(valor)) {
            return Integer.valueOf(valor);
        } else {
            if (value.startsWith(TRACO) && !StringUtils.isAlphanumeric(valor)) {
                return Integer.valueOf(valor);
            }
            return null;
        }
    }

    @Override
    public String convertToString(Integer value, Locale locale) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(new Locale("pt", "BR")));
        return decimalFormat.format(value);
    }

}
