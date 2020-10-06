package br.gov.bcb.sisaps.web.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.gov.bcb.sisaps.integracao.Cache;
import br.gov.bcb.sisaps.integracao.CacheChefiaPorLocalizacao;
import br.gov.bcb.sisaps.integracao.CacheFuncoesComissionadas;
import br.gov.bcb.sisaps.integracao.CacheServidores;
import br.gov.bcb.sisaps.integracao.CacheServidoresPorUnidade;
import br.gov.bcb.sisaps.integracao.CacheUnidades;

@SuppressWarnings(value = {"PMD"})
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	
        // Inicializa��o do cache de servidores do bacen (cria a inst�ncia por n� do cluster e inicia a carga)
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 20); // Espera a inicializa��o dos beans
                } catch (InterruptedException e) {
                    e.getMessage();
                }
                Cache.getInstancia(CacheUnidades.class).iniciar();
                Cache.getInstancia(CacheServidores.class).iniciar();
                Cache.getInstancia(CacheServidoresPorUnidade.class).iniciar();
                Cache.getInstancia(CacheChefiaPorLocalizacao.class).iniciar();
                Cache.getInstancia(CacheFuncoesComissionadas.class).iniciar();
            }
        }).start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //TODO n�o precisa implementar
    }

}
