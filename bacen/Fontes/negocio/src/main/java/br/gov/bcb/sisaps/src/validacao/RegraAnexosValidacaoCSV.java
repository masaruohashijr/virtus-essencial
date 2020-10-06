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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnexosValidacaoCSV implements IRegraAnexosValidacaoCSV {

    private static final String DIFERENTE_DE_CSV = "Não é permitido anexar arquivos com formato diferente de CSV.";
    private static final String VALOR_VALIDO = "prioridade,email,matricula,nome,equipe,funcao,Equipe_a_Excluir,"
            + "considerar_subordinadas_excluir,Equipe_a_Incluir,considerar_subordinadas_incluir";

    private void addValidarArquivoInvalido(ArrayList<ErrorMessage> erros, File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            if (br != null) {
                String campo = br.readLine().replace("ï»¿", "");
                if (campo != null && !campo.equals(VALOR_VALIDO)) {
                    SisapsUtil.adicionarErro(erros, new ErrorMessage("Formato do arquivo inválido."));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    @Override
    public void validar(InputStream anexo, File file) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        addValidarFormatoCSV(file.getName(), erros);
        addValidarArquivoInvalido(erros, file);
    }

    private void addValidarFormatoCSV(String nomeArquivo, ArrayList<ErrorMessage> erros) {
        SisapsUtil
                .adicionarErro(erros, new ErrorMessage(DIFERENTE_DE_CSV), !nomeArquivo.toLowerCase().endsWith(".csv"));
        SisapsUtil.lancarNegocioException(erros);
    }

}