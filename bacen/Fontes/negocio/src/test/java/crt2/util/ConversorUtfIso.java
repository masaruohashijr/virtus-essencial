package crt2.util;

public class ConversorUtfIso {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: ConversorUtfIso <arquivo_entrada> <arquivo_saida>"); // NOPMD
            return;
        }
        Conversor.main(new String[] {args[0], "UTF-8", args[1], "ISO-8859-1"});
    }
}