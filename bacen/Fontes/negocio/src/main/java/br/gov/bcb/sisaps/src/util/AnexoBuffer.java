package br.gov.bcb.sisaps.src.util;

import java.io.InputStream;

import jcifs.smb.SmbFile;
import br.gov.bcb.sisaps.util.IFile;

public class AnexoBuffer implements IFile {
    
    private InputStream inputStream;
    private String alias;
    private String subDir;
    private String nome;
    private SmbFile arquivoSmbFile;
    
    public AnexoBuffer() {
        
    }
    
    public AnexoBuffer(InputStream inputStream, String alias, String subDir, String nomeDoArquivo) {
        this.inputStream = inputStream;
        this.alias = alias;
        this.subDir = subDir;
        this.nome = nomeDoArquivo;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSubDir() {
        return subDir;
    }

    public void setSubDir(String subDir) {
        this.subDir = subDir;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nomeDoArquivo) {
        this.nome = nomeDoArquivo;
    }

    public SmbFile getArquivoSmbFile() {
        return arquivoSmbFile;
    }

    public void setArquivoSmbFile(SmbFile arquivoSmbFile) {
        this.arquivoSmbFile = arquivoSmbFile;
    }
    
}
