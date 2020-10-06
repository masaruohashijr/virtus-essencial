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
import java.util.Arrays;

import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraDesignacaoInclusaoValidacaoCampos {

    private Designacao designacao;
    private boolean ehAnalise;

    public RegraDesignacaoInclusaoValidacaoCampos(Designacao designacao, boolean ehAnalise) {
        this.designacao = designacao;
        this.ehAnalise = ehAnalise;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (ehAnalise) {
            SisapsUtil.adicionarErro(
                    erros,
                    new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_006),
                    Arrays.asList(EstadoARCEnum.CONCLUIDO).contains(
                            designacao.getAvaliacaoRiscoControle().getEstado()));
        } else {
            // C005 Falha - Designar ARC no estado 3 - Em edi��o.
            // C006 Falha - Designar ARC no estado 4 - Preenchido.
            // C007 Falha - Designar ARC no estado 5 - An�lise delegada.
            // C008 Falha - Designar ARC no estado 6 - Em an�lise.
            SisapsUtil.adicionarErro(
                    erros,
                    new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_005),
                    !Arrays.asList(EstadoARCEnum.PREVISTO,
                            EstadoARCEnum.DESIGNADO).contains(
                                    designacao.getAvaliacaoRiscoControle().getEstado()));
        }
        SisapsUtil.lancarNegocioException(erros);
    }
}