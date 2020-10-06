/*
 * Sistema: br.gov.bcb.creditos.web
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */

package br.gov.bcb.sisaps.web.page.componentes.mascara;

import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;

/**
 * Behavior de m�scaras.
 */
@SuppressWarnings("serial")
public class AbstractMascaraBehavior extends AbstractAjaxBehavior {

    public static final String PADRAO_VALOR_MONETARIO_JUROS = "#.###,####";
    public static final String PADRAO_VALOR_MONETARIO_QUANTIDADE = "#.###.###.###.###";
    public static final String PADRAO_VALOR_MONETARIO = "#.###.###.###.###,##";
    public static final String PADRAO_CEP = "#####-###";
    public static final String PADRAO_DATA = "##/##/####";
    public static final String PADRAO_CNPJ_BASE = "##.###.###";
    public static final String PADRAO_CPF = "###.###.###-##";
    public static final String PADRAO_CNPJ_COMPLETO = "##.###.###/####-##";
    public static final String PADRAO_TELEFONE = "#####-####";
    public static final String PADRAO_DDD = "##";
    public static final String PADRAO_0800 = "####-######";
    public static final String PADRAO_MATRICULA = "#.###.###-#";
    public static final String PADRAO_HORA = "##:##";

    /** Eventos javascript que a m�scara ser� chamada. */
    private static String[] eventos = new String[] {/*"onfocus", */"onblur", "onkeydown", "onkeypress", "onkeyup"};

    private String valor;

    /**
     * Retorna a chamada � fun��o JS de m�scaras.
     * 
     * @return valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * Atribui a chamada � fun��o JS de m�scaras.
     * 
     * @param valor valor
     */
    public final void setValor(String valor) {
        this.valor = valor;
    }

    public AbstractMascaraBehavior setMascara(String mascara, int tamanho, boolean permiteX) {
        setValor("mascara(event, '" + mascara + "', this, " + tamanho + ", " + permiteX + ");");
        return this;
    }

    @Override
    public void onRequest() {
        //TODO n�o precisa implementar
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        for (String evento : eventos) {
            tag.getAttributes().put(evento, getValor());
        }
    }

}
