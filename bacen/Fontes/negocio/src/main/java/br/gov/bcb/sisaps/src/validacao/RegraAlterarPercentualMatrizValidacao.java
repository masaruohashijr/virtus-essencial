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

public class RegraAlterarPercentualMatrizValidacao {

    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    private final BigDecimal numeroFatorRelevanciaUC;

    public RegraAlterarPercentualMatrizValidacao(BigDecimal numeroFatorRelevanciaUC) {
        this.numeroFatorRelevanciaUC = numeroFatorRelevanciaUC;
    }

    public void validar() {

        SisapsUtil.adicionarErro(erros, new ErrorMessage(
                "Campo participação percentual do bloco corporativo é de preenchimento obrigatório"),
                numeroFatorRelevanciaUC == null);

        if (numeroFatorRelevanciaUC != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo participação percentual do bloco corporativo inválido."), verificarPercentual());
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