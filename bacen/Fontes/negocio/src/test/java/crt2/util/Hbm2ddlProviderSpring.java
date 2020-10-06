package crt2.util;

import crt2.AbstractTestesNegocio;


public class Hbm2ddlProviderSpring {

    public static final String getValue() {
        if (AbstractTestesNegocio.ehWindows) {
            return "validate";
        }
        return "update";
    }
}