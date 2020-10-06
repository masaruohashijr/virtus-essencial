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

import java.math.BigDecimal;
import java.util.ArrayList;

import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAlterarPercentualMatrizValidacao {

    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    private final BigDecimal numeroFatorRelevanciaUC;

    public RegraAlterarPercentualMatrizValidacao(BigDecimal numeroFatorRelevanciaUC) {
        this.numeroFatorRelevanciaUC = numeroFatorRelevanciaUC;
    }

    public void validar() {

        SisapsUtil.adicionarErro(erros, new ErrorMessage(
                "Campo participa��o percentual do bloco corporativo � de preenchimento obrigat�rio"),
                numeroFatorRelevanciaUC == null);

        if (numeroFatorRelevanciaUC != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo participa��o percentual do bloco corporativo inv�lido."), verificarPercentual());
        }

        SisapsUtil.lancarNegocioException(erros);

    }

    private boolean verificarPercentual() {
        try {
            if (numeroFatorRelevanciaUC.intValue() > 1) {
                return true;
            } else if (numeroFatorRelevanciaUC.intValue() < 1 && numeroFatorRelevanciaUC.intValue() != 0) {
                return true;
            }
            numeroFatorRelevanciaUC.multiply(new BigDecimal(100)).intValueExact();
        } catch (ArithmeticException e) {
            return true;
        }
        return false;
    }
}