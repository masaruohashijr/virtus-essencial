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
                    "Campo participação percentual do bloco governança corporativa inválido."), verificarPercentual());
            SisapsUtil.lancarNegocioException(erros);
            
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo participação percentual do bloco governança corporativa não pode ser definido com 100%."), 
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