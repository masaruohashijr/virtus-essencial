/*
 * Sistema: br.gov.bcb.creditos.web
 * MascaraDeDataBehaviour.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */

package br.gov.bcb.sisaps.web.page.componentes.mascara;

/**
 * Classe que implementa o behavior espec�fico para a m�scara de formato de data dd/mm/aaaa.
 */

@SuppressWarnings("serial")
public class MascaraDeDataBehaviour extends AbstractMascaraBehavior {

    private static final int TAMANHO = 10;

    /**
     * Construtor da classe que implementa a chamada ao javascript com evento e par�metros.
     */
    public MascaraDeDataBehaviour() {
        setMascara(PADRAO_DATA, TAMANHO, false);
    }

}