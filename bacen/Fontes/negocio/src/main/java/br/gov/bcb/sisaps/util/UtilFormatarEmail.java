package br.gov.bcb.sisaps.util;
public class UtilFormatarEmail {

    private static final String FECHA_COCHETE = "]";
    private static final String ABRE_COCHETE = "[";
    private static final String MARCADOR_N = "n[";
    private static final String MARCADOR_S = "s[";
    private static final String MARCADOR_I = "i[";
    
    /**
     * Referência http://benjchristensen.com/2008/02/07/how-to-strip-invalid-xml-characters/. This
     * method ensures that the output String has only valid XML unicode characters as specified by
     * the XML 1.0 standard. For reference, please see <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will
     * return an empty String if the input is null or empty.
     * 
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String removerCaracteresInvalidosXml(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.
        if (isNullEmpty(in)) {
            out.append(""); // vacancy test.
        } else {
            for (int i = 0; i < in.length(); i++) {
                current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
                if (isCaracterXmlValido(current)) {
                    out.append(current);
                }

            }
        }
        return out.toString();
    }

    public static boolean isNullEmpty(String in) {
        return in == null || ("".equals(in));
    }

    public static boolean isCaracterXmlValido(char current) {
        if (isConjuntoUm(current)) {
            return true;
        }
        if (isConjuntoDois(current)) {
            return true;
        }
        if (isConjuntoTres(current)) {
            return true;
        }
        if (isConjuntoQuatro(current)) {
            return true;
        }
        return false;
    }

    private static boolean isConjuntoUm(char current) {
        return (current == 0x9) || (current == 0xA) || (current == 0xD);
    }

    private static boolean isConjuntoDois(char current) {
        return (current >= 0x20) && (current <= 0xD7FF);
    }

    private static boolean isConjuntoTres(char current) {
        return (current >= 0xE000) && (current <= 0xFFFD);
    }

    private static boolean isConjuntoQuatro(char current) {
        return (current >= 0x10000) && (current <= 0x10FFFF);
    }
    
    
    public static String removerMarcadoresEmail(String textoAntes) {
        if (!Util.isNuloOuVazio(textoAntes) && isTextoContemMarcadores(textoAntes)) {
            String textoSemMarcacoes = removeMarcacoes(textoAntes);
            String prefixoAuditar = "Auditar";
            if (textoAntes.startsWith(ABRE_COCHETE + prefixoAuditar + FECHA_COCHETE)) {
                textoSemMarcacoes =
                        textoSemMarcacoes.replaceFirst(prefixoAuditar, ABRE_COCHETE + prefixoAuditar + FECHA_COCHETE);
            }
            return textoSemMarcacoes;
        } else {
            return textoAntes;
        }

    }

    private static boolean isTextoContemMarcadores(String texto) {
        return texto.contains(MARCADOR_I) || texto.contains(MARCADOR_S) || texto.contains(Constantes.LINK_ABRE)
                || texto.contains(MARCADOR_N);
    }

    private static String removeMarcacoes(String texto) {
        return texto.replace(MARCADOR_I, "").replace(MARCADOR_N, "").replace(MARCADOR_S, "")
                .replace(Constantes.LINK_ABRE, "").replace(ABRE_COCHETE, "").replace(FECHA_COCHETE, "");
    }

    
}
