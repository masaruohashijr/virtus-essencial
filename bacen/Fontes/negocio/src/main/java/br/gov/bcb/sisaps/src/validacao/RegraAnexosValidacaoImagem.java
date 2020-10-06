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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnexosValidacaoImagem implements IRegraAnexosValidacao {

    private static final String TIPO_IMAGEM_INVALIDO = "Tipo de imagem inválido.";
    private static final String NOME_CARACTERES_ESPECIAIS = "O nome do arquivo não é aceito pelo sistema. "
            + "Tente renomeá-lo removendo caracteres especiais como travessões e símbolos.";
    
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
    	// Declarações
        ArrayList<ErrorMessage> erros;
        boolean tipoValido;

        // Inicializações
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