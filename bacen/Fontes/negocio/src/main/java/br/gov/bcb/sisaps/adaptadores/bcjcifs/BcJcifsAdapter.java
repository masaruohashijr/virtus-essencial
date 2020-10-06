/*
 * Sistema APS
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.bcjcifs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.springframework.stereotype.Service;

import br.gov.bcb.comum.bcjcifs.JCIFSHelper;
import br.gov.bcb.comum.excecoes.BCException;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

@Service
public class BcJcifsAdapter implements IBcJcifs {
    private static final String ERRO_AO_LER_ARQUIVO = "Erro ao ler arquivo: ";
    private static final String ERRO_AO_CRIAR_ARQUIVO = "Erro ao criar arquivo: ";

    public byte[] recuperarArquivo(String dir, String nomeDoArquivo, SmbFile arquivoSubDiretorio)
            throws NegocioException {
        byte[] arquivoBytes = null;
        InputStream inputStream = null;
        String mensagem = "Erro ao buscar o arquivo: " + dir + nomeDoArquivo;
        try {
            SmbFile arquivo = null;
            if (arquivoSubDiretorio == null) {
                arquivo = buscarArquivo(dir, nomeDoArquivo);
            } else {
                arquivo = arquivoSubDiretorio;
            }

            arquivoBytes = new byte[(int) arquivo.length()];
            inputStream = arquivo.getInputStream();
            inputStream.read(arquivoBytes);
        } catch (SmbException e) {
            throw new NegocioException(mensagem, e);
        } catch (IOException e) {
            throw new NegocioException(mensagem, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new NegocioException(mensagem, e);
                }
            }
        }
        return arquivoBytes;
    }

    public void apagarArquivo(String dir, String nomeDoArquivo, String prefixoMensagemErro, SmbFile arquivoSubDiretorio)
            throws NegocioException {
        SmbFile arquivo = null;
        if (arquivoSubDiretorio == null) {
            arquivo = buscarArquivo(dir, nomeDoArquivo);
        } else {
            arquivo = arquivoSubDiretorio;
        }
        try {
            arquivo.delete();
        } catch (SmbException e) {
            throw new NegocioException(prefixoMensagemErro + e.getMessage(), e);
        }
    }

    /**
     * Cria uma cópia do arquivo
     * 
     * @param dirOrigem - Diretório do arquivo a ser copiado
     * @param nomeArqOrigem - Nome do arquivo a ser copiado
     * @param dirDestino - Diretório onde será salvo o arquivo copiado
     * @param nomeArqDestino - Nome que o arquivo copiado terá
     * @throws BCException
     */
    public void copiarArquivo(String dirOrigem, String nomeArqOrigem, String dirDestino, String nomeArqDestino)
            throws BCException {
        byte[] arquivoBytes = null;
        InputStream in = null;
        SmbFile origem = buscarArquivo(dirOrigem, nomeArqOrigem);
        SmbFileOutputStream destino = criarArquivo(dirDestino, nomeArqDestino);
        String msgErro =
                "Erro ao tentar copiar o aquivo " + dirOrigem + nomeArqOrigem + " para " + dirDestino + nomeArqDestino;
        try {
            arquivoBytes = new byte[(int) origem.length()];
            in = origem.getInputStream();
            in.read(arquivoBytes);
            destino.write(arquivoBytes);
        } catch (SmbException e) {
            throw new BCException(msgErro, e);
        } catch (IOException e) {
            throw new BCException(msgErro, e);
        } finally {
            arquivoBytes = null;
            try {
                destino.close();
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new BCException(msgErro, e);
            }
        }
    }

    /**
     * Busca um arquivo.
     * 
     * @param dir Diretório do arquivo
     * @param nomeArquivo Nome do arquivo
     * @return O arquivo gerado representado como um objeto do tipo SmbFile.
     * @throws BCException Caso ocorra algum problema durante o processamento.
     */
    public SmbFile buscarArquivo(String dir, String nomeArquivo) {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        SmbFile arquivo = null;

        try {
            arquivo = jcifs.getSmbFile(dir, nomeArquivo);

        } catch (BCInfraException ex) {
            throw new NegocioException(ERRO_AO_LER_ARQUIVO + ex.getMessage(), ex);
        }

        return arquivo;
    }

    /**
     * Cria o arquivo vazio no diretório jcifs
     * 
     * @param dir Diretório do arquivo
     * @param nomeArquivo Nome do arquivo
     * @return O arquivo gerado representado como um objeto do tipo SmbFile.
     * @throws BCException Caso ocorra algum problema durante o processamento.
     */
    public SmbFileOutputStream criarArquivo(String dir, String nomeArquivo) throws BCException {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        SmbFileOutputStream destino = null;

        try {
            destino = jcifs.getSmbFileOutputStream(dir, nomeArquivo);
        } catch (BCInfraException ex) {
            throw new NegocioException(ERRO_AO_CRIAR_ARQUIVO + ex.getMessage(), ex);
        }

        return destino;
    }

    public SmbFile criarArquivoDir(String alias, String subDir, String nomeArquivo) throws BCException {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        try {
            SmbFile dirFinal = criarSubDiretorio(alias, subDir, jcifs);
            return new SmbFile(dirFinal, nomeArquivo);
        } catch (BCInfraException ex) {
            throw new NegocioException(ERRO_AO_CRIAR_ARQUIVO + ex.getMessage(), ex);
        } catch (MalformedURLException e) {
            throw new NegocioException(e.getMessage());
        } catch (UnknownHostException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    private SmbFile criarSubDiretorio(String alias, String subDir, JCIFSHelper jcifs) throws BCInfraException,
            MalformedURLException, UnknownHostException {
        SmbFile dirAlias = jcifs.getSmbFile(alias);
        return new SmbFile(dirAlias, subDir);
    }

}
