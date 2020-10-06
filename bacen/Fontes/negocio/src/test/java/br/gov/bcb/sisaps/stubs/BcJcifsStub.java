package br.gov.bcb.sisaps.stubs;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.springframework.stereotype.Component;

import br.gov.bcb.comum.excecoes.BCException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.BcJcifsAdapter;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.util.AmbienteSO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

@Component(value = IBcJcifs.NOME)
public class BcJcifsStub {

    private IBcJcifs versaoOriginal = new BcJcifsAdapter();

    public SmbFileOutputStream criarArquivo(String dir, String nomeDoArquivo) throws BCException {
        if (AmbienteSO.ehWindows()) {
            return versaoOriginal.criarArquivo(dir, nomeDoArquivo);
        } else {
            return null;
        }
    }

    public SmbFile criarArquivoDir(String alias, String subDir, String nomeDoArquivo) throws BCException {
        if (AmbienteSO.ehWindows()) {
            return versaoOriginal.criarArquivoDir(alias, subDir, nomeDoArquivo);
        } else {
            return null;
        }
    }

    public byte[] recuperarArquivo(String dir, String nomeDoArquivo, SmbFile arquivoSubDiretorio)
            throws NegocioException {
        if (AmbienteSO.ehWindows()) {
            return versaoOriginal.recuperarArquivo(dir, nomeDoArquivo, arquivoSubDiretorio);
        } else {
            return new byte[0];
        }
    }

    public void apagarArquivo(String dir, String nomeDoArquivo, String prefixoMensagemErro, SmbFile arquivoSubDiretorio)
            throws NegocioException {
        if (AmbienteSO.ehWindows()) {
            versaoOriginal.apagarArquivo(dir, nomeDoArquivo, prefixoMensagemErro, arquivoSubDiretorio);
        }
    }

    public void copiarArquivo(String dirOrigem, String nomeArqOrigem, String dirDestino, String nomeArqDestino)
            throws BCException {
        if (AmbienteSO.ehWindows()) {
            versaoOriginal.copiarArquivo(dirOrigem, nomeArqOrigem, dirDestino, nomeArqDestino);
        }
    }

    public SmbFile buscarArquivo(String dir, String nomeArquivo) throws NegocioException {
        if (AmbienteSO.ehWindows()) {
            return versaoOriginal.buscarArquivo(dir, nomeArquivo);
        }
        return null;
    }
}
