package br.gov.bcb.sisaps.util;

public class AmbienteSO {
    public static boolean ehWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}
