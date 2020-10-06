/*
 * Sistema: br.gov.bcb.creditos.web
 * MascaraDeDataBehaviour.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.web.page.componentes.mascara;

/**
 * Classe que implementa o behavior específico para a máscara de formato de data dd/mm/aaaa.
 */

@SuppressWarnings("serial")
public class MascaraDeDataBehaviour extends AbstractMascaraBehavior {

    private static final int TAMANHO = 10;

    /**
     * Construtor da classe que implementa a chamada ao javascript com evento e parâmetros.
     */
    public MascaraDeDataBehaviour() {
        setMascara(PADRAO_DATA, TAMANHO, false);
    }

}