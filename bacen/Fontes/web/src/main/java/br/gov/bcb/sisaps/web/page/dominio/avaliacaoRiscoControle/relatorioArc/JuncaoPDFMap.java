/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.relatorioArc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class JuncaoPDFMap {

    public static PdfReader gerarPDF(byte[] aquivo, Map<String, List<byte[]>> mapaMap) throws IOException,
            DocumentException {
        PdfStamper escritor = null;
        PdfReader reader = new PdfReader(aquivo);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            escritor = new PdfStamper(reader, byteArrayOutputStream);
            if (!mapaMap.isEmpty()) {
                for (int i = 1; i <= escritor.getReader().getNumberOfPages(); i++) {
                    String textoPrincipal = PdfTextExtractor.getTextFromPage(reader, i);
                    obterChaveEGerarArquivo(mapaMap, escritor, i, textoPrincipal);
                }
            }
        } finally {
            if (escritor != null) {
                escritor.close();
            }
        }

        return new PdfReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    private static void obterChaveEGerarArquivo(Map<String, List<byte[]>> mapaMap, PdfStamper escritor, int i,
            String textoPrincipal) throws IOException, DocumentException {
        String chave = obterChaveAnexo(textoPrincipal, mapaMap);
        if (!chave.isEmpty()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfCopyFields copy;
            BufferedOutputStream bos = null;
            PdfReader.unethicalreading = true;
            try {

                bos = new BufferedOutputStream(byteArrayOutputStream); //Criamos o arquivo  
                copy = new PdfCopyFields(bos);
                for (byte[] b : mapaMap.get(chave)) {
                    copy.addDocument(new PdfReader(b));
                }
                copy.close();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }

            PdfReader leitorAnexo = new PdfReader(byteArrayOutputStream.toByteArray());
            int numeroPaginasAnexo = leitorAnexo.getNumberOfPages();
            Integer valorPaginaAtual = Integer.valueOf(i);
            PdfContentByte cb;
            for (int j = 1; j <= numeroPaginasAnexo; j++) {
                valorPaginaAtual++;
                escritor.insertPage(valorPaginaAtual, PageSize.A4);
                cb = escritor.getOverContent(valorPaginaAtual);
                escritor.getWriter().setPageEmpty(false);
                PdfImportedPage importedPage = escritor.getImportedPage(leitorAnexo, j);
                cb.addTemplate(importedPage, 1, 0, 0, 1, 0, 0);
                importedPage.closePath();
            }
            leitorAnexo.close();
        }

    }

    public static String obterChaveAnexo(String texto, Map<String, List<byte[]>> mapa) {

        List<String> lista = configurarListaAnexos(mapa);
        String resposta = "";

        for (String anexo : lista) {

            if (texto.contains(anexo)) {
                resposta = anexo;

            }
        }
        return resposta;
    }

    private static List<String> configurarListaAnexos(Map<String, List<byte[]>> map) {
        List<String> listaIdentificadores = new ArrayList<String>(map.keySet());
        Collections.sort(listaIdentificadores);
        return listaIdentificadores;
    }

}
