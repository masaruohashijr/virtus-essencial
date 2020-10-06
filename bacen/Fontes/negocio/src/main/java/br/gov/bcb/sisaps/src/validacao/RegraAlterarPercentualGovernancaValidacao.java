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

public class RegraAlterarPercentualGovernancaValidacao {

    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    private final BigDecimal numeroFatorRelevanciaAE;

    public RegraAlterarPercentualGovernancaValidacao(BigDecimal numeroFatorRelevanciaAE) {
        this.numeroFatorRelevanciaAE = numeroFatorRelevanciaAE;
    }

    public void validar() {
        if (numeroFatorRelevanciaAE != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo participa��o percentual do bloco governan�a corporativa inv�lido."), verificarPercentual());
            SisapsUtil.lancarNegocioException(erros);
            
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo participa��o percentual do bloco governan�a corporativa n�o pode ser definido com 100%."), 
                    numeroFatorRelevanciaAE.intValue() == 1);
            SisapsUtil.lancarNegocioException(erros);
        }
    }

    private boolean verificarPercentual() {
        try {
            if (numeroFatorRelevanciaAE.intValue() > 1) {
                return true;
            } else if (numeroFatorRelevanciaAE.intValue() < 1 && numeroFatorRelevanciaAE.intValue() != 0) {
                return true;
            }
            numeroFatorRelevanciaAE.multiply(new BigDecimal(100)).intValueExact();
        } catch (ArithmeticException e) {
            return true;
        }
        return false;
    }
}