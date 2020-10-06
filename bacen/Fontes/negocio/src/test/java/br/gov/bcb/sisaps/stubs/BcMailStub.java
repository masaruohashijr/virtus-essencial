/*
 * Sistema: TBC
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.stubs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.adaptadores.bcmail.IBcMail;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.util.IFile;

@Component(IBcMail.NOME)
public class BcMailStub implements IBcMail, Stub {

    private static List<Email> listaEMail = new ArrayList<Email>();

    @Override
    public void enviarEmail(Email email) {
        if (email.getAnexos() != null && !email.getAnexos().isEmpty()) {
            StringBuffer nomeArquivo = new StringBuffer("");
            String conector = "";
            for (IFile arquivo : email.getAnexos()) {
                nomeArquivo.append(conector).append(arquivo.getNome());
                conector = "; ";
            }
            email.setNomeAnexo(nomeArquivo.toString());
        }
        listaEMail.add(email);
    }

    public static List<Email> getListaEMail() {
        return listaEMail;
    }

    public static void limpaListaEMails() {
        listaEMail = new ArrayList<Email>();
    }

    @Override
    public void limpar() {
        listaEMail.clear();
    }

}
