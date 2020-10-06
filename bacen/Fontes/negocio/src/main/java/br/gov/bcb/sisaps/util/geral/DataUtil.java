/*
 * Sistema APS
 * DataUtil.java
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.util.Constantes;

public class DataUtil extends DataUtilLocalDate {
    public static final String DIA_01_MES_01 = "01/01/";
    public static final String DIA_31_MES_12 = "31/12/";
    private static final int INDICE_4 = 4;
    private static DateTime dateTimeAtual;
    private static final String FORMATO_DATA = "dd/MM/yyyy";
    private static final String FORMATO_DATA_BATCH = "yyyMMdd";

    public static LocalDate obterDataCorrenteSemHoras() {
        return removerHoras(new LocalDate());
    }

    public static LocalDate removerHoras(LocalDate data) {
        Calendar calendar = GregorianCalendar.getInstance();

        calendar.setTime(data.toDateTimeAtStartOfDay().toDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new LocalDate(calendar.getTime());
    }

    public static boolean estaEmPeriodoDeVigencia(LocalDate dataBase, LocalDate dtInicioVigencia,
            LocalDate dtFimVigencia, boolean considerarHoras) {
        boolean retorno = false;

        if (dataBase != null && dtInicioVigencia != null && !considerarHoras) {
            if (dtFimVigencia == null) {
                retorno = !dataBase.isBefore(dtInicioVigencia);
            } else {
                retorno = isDataBaseNoIntervalo(dataBase, dtInicioVigencia, dtFimVigencia);
            }
        }

        return retorno;
    }

    private static boolean isDataBaseNoIntervalo(LocalDate dataBase, LocalDate dtInicioVigencia, LocalDate dtFimVigencia) {
        return !dataBase.isBefore(dtInicioVigencia)
                && (dataBase.isBefore(dtFimVigencia) || dataBase.equals(dtFimVigencia));
    }

    public static boolean isDataMaior(Date dataBase, Date dataReferencia) {
        return dataBase.after(dataReferencia);
    }

    public static boolean isDataMenor(Date dataBase, Date dataReferencia) {
        return dataBase.before(dataReferencia);
    }

    public static void setDateTimeAtual(DateTime data) {
        dateTimeAtual = data;
    }

    public static DateTime getDateTimeAtual() {
        return dateTimeAtual == null ? new DateTime() : dateTimeAtual;
    }

    public static Date dateFromString(String dataString) {
        DateFormat formatter = new SimpleDateFormat(FORMATO_DATA);
        try {
            return formatter.parse(dataString);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToString(Date data) {
        return new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS).format(data);
    }

    public static String formatoMesAno(String data) {
        String mes = String.valueOf(data).substring(INDICE_4);
        String ano = String.valueOf(data).substring(0, INDICE_4);
        return mes + "/" + ano;
    }

    public static String converterDateParaDataBase(Date data) {
        return data == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_MES_ANO).format(data);
    }

    public static String dataFormatadaBacth() {
        return new SimpleDateFormat(FORMATO_DATA_BATCH).format(new Date());
    }

}