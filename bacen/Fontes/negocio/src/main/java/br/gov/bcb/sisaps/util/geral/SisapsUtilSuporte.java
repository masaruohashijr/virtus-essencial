/*
 * Sistema APS
 * SigasUtilSuporte.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.comum.util.string.format.MaskFormatter;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class SisapsUtilSuporte {

    public static final String BARRA = "/";
    public static final String PONTO = ".";
    public static final String VIRGULA = ",";
    public static final String VAZIO = "";
    public static final String ESPACO_EM_BRANCO = " ";
    public static final String TEXTO_COM_ACENTOS_E_CEDILHA = "����������������������������������������������";
    public static final String TEXTO_SEM_ACENTOS_E_CEDILHA = "AaEeIiOoUuAaEeIiOoUuAaEeIiOoUuAaEeIiOoUuAaOoCc";
    public static final String PONTO_DELIM_NUMEROS = PONTO;
    public static final Locale PT_BR = new Locale("pt", "BR");
    public static final String TRACO = "-";
    public static final String RAWTYPES = "rawtypes";

    public static String formatarString(String string, String mascara) {
        String resultado = string;
        if (StringUtils.isNotBlank(string)) {
            try {
                MaskFormatter formatter = new MaskFormatter(mascara);
                resultado = formatter.format(string.trim());
            } catch (IllegalArgumentException e) {
                /* ignorar formata��o caso n�o consiga formatar */
                resultado = string;
            }
        }
        return resultado;
    }

    public static String removerFormatacaoMatricula(String matricula) {
        return matricula.replaceAll("[.-]", VAZIO);
    }

    public static void validarObrigatoriedade(String valor, String nomeCampo, List<ErrorMessage> mensagens) {
        if (StringUtils.isBlank(valor)) {
            mensagens.add(new ErrorMessage("Campo \"" + nomeCampo + "\" � de preenchimento obrigat�rio."));
        }
    }

}
