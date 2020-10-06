/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraSinteseMatrizSalvarValidacaoCampos {

    private final SinteseDeRisco sinteseDeRisco;

    public RegraSinteseMatrizSalvarValidacaoCampos(SinteseDeRisco sinteseDeRisco) {
        this.sinteseDeRisco = sinteseDeRisco;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo \"S�ntese vigente\" � de preenchimento obrigat�rio."),
                sinteseDeRisco.getDescricaoSintese() == null);

        SisapsUtil.lancarNegocioException(erros);

    }
}