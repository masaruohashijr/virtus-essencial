/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.ciclo.inclusao;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.web.page.dominio.matriz.AbstractMatrizPage;

@SuppressWarnings("serial")
public class ConfirmarCadastrarCicloPage extends AbstractMatrizPage {

    private static final String CONFIRMACAO_DE_INCLUSAO_DO_CICLO = "Confirmação de inclusão do ciclo";

    public ConfirmarCadastrarCicloPage(Ciclo ciclo) {
        super(ciclo.getPk());
        success("O ciclo foi incluído com sucesso, be bem como o registro para análise econômico-financeira.");
        info("A matriz de riscos e controles encontra-se disponível para edição.");
        addComponentes();
    }

    @Override
    public String getTitulo() {
        return CONFIRMACAO_DE_INCLUSAO_DO_CICLO;
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0107";
    }

}
