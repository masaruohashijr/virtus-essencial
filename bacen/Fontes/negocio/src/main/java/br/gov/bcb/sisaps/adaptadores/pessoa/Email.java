/*
 * Sistema TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import br.gov.bcb.sisaps.util.IFile;
import br.gov.bcb.sisaps.util.Util;

public class Email {
    private static final String COLCHETE_FECHA = "]";
    private static final int PRIMO = 31;
    private String remetente;
    private List<String> destinatarios;
    private String assunto;
    private String corpo;
    private List<IFile> anexos = Collections.emptyList();
    private String nomeAnexo;

    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getDestinatario() {
        String resultado = null;
        if (destinatarios != null) {
            resultado = destinatarios.toString().replace("[", "").replace(COLCHETE_FECHA, "").replace(",", ";");
        }
        return resultado;
    }

    public void setDestinatario(String destinatario) {
        destinatarios = new LinkedList<String>();
        StringTokenizer st = new StringTokenizer(destinatario, ",;");
        while (st.hasMoreElements()) {
            destinatarios.add(st.nextToken().trim());
        }
    }

    public void setCorpoSemFormatacao(String corpo) {
        this.corpo = corpo;
    }

    public String getCorpoSemFormatacao() {
        return Util.normalize(corpo.replaceAll("\\<.*?\\>", ""));
    }

    public List<IFile> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<IFile> anexos) {
        this.anexos = anexos;
    }

    /**
     * Não usar está variável, ela só foi criada para o plugin de saida do email.
     * 
     * @return
     */
    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = PRIMO * result + hashCode(assunto);
        result = PRIMO * result + hashCode(getCorpoSemFormatacao());
        result = PRIMO * result + hashCode(destinatarios);
        result = PRIMO * result + hashCode(nomeAnexo);
        result = PRIMO * result + hashCode(remetente);
        return result;
    }

    private int hashCode(Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Email other = (Email) obj;
        return verificarIgualdade(other);
    }

    private boolean verificarIgualdade(Email other) {
        boolean retornoBool = verificaAssuntoCorpo(other);
        retornoBool = retornoBool && verificaDestinatariosNomeAnexo(other);
        retornoBool = retornoBool && Util.saoIguais(remetente, other.remetente);
        return retornoBool;
    }

    private boolean verificaAssuntoCorpo(Email other) {
        return Util.saoIguais(assunto, other.assunto)
                && Util.saoIguais(getCorpoSemFormatacao(), other.getCorpoSemFormatacao());
    }

    private boolean verificaDestinatariosNomeAnexo(Email other) {
        return Util.saoIguais(destinatarios, other.destinatarios) && Util.saoIguais(nomeAnexo, other.nomeAnexo);
    }

    @Override
    public String toString() {
        return "Email [remetente=" + remetente + ", destinatarios=" + destinatarios + ", assunto=" + assunto
                + ", corpo=" + getCorpoSemFormatacao() + ", nomeAnexos=" + nomeAnexo + COLCHETE_FECHA;
    }

}
