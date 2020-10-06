/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraSalvarParametroEmailValidacao {

    private static final String PARAMETRO = "Parâmetro '";
    private static final String TABELACOMITE = "%tabelacomite%";
    private static final String HORARIO = "%horário%";
    private static final String HORARIO_CKEDITOR = "%hor&aacute;rio%";
    private static final String LOCAL = "%local%";
    private static final String LOCALIZACAO_CKEDITOR = "%localiza&ccedil;&atilde;o%";
    private static final String LOCALIZACAO = "%localização%";
    private static final String ES = "%es%";
    private static final String DATACOREC = "%datacorec%";
    private final List<String> parametrosValidos = 
            Arrays.asList(DATACOREC, ES, LOCALIZACAO_CKEDITOR, LOCALIZACAO, LOCAL, HORARIO_CKEDITOR, HORARIO, TABELACOMITE);
    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
    private final ParametroEmail parametroEmail;

    public RegraSalvarParametroEmailValidacao(ParametroEmail parametroEmail) {
        this.parametroEmail = parametroEmail;
    }

    public void validar() {
        validarCamposObrigatorios();
        validarRemetente();
        validarPrazo();
        validarParametrosInvalidos();
        validarParametrosTitulo();
        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarCamposObrigatorios() {
        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Remetente' é de preenchimento obrigatório."),
                StringUtils.isBlank(parametroEmail.getRemetente()));
        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Título' é de preenchimento obrigatório."),
                StringUtils.isBlank(parametroEmail.getTitulo()));
        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Corpo' é de preenchimento obrigatório."),
                StringUtils.isBlank(parametroEmail.getCorpo()));
        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Prazo' é de preenchimento obrigatório."),
                parametroEmail.getPrazo() == null && parametroEmail.getPrazoObrigatorio().booleanValue());
    }

    private void validarRemetente() {
        if (StringUtils.isNotBlank(parametroEmail.getRemetente())) {
            Pattern padrao = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,3}$"); 
            SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Remetente' inválido."), 
                    !padrao.matcher(parametroEmail.getRemetente()).find());
        }
    }
    
    private void validarPrazo() {
        if (parametroEmail.getPrazo() != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo 'Prazo' tem que ser maior que zero."), 
                    parametroEmail.getPrazo().compareTo(0) <= 0);
        }
    }
    
    private void validarParametrosInvalidos() {
        StringBuilder texto = new StringBuilder();
        texto.append(StringUtils.isBlank(parametroEmail.getTitulo()) ? "" : parametroEmail.getTitulo());
        texto.append(SisapsUtil.isTextoCKEditorBrancoOuNulo(parametroEmail.getCorpo()) ? 
                "" : SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(parametroEmail.getCorpo()));
        List<String> parametrosTextoTituloECorpo = obterTodosParametrosTexto(texto.toString());
        for (String parametroTexto : parametrosTextoTituloECorpo) {
            SisapsUtil.adicionarErro(erros, 
                    new ErrorMessage(PARAMETRO + parametroTexto + "' inexistente."), 
                    !parametrosValidos.contains(parametroTexto));
        }
    }
    
    private void validarParametrosTitulo() {
        if (StringUtils.isNotBlank(parametroEmail.getTitulo())) {
            SisapsUtil.adicionarErro(erros, 
                    new ErrorMessage(PARAMETRO + TABELACOMITE + "' não permitido no título."), 
                    parametroEmail.getTitulo().contains(TABELACOMITE));
        }
    }
    
    private List<String> obterTodosParametrosTexto(String texto) {
        Set<String> parametros = new HashSet<String>();
        Pattern pattern = Pattern.compile("%.*?%");
        Matcher matcher = pattern.matcher(texto);
        while (matcher.find()) {
            parametros.add(matcher.group());
        }
        ArrayList<String> retorno = new ArrayList<String>();
        retorno.addAll(parametros);
        return retorno;
    }
    
}