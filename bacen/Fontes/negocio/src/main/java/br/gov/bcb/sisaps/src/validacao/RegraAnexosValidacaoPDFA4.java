/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

public class RegraAnexosValidacaoPDFA4 implements IRegraAnexosValidacao {

    private static final String DIFERENTE_DE_PDF_TAMANHO_A4 =
            "Não é permitido anexar arquivos com formato diferente de PDF tamanho A4.";
    
    private static final String NOME_CARACTERES_ESPECIAIS = "O nome do arquivo não é aceito pelo sistema. "
            + "Tente renomeá-lo removendo caracteres especiais como travessões e símbolos.";

    private static final float TOLERANCIA_DIFERENCA_TAMANHO = 2;
    
    public RegraAnexosValidacaoPDFA4() {
    }

    @Override
	public void validar(InputStream anexo, String nomeArquivo) {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros, new ErrorMessage(DIFERENTE_DE_PDF_TAMANHO_A4), !nomeArquivo.toLowerCase()
                .endsWith(".pdf"));
        
        SisapsUtil.adicionarErro(erros, new ErrorMessage(NOME_CARACTERES_ESPECIAIS), !validarNomeArquivo(nomeArquivo));
        
        SisapsUtil.lancarNegocioException(erros);

        PdfReader reader;
        try {
            reader = new PdfReader(anexo);
            Rectangle tamanhoPage = reader.getPageSize(1);
            if (!validarA4Vertical(tamanhoPage) || !validarA4LandScape(tamanhoPage)) {
                erros.add(new ErrorMessage(DIFERENTE_DE_PDF_TAMANHO_A4));
            }
        } catch (IOException e) {
            erros.add(new ErrorMessage(DIFERENTE_DE_PDF_TAMANHO_A4));

        }
        SisapsUtil.lancarNegocioException(erros);
    }
    
    public boolean validarNomeArquivo(String nomeArquivo) {
        Pattern asciiPattern = Pattern.compile("\\p{ASCII}*$");
        Matcher matcher = asciiPattern.matcher(nomeArquivo);
        return matcher.matches();
    }

    private boolean validarA4Vertical(Rectangle tamanhoPage) {
        float diferencaLargura = Math.abs(PageSize.A4.getWidth() - tamanhoPage.getWidth());
        float diferentaAltura = Math.abs(PageSize.A4.getHeight() - tamanhoPage.getHeight());
        return diferencaLargura < TOLERANCIA_DIFERENCA_TAMANHO && diferentaAltura < TOLERANCIA_DIFERENCA_TAMANHO;
    }

    private boolean validarA4LandScape(Rectangle tamanhoPage) {
        float diferencaLargura = Math.abs(PageSize.A4_LANDSCAPE.getWidth() - tamanhoPage.getWidth());
        float diferentaAltura = Math.abs(PageSize.A4_LANDSCAPE.getHeight() - tamanhoPage.getHeight());
        return diferencaLargura < TOLERANCIA_DIFERENCA_TAMANHO && diferentaAltura < TOLERANCIA_DIFERENCA_TAMANHO;
    }
    
    public static void main(String[] args) {
        System.out.println("ARQUIVO : "+ new RegraAnexosValidacaoPDFA4().validarNomeArquivo("ARQUIVO.pdf"));
    }
}