/*
 * Sistema: sisaps.
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package app;

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.test.stuff.web.app.DefaultJettyConfigurationBuilder;

/**
 * Executor do Jetty standalone.
 */
public class JettyMain {

    public static void main(String[] args) {
        //Troque por um usuario de desenvolvimento com permissao para acessar o BcPessoa via Rest de acordo com o bcrest.properties
        //System.setProperty("br.bcb.rest.user", "<usuario>:<senha>");

        CustomServerBcNet customJettyServer = new CustomServerBcNet();
        customJettyServer.executar(new DefaultJettyConfigurationBuilder().build(Constantes.ENDERECO_SISTEMA));
    }
}
