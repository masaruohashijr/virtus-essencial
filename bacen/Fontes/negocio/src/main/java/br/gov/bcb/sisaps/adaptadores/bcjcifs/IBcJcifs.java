package br.gov.bcb.sisaps.adaptadores.bcjcifs;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import br.gov.bcb.comum.excecoes.BCException;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public interface IBcJcifs {
    String NOME = "bcJcifs";

    SmbFileOutputStream criarArquivo(String dir, String nomeDoArquivo) throws BCException;

    SmbFile criarArquivoDir(String alias, String subDir, String nomeDoArquivo) throws BCException;

    SmbFile buscarArquivo(String dir, String nomeDoArquivo) throws NegocioException;

    byte[] recuperarArquivo(String dir, String nomeDoArquivo, SmbFile arquivoSubDiretorio) throws NegocioException;

    void apagarArquivo(String dir, String nomeDoArquivo, String prefixoMensagemErro, SmbFile arquivoSubDiretorio)
            throws NegocioException;

    void copiarArquivo(String dirOrigem, String nomeArqOrigem, String dirDestino, String nomeArqDestino)
            throws BCException;
}
