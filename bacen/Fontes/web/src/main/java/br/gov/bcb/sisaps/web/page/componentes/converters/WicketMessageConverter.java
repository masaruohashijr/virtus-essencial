/*
 * Sistema SIGAS..
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.web.page.componentes.converters;

import org.apache.wicket.MarkupContainer;

import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class WicketMessageConverter extends AbstractMessageConverter {

    private MarkupContainer container;

    public WicketMessageConverter(MarkupContainer container, ErrorMessage error) {
        super(error);
        error.getVariables().put("nomeSistema", "SISAPS");
        this.container = container;
    }

    private String getSimpleEntityAndProperty(String property) {
        int lastDot = property.lastIndexOf('.');
        if (lastDot < 0) {
            return null;
        }
        int previousDot = property.lastIndexOf('.', lastDot - 1);
        if (previousDot >= 0) {
            return property.substring(previousDot + 1);
        }
        return property;
    }

    private String getContainerString(String key) {
        String value = container.getString(key, null, "");
        if ("".equals(value)) {
            return null;
        }
        return value;
    }

    @Override
    protected String convertKeyParameter(String name) {
        String value = getContainerString(name);
        if (value != null) {
            return value;
        }

        String simple = getSimpleEntityAndProperty(name);
        if (simple != null) {
            value = getContainerString(simple);
        }
        return value;
    }

    @Override
    protected String convertLabelParameter(String name) {
        String value = convertKeyParameter(name);
        if (value == null) {
            int index = name.lastIndexOf('.');
            if (index >= 0) {
                value = name.substring(index + 1);
            } else {
                value = name;
            }
        }
        return value;
    }

}
