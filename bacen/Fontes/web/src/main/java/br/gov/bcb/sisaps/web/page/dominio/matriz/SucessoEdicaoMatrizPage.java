/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.matriz;

@SuppressWarnings("serial")
public class SucessoEdicaoMatrizPage extends EdicaoMatrizPage {

    public SucessoEdicaoMatrizPage(Integer idCiclo, boolean isLiberacao) {
        super(idCiclo);
        if (isLiberacao) {
            success("Uma versão da matriz vigente está disponível para edição.");
        } else {
            success("As alterações foram desfeitas com sucesso.");
        }
    }

}
