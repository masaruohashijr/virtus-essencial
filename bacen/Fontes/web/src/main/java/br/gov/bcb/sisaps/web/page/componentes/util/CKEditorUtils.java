package br.gov.bcb.sisaps.web.page.componentes.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.DefaultPage;

/**
 * Classe utilitária para métodos de "criação" de JS para a utilização do CKEditor
 */
public class CKEditorUtils {

    private static final String BARRA_N = "\n";
    private static final String FECHA_PARENTESES_PONTO_VIRGULA = ");";
    private static final String ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES = "', '";
    private static final String ASPAS_SIMPLES_VIRGULA = "', ";

    public static String jsAtualizarAlerta(String idAlerta, boolean isVisible) {
        String retorno = "";
        if (!DefaultPage.isUsarHtmlUnit()) {
            retorno = " atualizarAlerta('" + idAlerta + ASPAS_SIMPLES_VIRGULA + isVisible + "); ";
        }
        return retorno;
    }

    public static String jsAtualizarAlertaPrincipal(String idAlertaPrincipal, List<String> idsAlertasAssociados) {
        String retorno = "";
        if (!DefaultPage.isUsarHtmlUnit()) {
            retorno =
                    " atualizarAlertaPrincipal('" + idAlertaPrincipal + ASPAS_SIMPLES_VIRGULA
                            + getListaIdsAlertas(idsAlertasAssociados) + FECHA_PARENTESES_PONTO_VIRGULA;
        }
        return retorno;
    }

    public static String jsVisibilidadeAlertaPrincipal(String idAlertaPrincipal, String idAlertaAAtualizar,
            List<String> idsAlertasAssociados) {
        StringBuilder jsRetorno = new StringBuilder("");
        if (!DefaultPage.isUsarHtmlUnit()) {
            if (StringUtils.isNotBlank(idAlertaAAtualizar)) {
                jsRetorno.append(CKEditorUtils.jsAtualizarAlerta(idAlertaAAtualizar, false));
            }
            jsRetorno.append(CKEditorUtils.jsAtualizarAlertaPrincipal(idAlertaPrincipal, idsAlertasAssociados));
        }
        return jsRetorno.toString();
    }

    public static String jsVisibilidadeAlerta(String idAlerta, boolean visible) {
        StringBuilder jsRetorno = new StringBuilder("");
        if (!DefaultPage.isUsarHtmlUnit()) {
            if (StringUtils.isNotBlank(idAlerta)) {
                jsRetorno.append(CKEditorUtils.jsAtualizarAlerta(idAlerta, visible));
            }
        }
        return jsRetorno.toString();
    }

    public static String jsAtualizarBotoesVoltar(String idBotaoVoltar, String idBotaoVoltarSemSalvar,
            boolean mostrarBotaoVoltarSemSalvar) {
        String retorno = "";
        if (!DefaultPage.isUsarHtmlUnit()) {
            retorno =
                    " atualizarBotoesVoltar('" + idBotaoVoltar + ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES
                            + idBotaoVoltarSemSalvar + ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES
                            + mostrarBotaoVoltarSemSalvar + "');";
        }
        return retorno;
    }

    public static String jsAtualizarBotoesVoltar(String idBotaoVoltar, String idBotaoVoltarSemSalvar,
            List<String> idsAlertasAssociados) {
        String retorno = "";
        if (!DefaultPage.isUsarHtmlUnit()) {
            retorno =
                    " atualizarBotoesAlerta('" + idBotaoVoltar + ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES
                            + idBotaoVoltarSemSalvar + ASPAS_SIMPLES_VIRGULA + getListaIdsAlertas(idsAlertasAssociados)
                            + FECHA_PARENTESES_PONTO_VIRGULA;
        }
        return retorno;
    }

    public static String jsVisibilidadeBotaoVoltarPrincipal(String idBotaoVoltar, String idBotaoVoltarSemSalvar,
            String idAlertaAAtualizar, List<String> idsAlertasAssociados) {
        StringBuilder jsRetorno = new StringBuilder("");
        if (!DefaultPage.isUsarHtmlUnit()) {
            if (StringUtils.isNotBlank(idAlertaAAtualizar)) {
                jsRetorno.append(CKEditorUtils.jsAtualizarAlerta(idAlertaAAtualizar, false));
            }
            jsRetorno.append(CKEditorUtils.jsAtualizarBotoesVoltar(idBotaoVoltar, idBotaoVoltarSemSalvar,
                    idsAlertasAssociados));
        }
        return jsRetorno.toString();
    }

    private static String getListaIdsAlertas(List<String> idsAlertasAssociados) {
        StringBuilder listaIds = new StringBuilder();
        listaIds.append("[");
        for (String idAlerta : idsAlertasAssociados) {
            listaIds.append(Constantes.ASPAS_SIMPLES);
            listaIds.append(idAlerta);
            listaIds.append(Constantes.ASPAS_SIMPLES);
            listaIds.append(",");
        }
        listaIds.replace(listaIds.length() - 1, listaIds.length(), "]");
        return listaIds.toString();
    }

    public static String setarFocus(String idComponente) {

        StringBuilder b = new StringBuilder();
        b.append("document.onload = focusPainel();  ");
        b.append(BARRA_N);
        b.append("function focusPainel(){");
        b.append(BARRA_N);
        b.append("window.location.href = '#").append(idComponente).append("';");
        b.append("}");
        return b.toString();
    }

    public static String jsAtualizarBotoesSalvarInformacoes(String idBotaoSalvar, String idBotaoSalvarDesabilitado,
            boolean mostrarBotaoSalvar) {
        String retorno = "";

        if (!DefaultPage.isUsarHtmlUnit()) {
            retorno =
                    " atualizarBotoesSalvarInformacoes('" + idBotaoSalvar + ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES
                            + idBotaoSalvarDesabilitado + ASPAS_SIMPLES_VIRGULA_ASPAS_SIMPLES + mostrarBotaoSalvar
                            + "');";
        }

        return retorno;
    }
}
