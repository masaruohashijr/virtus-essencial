package br.gov.bcb.sisaps.util.geral;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class DataUtilLocalDate {

    public static final String FORMATO_DIA_MES_ANO = "dd/MM/yyyy";
    private static final String FORMATO_MES_ANO = "MM/yyyy";  
    private static final Locale LOCALE_BRASIL = new Locale("pt", "BR");

    public static String localDateToString(LocalDate data) {
        String dataConvertida = "";
        if (data != null) {
            dataConvertida = data.toString(FORMATO_DIA_MES_ANO, LOCALE_BRASIL);
        }
        return dataConvertida;
    }

    public static String converterLocalDateParaDataBase(LocalDate data) {
        return converterLocalDateParaDataBaseComPattern(FORMATO_MES_ANO, data);
    }

    public static String converterLocalDateParaDataBaseComPattern(String pattern, LocalDate data) {
        String texto = null;
        if (data != null) {
            texto = data.toString(pattern, LOCALE_BRASIL);
        }
        return texto;
    }

    public static String converterDateTimeParaDataBaseComPattern(String pattern, DateTime data) {
        String texto = null;
        if (data != null) {
            texto = data.toString(pattern, LOCALE_BRASIL);
        }
        return texto;
    }

    public static LocalDate stringToLocalDate(String data) {
        LocalDate dataConvertida = null;
        if (StringUtils.isNotBlank(data)) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(FORMATO_DIA_MES_ANO, LOCALE_BRASIL);
            dateFormatter.setLenient(false);
    
            Date d = formatarData(data, dateFormatter);
            dataConvertida = new LocalDate(d);
        }
    
        return dataConvertida;
    }

    private static Date formatarData(String data, SimpleDateFormat dateFormatter) {
        Date d = null;
    
        try {
            d = dateFormatter.parse(data);
        } catch (ParseException e) {
            throw new NegocioException(ConstantesMensagens.MSG_DATA_INVALIDA, e);
        }
        return d;
    }
}