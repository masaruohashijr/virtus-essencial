/*
 * Sistema APS
 * SigasUtil.java
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software È confidencial e propriedade do Banco Central do Brasil.
 * N„o È permitida sua distribuiÁ„o ou divulgaÁ„o do seu conte˙do sem
 * expressa autorizaÁ„o do Banco Central.
 * Este arquivo contÈm informaÁıes propriet·rias.
 */
package br.gov.bcb.sisaps.util.geral;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import br.gov.bcb.comum.excecoes.BCNegocioException;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ParametroGrauPreocupacao;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelSisAps;

public class SisapsUtil extends SisapsExcecaoUtil {

    private static final String REGEX_PONTO = "\\.";
    private static final int QUANTIDADE_256 = 256;
    private static char[] tabelaCaractere;
    private static boolean isExecucaoTeste;

    private static final String EXPRESSAO_REGULAR_CAMPO_ALFA_NUMERICO =
            "[(a-zA-Z·¡È…ÌÕÛ”˙⁄„√ı’‚¬Í ÓŒÙ‘˚€Á«¸‹_.)]+[(a-z|A-Z|0-9\\s·¡È…ÌÕÛ”˙⁄„√ı’‚¬Í ÓŒÙ‘˚€Á«¸‹_.)]*";

    static {
        tabelaCaractere = new char[QUANTIDADE_256];
        for (int i = 0; i < tabelaCaractere.length; ++i) {
            tabelaCaractere[i] = (char) i;
        }
        for (int i = 0; i < SisapsUtilSuporte.TEXTO_COM_ACENTOS_E_CEDILHA.length(); ++i) {
            tabelaCaractere[SisapsUtilSuporte.TEXTO_COM_ACENTOS_E_CEDILHA.charAt(i)] =
                    SisapsUtilSuporte.TEXTO_SEM_ACENTOS_E_CEDILHA.charAt(i);
        }
    }

    public static String obterNomeArquivo(String path) {
        return path.replaceAll(".+\\\\", StringUtils.EMPTY).replaceAll(".+\\/", StringUtils.EMPTY);
    }

    public static boolean isCaracteresAlfaNumericos(String texto) {
        boolean resultado = true;
        if (SisapsUtil.isNaoNuloOuVazio(texto) && !texto.matches(EXPRESSAO_REGULAR_CAMPO_ALFA_NUMERICO)) {
            resultado = false;
        }
        return resultado;
    }

    public static boolean nenhumNulo(Object... objetos) {
        boolean algumNulo = false;
        for (Object objeto : objetos) {
            if (objeto == null) {
                algumNulo = true;
                break;
            }
        }
        return !algumNulo;
    }

    public static boolean isVerdadeiro(Boolean... booleans) {
        for (Boolean value : booleans) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    public static String removeAcentos(String texto) {
        String retorno = texto;
        retorno = Normalizer.normalize(retorno, Normalizer.Form.NFD);
        retorno = retorno.replaceAll("[^\\p{ASCII}]", "");
        return retorno;
    }
    
    public static String removePontos(String texto) {
        return texto.replaceAll(REGEX_PONTO, Constantes.VAZIO);
    }

    public static String substituirEspacoBrancoPorUnderline(String texto) {
        return texto.replaceAll(Constantes.ESPACO_EM_BRANCO, Constantes.SUBLINHADO);
    }

    public static String criarMarkupId(String nomeUnico) {
        String id = nomeUnico;
        id = removeAcentos(id);
        id = removePontos(nomeUnico);
        id = substituirEspacoBrancoPorUnderline(id);
        return id;
    }
    
    public static List<ParametroGrauPreocupacao> removerParametroGrauPreocupacaoNA(List<ParametroGrauPreocupacao> lista) {
        List<ParametroGrauPreocupacao> retorno = new ArrayList<ParametroGrauPreocupacao>();
        retorno.addAll(lista);
        for (ParametroGrauPreocupacao nota : lista) {
            if (nota.getDescricao() == null) {
                retorno.remove(nota);
            }
        }
        return retorno;
    }

    public static List<ParametroNota> removerParametroNotaNA(List<ParametroNota> lista) {
        List<ParametroNota> retorno = new ArrayList<ParametroNota>();
        retorno.addAll(lista);
        for (ParametroNota nota : lista) {
            if (nota.getNaoAplicavel().equals(SimNaoEnum.SIM)) {
                retorno.remove(nota);
            }
        }
        return retorno;
    }
    
    public static List<ParametroNota> removerParametroNotaSemDescricao(List<ParametroNota> lista) {
        List<ParametroNota> retorno = new ArrayList<ParametroNota>();
        retorno.addAll(lista);
        for (ParametroNota nota : lista) {
            if (StringUtils.isBlank(nota.getDescricao())) {
                retorno.remove(nota);
            }
        }
        return retorno;
    }

    public static List<ParametroNotaAQT> removerParametroNotaNAANEF(List<ParametroNotaAQT> lista) {
        List<ParametroNotaAQT> retorno = new ArrayList<ParametroNotaAQT>();
        retorno.addAll(lista);
        for (ParametroNotaAQT nota : lista) {
            if (nota.getNotaNA().equals(SimNaoEnum.SIM)) {
                retorno.remove(nota);
            }
        }
        return retorno;
    }

    public static List<ParametroNota> removerParametroNotaInspetor(AvaliacaoRiscoControle avaliacao, Elemento elemento,
            List<ParametroNota> listaChoices) {
        List<ParametroNota> retorno = new ArrayList<ParametroNota>();
        retorno.addAll(listaChoices);
        retorno = removerParametroNotaNA(listaChoices);

        if (elemento != null 
                && (elemento.getParametroNotaSupervisor() == null 
                || (!elemento.getParametroNotaInspetor().equals(elemento.getParametroNotaSupervisor())))) {
            retorno.remove(elemento.getParametroNotaInspetor());
        }
        return retorno;
    }

    public static List<ParametroNota> removerParametroNotaInspetor(Elemento elemento, List<ParametroNota> listaChoices) {
        List<ParametroNota> retorno = new ArrayList<ParametroNota>();
        retorno.addAll(listaChoices);

        if (elemento != null
                && (elemento.getParametroNotaSupervisor() == null || (!elemento.getParametroNotaInspetor().equals(
                        elemento.getParametroNotaSupervisor())))) {
            retorno.remove(elemento.getParametroNotaInspetor());
        }
        return retorno;
    }

    public static List<ParametroNotaAQT> removerParametroNotaInspetorANEF(String valorElemento,
            List<ParametroNotaAQT> listaChoices) {
        List<ParametroNotaAQT> retorno = new ArrayList<ParametroNotaAQT>();
        retorno.addAll(listaChoices);

        retorno = removerParametroNotaNAANEF(listaChoices);

        for (ParametroNotaAQT paramentro : listaChoices) {
            if (valorElemento.equals(paramentro.getDescricaoValor())) {
                retorno.remove(paramentro);
            }
        }

        return retorno;
    }

    public static boolean isCicloMigrado(Ciclo ciclo) {
        if (ciclo == null || ciclo.getPk() == null) {
            return false;
        } else {
            return ciclo.getPk() < 0;
        }
    }

    public static String extrairExtensaoNomeArquivo(String nomeArquivo) {
        if (nomeArquivo != null) {
            String[] ext = nomeArquivo.split(REGEX_PONTO);
            return ext[ext.length - 1];
        }
        return null;
    }

    public static String extrairTextoCKEditorSemEspacosEmBranco(String texto) {
        if (texto == null) {
            return null;
        } else {
            return texto.replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("<i>", "").replaceAll("</i>", "")
                    .replaceAll("<strong>", "").replaceAll("</strong>", "").replaceAll("<b>", "")
                    .replaceAll("</b>", "").replaceAll("<u>", "").replaceAll("</u>", "").replaceAll("<em>", "")
                    .replaceAll("</em>", "").replaceAll("<ol>", "").replaceAll("</ol>", "").replaceAll("<li>", "")
                    .replaceAll("</li>", "").replaceAll("<ul>", "").replaceAll("</ul>", "")
                    .replaceAll("&nbsp;", Constantes.ESPACO_EM_BRANCO);

        }
    }

    public static boolean isTextoCKEditorBrancoOuNulo(String texto) {
        return StringUtils.isBlank(extrairTextoCKEditorSemEspacosEmBranco(texto));
    }

    public static String getNumeros(String texto) {
        if (StringUtils.isBlank(texto)) {
            return "";
        }
        return Pattern.compile("[^0-9]").matcher(texto).replaceAll("");
    }

    public static String getDataFormatadaComBarras(Date data) {
        if (data == null) {
            return "";
        }
        return new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS).format(data);
    }

    public static boolean isExecucaoTeste() {
        return isExecucaoTeste;
    }

    public static void setExecucaoTeste(boolean isExecucaoTeste) {
        SisapsUtil.isExecucaoTeste = isExecucaoTeste;
    }

    public static String getNomeOperadorDataHora(ObjetoPersistenteAuditavelSisAps<?> objeto) {
        return getNomeOperadorDataHora(objeto.getOperadorAtualizacao(), objeto.getUltimaAtualizacao());
    }

    public static String getNomeOperadorDataHora(String operador, DateTime data) {
        ServidorVO servidorVO = null;
        StringBuilder odh = null;
        if (operador != null && data != null) {
            try {
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(operador);
                odh = new StringBuilder(servidorVO == null ? "" : servidorVO.getNome());
                odh.append(" em ");
                odh.append(data.toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS));
                odh.append("h");
                return odh.toString();
            } catch (BCNegocioException e) {
                return "";
            }
        }
        return null;
    }
    
    public static String adicionar2CasasDecimais(String nota) {
        return nota == null ? "" : "".equals(nota) || nota.equals(Constantes.ASTERISCO_A) ? nota : nota + ",00";
    }
}
