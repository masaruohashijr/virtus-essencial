package crt2.util;

import java.io.File;
import java.io.FileFilter;

public class ConversorUtfIsoDir {

    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: ConversorUtfIsoDir <diretorio> <padrao: endsWith>"); // NOPMD
            return;
        }
        File[] arquivos = new File(args[0]).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(args[1]);
            }
        });
        for (File file : arquivos) {
            ConversorUtfIso.main(new String[] {file.getAbsolutePath(), file.getAbsolutePath()});
        }
    }

}
