package br.gov.bcb.sisaps.src.mediator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import jcifs.smb.SmbException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.util.IEntidadeJcifs;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

@Service
public class GeradorAnexoMediator {

    private static final int BUFFER_SIZE = 2 * 1024;

    @Autowired
    private IBcJcifs bcJcifs;

    public static GeradorAnexoMediator get() {
        return SpringUtils.get().getBean(GeradorAnexoMediator.class);
    }
    
    public void gerarArquivoAnexoUpload(IEntidadeJcifs arquivo, String nomeDoArquivo, String alias, String subDir)
            throws Exception { //NOPMD
        gerarArquivoAnexoUpload(arquivo.getInputStream(), nomeDoArquivo, alias, subDir);
    }
    
    public void gerarArquivoAnexoUpload(InputStream inputStream, String nomeDoArquivo, String alias, String subDir)
            throws Exception { //NOPMD
        OutputStream out = null;
        InputStream fin = null;
        if (Util.isIncluirBufferAnexos()) {
            BufferAnexos.getBufferInclusaoAnexos().add(new AnexoBuffer(inputStream, alias, subDir, nomeDoArquivo));
        } else {
            try {
                fin = inputStream;
                out = bcJcifs.criarArquivoDir(alias, subDir, nomeDoArquivo).getOutputStream();
                write(fin, out);
                out.flush();
            } finally {
                if (fin != null) {
                    fin.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    private void write(InputStream fin, OutputStream out) throws IOException {
        if (fin != null && out != null) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int b = fin.read(buffer);
            while (b > 0) {
                out.write(buffer, 0, b);
                b = fin.read(buffer);
            }
        }
    }
    
    public void gerarArquivosAnexosUpload(List<AnexoBuffer> anexosBuffer) throws Exception {
        for (AnexoBuffer anexo : anexosBuffer) {
            long tempo = System.currentTimeMillis();
            gerarArquivoAnexoUpload(anexo.getInputStream(), anexo.getNome(), anexo.getAlias(), anexo.getSubDir());
            System.out.println("###### Tempo inclusão arquivo: " + (System.currentTimeMillis() - tempo));
        }
    }
    
    public void incluirAnexosBuffer() {
        Util.setIncluirBufferAnexos(false);
        List<AnexoBuffer> anexos = BufferAnexos.getBufferInclusaoAnexos();
        if (CollectionUtils.isNotEmpty(anexos)) {
            System.out.println("####### " + anexos.size() + " anexo(s) para inclusão");
            try {
                GeradorAnexoMediator.get().gerarArquivosAnexosUpload(anexos);
            } catch (BCInfraException e) {
                throw new NegocioException(e.getMessage());
            } catch (SmbException e) {
                throw new NegocioException(e.getMessage());
                // CHECKSTYLE:OFF Exception deve ser tratada
            } catch (Exception e) {
                // CHECKSTYLE:ON
                throw new NegocioException(e.getMessage()); // NOPMD
            }
        }
    }
    
    public void excluirAnexosBuffer() {
        Util.setIncluirBufferAnexos(false);
        List<AnexoBuffer> anexos = BufferAnexos.getBufferExclusaoAnexos();
        if (CollectionUtils.isNotEmpty(anexos)) {
            System.out.println("###### " + anexos.size() + " anexo(s) para exclusão");
            for (AnexoBuffer anexo : anexos) {
                long tempo = System.currentTimeMillis();
                try {
                    if (anexo.getArquivoSmbFile().exists()) {
                        bcJcifs.apagarArquivo(anexo.getAlias(), anexo.getNome(), 
                                "Erro ao tentar excluir arquivo de anexo: " + anexo.getNome(), 
                                anexo.getArquivoSmbFile());
                        System.out.println("###### Tempo exclusão arquivo: " + (System.currentTimeMillis() - tempo));
                    }
                } catch (SmbException e) {
                    throw new NegocioException(e.getMessage());
                } catch (NegocioException e) {
                    throw new NegocioException(e.getMessage());
                }
            }
        }
    }
}
