/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page;

import org.apache.wicket.markup.html.form.Button;

public class DefaultPageMenu extends DefaultPage {

    public class BotaoVoltar extends Button {
        public BotaoVoltar() {
            super("btnVoltar");
            setMarkupId(getId());
        }

        @Override
        public void onSubmit() {
            voltar();
        }
    }

}