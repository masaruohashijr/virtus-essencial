package crt2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

//CHECKSTYLE:OFF
public class Conversor {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("usage: Conversor <arquivo_entrada> <encoding> <arquivo_saida> <encoding>"); // NOPMD
            return;
        }
        String fileIn = args[0];
        String encodingIn = args[1];
        String fileOut = args[2];
        String encodingOut = args[3];

        StringBuffer buffer = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), encodingIn));

        String line;
        while ((line = in.readLine()) != null) {
            if ((fileIn.endsWith(".html") || fileIn.endsWith(".htm")) && line.contains("charset=" + encodingIn)) {
                line = line.replace(encodingIn, encodingOut);
            }
            buffer.append(line + "\n");
        }

        in.close();

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOut), encodingOut));
        out.write(buffer.toString());
        out.close();
        System.out.print("(" + encodingIn + "," + fileIn + ") \n\t-> "); // NOPMD
        System.out.println("(" + encodingOut + "," + fileOut + "):" + new Date()); // NOPMD
    }
}
// CHECKSTYLE:ON
