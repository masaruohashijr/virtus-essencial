/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnexosValidacaoImagem implements IRegraAnexosValidacao {

    private static final String TIPO_IMAGEM_INVALIDO = "Tipo de imagem inv�lido.";
    private static final String NOME_CARACTERES_ESPECIAIS = "O nome do arquivo n�o � aceito pelo sistema. "
            + "Tente renome�-lo removendo caracteres especiais como travess�es e s�mbolos.";
    
    // Tipos de imagens aceitas.
    private static final String[] TIPOS_IMAGENS = new String[] {
    	".png", ".gif"
	};
    
    // Construtor
    public RegraAnexosValidacaoImagem() {
    }

    // Valida a imagem anexada.
    @Override
	public void validar(InputStream anexo, String nomeArquivo) {
    	// Declara��es
        ArrayList<ErrorMessage> erros;
        boolean tipoValido;

        // Inicializa��es
        erros = new ArrayList<ErrorMessage>();
        
        SisapsUtil.adicionarErro(erros, new ErrorMessage(NOME_CARACTERES_ESPECIAIS), !validarNomeArquivo(nomeArquivo));
        
        // Valida o tipo de arquivo.
        tipoValido = false;
        for (String tipo : TIPOS_IMAGENS) {
			if (nomeArquivo.toLowerCase().endsWith(tipo)) {
				tipoValido = true;
				break;
			}
		}
        
        SisapsUtil.adicionarErro(erros, new ErrorMessage(TIPO_IMAGEM_INVALIDO), !tipoValido);
        SisapsUtil.lancarNegocioException(erros);
    }
    
    public boolean validarNomeArquivo(String nomeArquivo) {
        Pattern asciiPattern = Pattern.compile("\\p{ASCII}*$");
        Matcher matcher = asciiPattern.matcher(nomeArquivo);
        return matcher.matches();
    }

}