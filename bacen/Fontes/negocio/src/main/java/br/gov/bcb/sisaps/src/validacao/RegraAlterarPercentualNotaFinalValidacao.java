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

public class RegraAlterarPercentualNotaFinalValidacao {

    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    private final BigDecimal numeroFatorRelevanciaAnef;

    public RegraAlterarPercentualNotaFinalValidacao(BigDecimal numeroFatorRelevanciaAnef) {
        this.numeroFatorRelevanciaAnef = numeroFatorRelevanciaAnef;
    }

    public void validar() {
        if (numeroFatorRelevanciaAnef != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("Campo percentual da Nota final da ES inválido."),
                    verificarPercentual());
            SisapsUtil.lancarNegocioException(erros);
            SisapsUtil.adicionarErro(
                    erros,
                    new ErrorMessage("Campo percentual da Nota final da ES não pode ser definido com 0% ou 100%."),
                    numeroFatorRelevanciaAnef.compareTo(BigDecimal.ZERO) == 0
                            || numeroFatorRelevanciaAnef.compareTo(BigDecimal.ONE) == 0);
            SisapsUtil.lancarNegocioException(erros);
        }
    }

    private boolean verificarPercentual() {
        try {
            if (numeroFatorRelevanciaAnef.intValue() > 1) {
                return true;
            } else if (numeroFatorRelevanciaAnef.intValue() < 1 && numeroFatorRelevanciaAnef.intValue() != 0) {
                return true;
            }
            numeroFatorRelevanciaAnef.multiply(new BigDecimal(100)).intValueExact();
        } catch (ArithmeticException e) {
            return true;
        }
        return false;
    }
}