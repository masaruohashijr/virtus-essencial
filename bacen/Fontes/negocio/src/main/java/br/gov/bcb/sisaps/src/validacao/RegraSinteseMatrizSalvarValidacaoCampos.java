/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
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

        SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo \"Síntese vigente\" é de preenchimento obrigatório."),
                sinteseDeRisco.getDescricaoSintese() == null);

        SisapsUtil.lancarNegocioException(erros);

    }
}