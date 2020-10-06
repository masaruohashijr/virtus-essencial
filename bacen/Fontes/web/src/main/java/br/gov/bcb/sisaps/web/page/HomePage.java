/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page;

/**
 * Sistema APS-SRC.
 * 
 */
public class HomePage extends DefaultPage {

    private static final long serialVersionUID = -1419177353690891443L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Início";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0000";
    }
}
