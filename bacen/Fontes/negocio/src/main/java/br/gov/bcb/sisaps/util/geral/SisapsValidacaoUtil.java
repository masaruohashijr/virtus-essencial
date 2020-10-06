/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.util.Collection;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistente;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class SisapsValidacaoUtil extends SisapsValidacaoUtilHelper {

    private static final String PREENCHIMENTO_OBRIGATORIO = "\" � de preenchimento obrigat�rio.";

    public static boolean validarDataInvalida(String data, String nomeCampo, String formato,
            List<ErrorMessage> mensagens) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(formato);
        boolean isValido = true;
        if (!SisapsUtil.isNuloOuVazio(data)) {
            try {
                formatter.parseLocalDate(data).toDate();
            } catch (IllegalArgumentException e) {
                isValido = false;
            }
        }

        if (!isValido) {
            mensagens.add(new ErrorMessage("O valor do campo \'" + nomeCampo + "\' n�o � uma data v�lida."));
        }
        return isValido;
    }
    
    
    public static boolean validarHoraInvalida(String hora, String nomeCampo, String formato,
            List<ErrorMessage> mensagens) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(formato);
        boolean isValido = true;
        if (!SisapsUtil.isNuloOuVazio(hora)) {
            try {
                formatter.parseLocalDate(hora).toDate();
            } catch (IllegalArgumentException e) {
                isValido = false;
            }
        }

        if (!isValido) {
            mensagens.add(new ErrorMessage("O valor do campo \'" + nomeCampo + "\' n�o � uma hora v�lida."));
        }
        return isValido;
    }
    
    
    

    public static boolean validarObrigatoriedade(Object object, String nomeCampo, List<ErrorMessage> mensagens) {
        if (SisapsUtil.isNuloOuVazio(object)) {
            mensagens.add(new ErrorMessage("Campo \"" + nomeCampo + PREENCHIMENTO_OBRIGATORIO));
            return false;
        }
        return true;
    }

    public static ErrorMessage validarObrigatoriedadeAnalise(String nomeCampo) {
        return new ErrorMessage("A an�lise para o elemento \"" + nomeCampo + PREENCHIMENTO_OBRIGATORIO);
    }

    public static boolean validarObrigatoriedadeItemLista(Object object, String nomeCampo, List<ErrorMessage> mensagens) {
        if (SisapsUtil.isNuloOuVazio(object)) {
            mensagens
                    .add(new ErrorMessage(ConstantesMensagens.MSG_APS_RATING_SELECAO_ITEM_LISTA_OBRIGATORIO, nomeCampo));
            return false;
        }
        return true;
    }

    public static boolean validarCaracteresAlfanumericos(String texto, String nomeCampo, List<ErrorMessage> mensagens) {
        if (!SisapsUtil.isCaracteresAlfaNumericos(texto)) {
            mensagens.add(new ErrorMessage(ConstantesMensagens.MSG_APS_RATING_CAMPO_INVALIDO, nomeCampo));
            return false;
        }
        return true;
    }

    public static boolean contemItensValidos(Collection<? extends ObjetoPersistente> itens) {
        if (isNuloOuVazio(itens)) {
            return false;
        }
        String fieldName = "ibExcluido";
        for (ObjetoPersistente item : itens) {
            if (ReflexaoUtil.hasField(fieldName, item.getClass())
                    && SimNaoEnum.NAO.equals(ReflexaoUtil
                            .<ObjetoPersistente, SimNaoEnum> getValorCampo(item, fieldName))) {
                return true;
            }
        }
        return false;
    }
}
