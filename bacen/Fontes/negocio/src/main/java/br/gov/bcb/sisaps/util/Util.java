/*
 * Sistema: auditar.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacões proprietarias.
 */
package br.gov.bcb.sisaps.util; // NOPMD existe complexidade na mesma por isso a exigencia de imports

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistente;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ComponenteOrganizacional;
import br.gov.bcb.comum.util.BCUtilException;
import br.gov.bcb.comum.util.checkdigits.Cpf;
import br.gov.bcb.comum.util.checkdigits.MatriculaFuncionario;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

//CHECKSTYLE:OFF classe utilitária, não existe razão para limitar a quantidade de métodos
public class Util {

    public static final String ROTULO_AUDIT = "AUDIT";
    public static final String OUTROS = "Outros";

    //CHECKSTYLE:ON
    protected static final String FORMATO_DATA_COM_BARRAS = "dd/MM/yyyy";
    protected static final String FORMATO_DATA_SEM_BARRAS = "ddMMyyyy";

    private static final Logger LOG = LoggerFactory.getLogger(Util.class.getName());
    private static final String FORMATO_DATA_HORA = "dd/MM/yyyy HH:mm:ss";
    private static final String PT = "pt";
    private static final String BR = "BR";
    private static final String SEPARADOR_MATRICULA_HIFEN = "-";
    private static final String SEPARADOR_PONTO = ".";
    private static final String STRING_VAZIA = "";
    private static final int POSICAO_STRING_0 = 0;
    private static final int POSICAO_STRING_1 = 1;
    private static final int POSICAO_STRING_4 = 4;
    private static final int POSICAO_STRING_7 = 7;
    private static final long UMKILOBYTE = 1024;
    private static final long UMMEGABYTE = UMKILOBYTE * 1024;
    private static final long UMGIGABYTE = UMMEGABYTE * 1024;
    private static final String STRKILOBYTE = "KB";
    private static final String STRMEGABYTE = "MB";
    private static final Locale BRASIL = new Locale(PT, BR);
    private static final NumberFormat NUMBER_FORMAT = criarNumberFormat();
    private static final int NUM_2 = 2;
    private static final String PARAGRAFO = "<p>";
    private static final String FECHAR_PARAGRAFO = "</p>";
    private static final String BR_P = "<br></p>";
    
    private static ThreadLocal<Boolean> contextoBatch = new ThreadLocal<Boolean>();
    
    private static ThreadLocal<Boolean> incluirBufferAnexos = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        };
    };

    /**
     * Locale para Brasil.
     */
    private static final Locale PT_BR = new Locale(PT, BR);

    private static final String PERCENT = "%";

    public static String prepararLike(String texto) {
        return PERCENT.concat(texto).concat(PERCENT);
    }

    public static String formatar(LocalDate data) {
        if (data == null) {
            return "";
        }
        return formatar(data, FORMATO_DATA_COM_BARRAS);
    }

    public static String formatarDataHora(DateTime data) {
        String valorRetorno = null;

        if (data != null) {
            valorRetorno = data.toString(FORMATO_DATA_HORA);
        }

        return valorRetorno;
    }

    public static String formatar(LocalDate valor, String pattern) {
        String data = null;

        if (valor != null && pattern != null) {
            data = valor.toString(pattern, PT_BR);
            return corrigirFormatacao(data);
        }

        return data;
    }
    
    private static String corrigirFormatacao(String data) {
        String novaData = null;

        // estranhamente, em produção, apenas janeiro fica com um espaço em
        // branco (talvez seja um problema da versão da VM usada)
        novaData = data.replaceAll("Jan /", "Jan/");

        // estranhamente a primeira letra não está ficando maiúscula em produção
        novaData = String.valueOf(novaData.charAt(0)).toUpperCase().concat(novaData.substring(1));
        novaData = novaData.toLowerCase(PT_BR);
        return novaData;
    }

    public static boolean isNumeroValido(String valor) {
        boolean isValido = true;

        if (valor == null || valor.isEmpty()) {
            return isValido;
        }

        try {
            Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            isValido = false;
        }

        return isValido;
    }

    public static Object getSpringBean(String nome) {
        return SpringUtils.get().getBean(nome);
    }

    public static boolean validarMatriculaServidor(String matricula) {
        try {
            new MatriculaFuncionario(matricula);
        } catch (BCUtilException e) {
            return false;
        }

        return true;

    }

    public static boolean validarCpf(String cpf) {
        try {
            new Cpf(cpf);
        } catch (BCUtilException e) {
            return false;
        }
        return true;
    }

    public static boolean validarMatriculaCpf(String valor) {
        return (validarMatriculaServidor(valor) || validarCpf(valor));
    }

    /**
     * Padroniza uma matrícula para gravação/consulta em banco de dados.
     * 
     * @param matricula Matrícula em formato "1.234.567-x".
     * @return Matrícula normalizada (formato "1234567X").
     */
    public static String normalizarMatriculaCpf(String matricula) {
        if (matricula == null) {
            return null;
        } else {
            return matricula.replaceAll("[-\\.,]", "").toUpperCase().trim();
        }
    }

    public static boolean isDataValida(LocalDate data) {
        String texto = data.toString(FORMATO_DATA_COM_BARRAS);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_DATA_COM_BARRAS);
        sdf.setLenient(false);
        try {
            sdf.parse(texto);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    public static String formatarValorMatriz(BigDecimal valor, String pattern) {
        DecimalFormat formatter = getDecimalFormat(pattern);
        return formatter.format(valor);
    }

    public static DecimalFormat getDecimalFormat(String pattern) {
        DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(new Locale(PT, BR));
        formatter.applyLocalizedPattern(pattern);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        return formatter;
    }

    public static int obterInt(Integer valor) {
        if (valor != null) {
            return valor.intValue();
        }
        return 0;
    }

    public static int obterInt(Object valor) {
        if (valor != null) {
            return Integer.parseInt(valor.toString());
        }
        return 0;
    }

    public static String obterStringNaoNula(Object valor) {
        if (valor != null) {
            return valor.toString();
        }
        return "";
    }

    /**
     * Obtém uma lista com as PKs dos objetos.
     * 
     * @param lista - A lista de objetos persistentes.
     * @return lista de PKs.
     */
    public static List<Integer> obterListaIds(List<? extends IObjetoPersistente<Integer>> lista) {
        List<Integer> ids = null;
        if (lista != null && !lista.isEmpty()) {
            ids = new ArrayList<Integer>();
            for (IObjetoPersistente<Integer> iObjetoPersistente : lista) {
                ids.add(iObjetoPersistente.getPk());
            }
        }
        return ids;
    }

    public static List<String> obterListaRotulosComponentes(List<ComponenteOrganizacional> lista) {
        List<String> codigos = null;
        if (lista != null && !lista.isEmpty()) {
            codigos = new ArrayList<String>();
            for (ComponenteOrganizacional comp : lista) {
                if (comp != null) {
                    codigos.add(comp.getRotulo());
                }
            }
        }
        return codigos;
    }

    public static String formatarMatricula(Object obj) {
        String matriculaFormatada = (String) obj;

        if (matriculaFormatada != null && !matriculaFormatada.equals(STRING_VAZIA) && matriculaFormatada.length() == 8) {
            // Primeiramente, normaliza a matrícula para poder formatar
            matriculaFormatada = normalizarMatriculaCpf((String) obj);

            matriculaFormatada =
                    matriculaFormatada.substring(POSICAO_STRING_0, POSICAO_STRING_1) + SEPARADOR_PONTO
                            + matriculaFormatada.substring(POSICAO_STRING_1, POSICAO_STRING_4) + SEPARADOR_PONTO
                            + matriculaFormatada.substring(POSICAO_STRING_4, POSICAO_STRING_7)
                            + SEPARADOR_MATRICULA_HIFEN + matriculaFormatada.substring(POSICAO_STRING_7);
        }

        return matriculaFormatada;
    }

    public static void copiarPropriedades(Object destino, Object origem) {
        try {
            BeanUtils.copyProperties(destino, origem);
        } catch (IllegalAccessException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Constantes.FALHA_COPY_PROPERTIES + e.getMessage(), e);
            }
            throw new NegocioException(Constantes.FALHA_COPY_PROPERTIES + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(Constantes.FALHA_COPY_PROPERTIES + e.getMessage(), e);
            }
            throw new NegocioException(Constantes.FALHA_COPY_PROPERTIES + e.getMessage(), e);
        }

    }

    public static boolean isNuloOuVazio(String str) {
        boolean retorno = false;
        if (str == null || str.trim().isEmpty()) {
            retorno = true;
        }
        return retorno;
    }

    public static boolean isNuloOuVazio(List<?> str) {
        boolean retorno = false;
        if (str == null || str.isEmpty()) {
            retorno = true;
        }
        return retorno;
    }

    /**
     * realiza a formatação do valor de acordo com a mascara enviada.
     * 
     * @param valor valor
     * @param mascara mascara que será ultilizada na formatação
     */
    public static String formatarMascara(String valor, String mascara) {

        StringBuffer dado = new StringBuffer(STRING_VAZIA);
        // remove caracteres nao numericos
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (Character.isDigit(c)) {
                dado.append(c);
            }
        }

        int indMascara = mascara.length();
        int indCampo = dado.length();

        while (indCampo > 0 && indMascara > 0) {
            if (mascara.charAt(--indMascara) == '#') {
                indCampo--;
            }
        }

        StringBuffer saida = new StringBuffer(STRING_VAZIA);
        for (; indMascara < mascara.length(); indMascara++) {
            saida.append((mascara.charAt(indMascara) == '#') ? dado.charAt(indCampo++) : mascara.charAt(indMascara));
        }
        return saida.toString();
    }

    public static boolean isUsuarioCorrente(String login) {
        String loginUsuarioCorrente = UsuarioCorrente.get().getLogin();
        if (loginUsuarioCorrente != null) {
            loginUsuarioCorrente = loginUsuarioCorrente.trim();
        }
        String tmp = login;
        if (tmp != null) {
            tmp = login.trim();
        }
        return loginUsuarioCorrente != null && loginUsuarioCorrente.equalsIgnoreCase(tmp);

    }

    public static String obterLoginMinusculo(String login) {
        if (login == null) {
            return null;
        }
        // deinf.thiagol: NUNCA COLOCAR TRIM, pois isso gera problema gigantescos em DB2.
        return login.toLowerCase();

    }

    public static String obterLoginAtual() {
        String loginUsuarioCorrente = UsuarioCorrente.get().getLogin();
        return obterLoginMinusculo(loginUsuarioCorrente);
    }

    public static String obterRotuloUnidadeUsuarioLogado() {
        return obterRotuloUnidadeUsuario(Util.obterLoginAtual());
    }

    public static String obterRotuloUnidadeUsuario(String login) {
        if (login == null) {
            return "";
        }
        return login.substring(0, 5).toUpperCase();
    }

    /**
     * Retorna o período formatado.
     * 
     * @param dataInicio dataInicio.
     * @param dataFim dataFim.
     * @return período no padrão dd/MM/yyyy à dd/MM/yyyy
     */
    @Transient
    public static String obterPeriodoFormatado(LocalDate dataInicio, LocalDate dataFim) {
        String formatoData = FORMATO_DATA_COM_BARRAS;
        StringBuilder strBuilder = new StringBuilder();
        String semData = "";
        if (dataInicio != null || dataFim != null) {
            if (dataInicio == null && dataFim != null) {
                semData = "Até ";
                strBuilder.append(semData);
                strBuilder.append(dataFim.toString(formatoData).trim());
            } else if (dataInicio != null && dataFim == null) {
                semData = "A partir de ";
                strBuilder.append(semData);
                strBuilder.append(dataInicio.toString(formatoData).trim());
            } else if (dataInicio != null && dataFim != null) {
                strBuilder.append(dataInicio.toString(formatoData).trim());
                strBuilder.append(" a ");
                strBuilder.append(dataFim.toString(formatoData).trim());
            }
        }

        return strBuilder.toString();
    }

    /***
     * Obter
     * 
     * @author deinf.weslen
     * @param dataFim
     * @param dataAtual
     * @return
     */
    public static int obterQuantidadeDias(LocalDate dataFim, LocalDate dataAtual) {
        return Days.daysBetween(dataFim, dataAtual).getDays();
    }

    /***
     * Converter Data String para LocalDate Obs. Se a data for inválida trate a exceção utilizando
     * IllegalFieldValueException
     * 
     * @param date
     * @return
     * @author deinf.weslen
     */
    public static LocalDate converterParaLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(Constantes.FORMATO_DATA_COM_BARRAS);
        DateTime dateTime = formatter.parseDateTime(date);
        return new LocalDate(dateTime);
    }

    public static boolean isUsuarioAudit() {
        return obterRotuloUnidadeUsuarioLogado().equalsIgnoreCase(ROTULO_AUDIT);
    }

    /**
     * - Remove os caracteres inválidos; Transforma dois ou mais espaços em branco em um só;
     * Transforma dois ou mais pontos em um só;
     */
    public static String normalizarAnexo(String nome) {
        String nomeNormalizado = null;
        if (nome != null) {
            String regexCaracteresInvalidos = "[^\\x20-\\x7E|^\\xA0-\\xFF]";
            String regexVariosPontos = "[\\x2e\\x2e]+";
            String regexVariosEspacos = "[\\x20\\x20|\\xa0\\xa0]+";

            nomeNormalizado =
                    nome.replaceAll(regexCaracteresInvalidos, "").replaceAll(regexVariosPontos, SEPARADOR_PONTO)
                            .replaceAll(regexVariosEspacos, " ");
        }

        return nomeNormalizado;
    }

    public static boolean isMatriculasIguais(String mat1, String mat2) {
        String mat1Normalizada = normalizarMatriculaCpf(mat1);
        String mat2Normalizada = normalizarMatriculaCpf(mat2);
        return mat1Normalizada != null && mat1Normalizada.equals(mat2Normalizada);
    }

    public static boolean isDataValidaNaoNula(LocalDate data) {
        if (data == null) {
            return false;
        }
        return isDataValida(data);
    }

    public static boolean validarFormatoData(String data) {
        return data != null && data.length() == Constantes.PERCENTUAL_10;
    }

    public static boolean saoIguais(Object str1, Object str2) {
        if (str1 == null) {
            if (str2 != null) {
                return false;
            }
        } else if (!str1.equals(str2)) {
            return false;
        }
        return true;
    }

    public static String obterTamanhoArquivoFormatado(Long tamanho) {
        if (tamanho == null) {
            return "";
        } else {
            String formatado = "";

            if (tamanho.longValue() < UMKILOBYTE) {
                formatado = "1,00" + STRING_VAZIA + STRKILOBYTE;
            } else if (tamanho.longValue() < UMMEGABYTE) {
                formatado = formatInKiloBytes(tamanho);
            } else if (tamanho.longValue() < UMGIGABYTE) {
                formatado = formatInMegaBytes(tamanho);

            }

            return formatado;

        }
    }

    /**
     * Formata uma quantidade de bytes em KiloBytes.
     * 
     * @param Quantidade de bytes
     * @return Número formatado.
     */
    public static String formatInKiloBytes(Long bytes) {
        return NUMBER_FORMAT.format(bytes.doubleValue() / UMKILOBYTE) + STRING_VAZIA + STRKILOBYTE;
    }

    private static NumberFormat criarNumberFormat() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(BRASIL);
        numberFormat.setMaximumFractionDigits(NUM_2);
        numberFormat.setMinimumFractionDigits(NUM_2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        return numberFormat;
    }

    /**
     * Formata uma quantidade de bytes em MegaBytes.
     * 
     * @param Quantidade de bytes
     * @return Número formatado.
     */
    public static String formatInMegaBytes(Long bytes) {
        return NUMBER_FORMAT.format(bytes.doubleValue() / UMMEGABYTE) + STRING_VAZIA + STRMEGABYTE;
    }

    public static boolean isDataMaiorOuIgual(LocalDate data, LocalDate dataRefencia) {
        if (data == null || dataRefencia == null) {
            return false;
        } else {
            return data.isAfter(dataRefencia) || data.isEqual(dataRefencia);
        }
    }

    public static boolean isDataMenorOuIgual(LocalDate data, LocalDate dataRefencia) {
        if (data == null || dataRefencia == null) {
            return false;
        } else {
            return data.isBefore(dataRefencia) || data.isEqual(dataRefencia);
        }
    }

    public static boolean ehDiferente(Object str1, Object str2) {
        return str1 != null && !str1.equals(str2);
    }

    public static boolean ehDiferente(String str1, String str2) {
        return str1 != null && !str1.equals(str2);
    }

    public static String getCssStatusRecomendacaoUnidade(String situacao) {
        String css = "";
        if (situacao != null) {
            if (situacao.equals(Constantes.RECOMENDACAO_CONCLUIDA)) {
                css = Constantes.CSS_BLUESKY;
            } else if (situacao.equals(Constantes.RECOMENDACAO_ATRASADA)) {
                css = Constantes.CSS_VERMELHO;
            } else if (situacao.equals(Constantes.RECOMENDACAO_DENTRO_PREVISTO)) {
                css = Constantes.CSS_LIGHTGREEN;
            } else if (situacao.equals(Constantes.RECOMENDACAO_PROXIMA_DATA_LIMITE)) {
                css = Constantes.CSS_YELLOW2;
            }
        }
        return css;
    }

    public static String obterVazioSeNulo(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    public static boolean isPrazoAnteriorADataAtual(LocalDate prazo) {
        return prazo != null && (new LocalDate()).isAfter(prazo);
    }

    public static boolean isCondicaoDentroDoPrevisto(LocalDate prazo, String parametro, int quantidade) {
        return isPrazoPosteriorADataAtual(prazo) && (quantidade > Integer.valueOf(parametro));
    }

    public static boolean isCondicaoProximoADataLimite(LocalDate prazo, String parametro, int quantidade) {
        return prazo != null && Util.isDataMenorOuIgual(new LocalDate(), prazo)
                && quantidade <= Integer.valueOf(parametro);
    }

    public static boolean isPrazoPosteriorADataAtual(LocalDate prazo) {
        return prazo != null && (new LocalDate()).isBefore(prazo);
    }

    public static String formatarNumeroDemanda(Object value) {
        String valorRetorno = null;
        if (value != null && value.toString().length() > 4) {
            valorRetorno = formatarMascara(atualizarNumeroDemanda(value), Constantes.FORMATO_VOTO_BCB);
        } else if (value != null) {
            valorRetorno = value.toString();
        }
        return valorRetorno;
    }

    public static String atualizarNumeroDemanda(Object value) {
        String aux = "";
        if (value != null) {
            if (value.toString().length() == 5) {
                aux = obterString(value, Constantes.PERCENTUAL_7, Constantes.NUMERO_1);
            } else if (value.toString().length() == 6) {
                aux = obterString(value, Constantes.PERCENTUAL_6, Constantes.NUMERO_2);
            } else if (value.toString().length() == 7) {
                aux = obterString(value, Constantes.PERCENTUAL_5, Constantes.NUMERO_3);
            } else {
                aux =
                        value.toString().substring(Constantes.NUMERO_4)
                                + value.toString().substring(0, Constantes.NUMERO_4);
            }
        }
        return aux;
    }

    private static String obterString(Object value, int constante, int numero) {
        return StringUtil.completeRight(value.toString().substring(numero), constante, '0')
                + value.toString().substring(0, numero);
    }

    public static String nomeOperador(String login) {
        ServidorVO servidorVO;
        try {
            if (StringUtil.isVazioOuNulo(login)) {
                return Constantes.VAZIO;
            } else {
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(login);

            }
        } catch (BCConsultaInvalidaException e) {
            throw new RuntimeException(e);
        }

        return servidorVO == null ? "" : servidorVO.getNome();
    }

    public static Boolean isBatch() {
        return contextoBatch.get();
    }

    public static void setBatch(Boolean isBatch) {
        contextoBatch.set(isBatch);
    }
    
    public static Boolean isIncluirBufferAnexos() {
        return incluirBufferAnexos.get();
    }
    
    public static void setIncluirBufferAnexos(Boolean isIncluirBufferAnexos) {
        incluirBufferAnexos.set(isIncluirBufferAnexos);
    }

    public static void resetLocalThreadIncluirBufferAnexos() {
        incluirBufferAnexos.remove();
    }

    public static String normalizaTexto(String texto) {
        String textoFormatado;
        if (Util.isNuloOuVazio(texto)) {
            return "";
        }

        String espaco =
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

        textoFormatado = texto.replaceAll(PARAGRAFO, PARAGRAFO + espaco);
        textoFormatado = textoFormatado.replaceAll(FECHAR_PARAGRAFO, BR_P);

        if (textoFormatado.endsWith(BR_P)) {
            int index = textoFormatado.lastIndexOf(BR_P);
            textoFormatado = textoFormatado.substring(0, index) + FECHAR_PARAGRAFO;
        }

        return textoFormatado;

    }

    public static String formatarData(DateTime data) {
        return data == null ? "" : data.toString(Constantes.FORMATO_DATA_COM_BARRAS);
    }
    
    public static String normalize(String text) {
        return filtrar(text, " \t\r\n");
    }
    
    private static String filtrar(String text, String filtro) {
        StringTokenizer st = new StringTokenizer(text, filtro);
        StringBuffer str = new StringBuffer(st.nextToken());
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            str.append(s);
        }
        return UtilFormatarEmail.removerCaracteresInvalidosXml(clean(str));
    }
    
    public static String clean(StringBuffer str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    public static Calendar calendario(long milisegundos) {
        Calendar calendario = Calendar.getInstance(); 
        calendario.setTimeInMillis(milisegundos);
        return calendario;
    }   
    
    public static DateTime ultimaHora(Date data) {
        return ultimaHora(data, false);
    }

    public static DateTime ultimaHora(Date data, boolean isEmail) {
        Calendar c = calendario(data.getTime());
        if (!SisapsUtil.isExecucaoTeste()) {
            if (isEmail) {
                c.add(Calendar.HOUR, +3);
            } else {
                c.add(Calendar.HOUR, -3);
            }
        }
        return new DateTime(c.getTime());
    }
  
}
