/*
 * Sistema SIGAS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.page.componentes.converters;

import java.util.HashMap;
import java.util.Map;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public abstract class AbstractMessageConverter {

    private static final int MAX_CONVERTED_COUNT = 5;

    private static final String OPEN_MARK = "{";
    private static final String NOT_FOUND_MARK = "$$$";

    private ErrorMessage error;

    private Map<String, Integer> convertedCount = new HashMap<String, Integer>();

    public AbstractMessageConverter(ErrorMessage error) {
        super();
        this.error = error;
    }

    private String convertTemplate(String template) {
        StringBuffer sb = new StringBuffer(template.length());

        int tail = 0;
        int paramStart = template.indexOf(OPEN_MARK);
        while (paramStart >= 0) {
            int paramEnd = template.indexOf('}', paramStart);
            if (paramEnd == -1) {
                break;
            }

            sb.append(template, tail, paramStart);
            tail = paramEnd + 1;

            String param = template.substring(paramStart, paramEnd + 1);
            String paramId = param.substring(OPEN_MARK.length(), param.length() - 1);
            if ("".equals(paramId)) {
                sb.append(param);
            } else {
                sb.append(convertParameter(paramId));
            }

            paramStart = template.indexOf(OPEN_MARK, paramEnd + 1);
        }

        sb.append(template.substring(tail));
        return sb.toString();
    }

    public String convert() {
        return convertTemplate(error.getMessage());
    }

    private String getNotFoundValue(String paramName) {
        return NOT_FOUND_MARK + paramName + NOT_FOUND_MARK;
    }

    private void checkAndIncrementConvertedCount(String name) {
        Integer count = convertedCount.get(name);
        if (count != null && count >= MAX_CONVERTED_COUNT) {
            throw new RuntimeException("Variável já foi avaliada antes (possível recursividade):" + name);
        }
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        convertedCount.put(name, count);
    }

    private String convertParameter(String name) {
        checkAndIncrementConvertedCount(name);

        String value;
        int colon = name.indexOf(':');
        if (colon >= 0) {
            String prefix = name.substring(0, colon);
            String nameWithoutPrefix = name.substring(colon + 1);
            value = convertPrefixedParameter(prefix, nameWithoutPrefix);
        } else {
            value = convertInlineParameter(name);
        }

        String converted;
        if (value == null) {
            converted = getNotFoundValue(name);
        } else {
            converted = convertTemplate(value);
        }
        return converted;
    }

    private String convertPrefixedParameter(String prefix, String name) {
        String converted = null;
        if ("key".equals(prefix)) {
            converted = convertKeyParameter(name);
        } else if ("label".equals(prefix)) {
            converted = convertLabelParameter(name);
        }
        return converted;
    }

    protected abstract String convertLabelParameter(String name);

    protected abstract String convertKeyParameter(String name);

    private String convertInlineParameter(String name) {
        Map<String, String> variables = error.getVariables();
        return variables.get(name);
    }

}
