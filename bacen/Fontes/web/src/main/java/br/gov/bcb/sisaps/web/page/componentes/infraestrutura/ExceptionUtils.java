/*
 * Sistema SIGAS
 * ExceptionUtils.java
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.page.componentes.infraestrutura;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.MissingResourceException;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.converters.WicketMessageConverter;

public final class ExceptionUtils {

    private static final String O_VALOR_DO_CAMPO = "O valor do campo \"";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
        // Este metodo devera ser sobescrito
    }

    public static <T extends MarkupContainer> void tratarNegocioException(NegocioException e, MarkupContainer pagina) {
        String texto;

        if (e.getMensagens().isEmpty()) {
            pagina.error(obterMensagem(e.getMessage(), pagina));
        } else {
            for (ErrorMessage mensagem : e.getMensagens()) {
                texto = new WicketMessageConverter(pagina, mensagem).convert();

                if (mensagem.getVariables().isEmpty()) {
                    pagina.error(obterMensagem(texto, pagina));
                } else {
                    pagina.error(texto);
                }
            }
        }
    }

    public static <T extends MarkupContainer> void tratarErroConcorrencia(Exception e, MarkupContainer componente) {
        adicionarMensagemErroConcorrencia(componente);

        if (e != null) {
            LOGGER.error(e.getMessage());
        }
    }

    public static <T extends MarkupContainer> void tratarErroConstraint(ConstraintViolationException e,
            MarkupContainer componente) {
        adicionarMensagemErroConstraint(componente, e);

        if (e != null) {
        	System.out.println(">>> ########### >>> - Erro do constraint no APS");
        	e.printStackTrace(System.out);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static <T extends MarkupContainer> void adicionarMensagemErroConcorrencia(MarkupContainer componente) {
        componente.error(obterMensagem(ConstantesMensagens.MSG_ERRO_CONCORRENCIA, componente));
    }

    public static <T extends MarkupContainer> void adicionarMensagemErroConstraint(MarkupContainer componente, Exception e) {
    	StringWriter sw = new StringWriter();
    	e.printStackTrace(new PrintWriter(sw));
        componente.error(obterMensagem(ConstantesMensagens.MSG_ERRO_CONSTRAINT, componente) + " - " + e.getMessage() + " <-> " + sw.toString());
    }

    public static <T extends MarkupContainer> void tratarInvalidStateException(InvalidStateException e,
            MarkupContainer componente) {
        InvalidValue[] invalidValues = e.getInvalidValues();

        for (InvalidValue invalidValue : invalidValues) {
            String message = invalidValue.getMessage();
            componente.error(message);
        }
    }

    public static <T extends MarkupContainer> void tratarIllegalArgumentExceptio(IllegalArgumentException e,
            MarkupContainer componente, String nomeCampo) {

        String message = O_VALOR_DO_CAMPO + nomeCampo + "\" não é uma data válida.";
        e.getMessage();
        componente.error(message);
    }
    
    public static <T extends MarkupContainer> void tratarIllegalArgumentExceptioHora(IllegalArgumentException e,
            MarkupContainer componente, String nomeCampo) {

        String message = O_VALOR_DO_CAMPO + nomeCampo + "\" não é uma hora válida.";
        e.getMessage();
        componente.error(message);

    }

    private static <T extends MarkupContainer> String obterMensagem(String chave, T componente) {
        String mensagem;

        try {
            mensagem = new StringResourceModel(chave, componente, null).getString();
        } catch (MissingResourceException e) {
            mensagem = chave;
        }
        return mensagem;
    }

}