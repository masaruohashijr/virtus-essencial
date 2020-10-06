/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração de emails para os diversos ambientes: desenv/homologa/fabrica/producao
 */
public class AmbienteEmail {
    private static final String AMBIENTE = "ambiente";

	private static final String PRODUCAO = "Producao";

	private static final Logger LOG = LoggerFactory.getLogger(AmbienteEmail.class.getName());

    private static final Properties PROPRIEDADES = new Properties();

    static {
        InputStream is = null;
        try {
            is = AmbienteEmail.class.getResourceAsStream("/apsEmail.properties");
            if (is != null) {
                PROPRIEDADES.load(is);
            }
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Erro ao abrir arquivo de propriedades:", e);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Erro a fechar arquivo de propriedades:", ex);
                }
            }
        }
    }

    public static boolean isProducao() {
        return getAmbiente().equals(PRODUCAO);
    }

    public static Properties propriedadesAmbiente() {
        return PROPRIEDADES;
    }
    
    public static String getAmbiente() {
        return (String) PROPRIEDADES.get(AMBIENTE);
    }
}
