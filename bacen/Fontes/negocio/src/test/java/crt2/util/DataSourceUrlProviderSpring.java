package crt2.util;

import crt2.AbstractTestesNegocio;

public class DataSourceUrlProviderSpring {
    public static final String getDriver() {
        return "org.hsqldb.jdbcDriver";
    }

    public static final String getUrl() {
        if (AbstractTestesNegocio.ehWindows) {
            return "jdbc:hsqldb:hsql://localhost/sisaps";
        }
        return "jdbc:hsqldb:mem:sisaps";
    }
}