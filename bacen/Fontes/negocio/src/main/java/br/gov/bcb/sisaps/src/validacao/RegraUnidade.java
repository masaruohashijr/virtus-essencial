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

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraUnidade {

    private static final String LABEL_NOME_UNIDADE = "Nome da unidade";
    private static final String LABEL_PESO_UNIDADE = "Peso da unidade";
    private static final String LABEL_FATOR_REVELANCIA = "Fator de relevância";

    private final Unidade unidade;
    private final UnidadeMediator unidadeMediator;

    public RegraUnidade(Unidade unidade, UnidadeMediator unidadeMediator) {
        this.unidade = unidade;
        this.unidadeMediator = unidadeMediator;
    }

    public void validar(boolean isAlterar) {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        if (EstadoMatrizEnum.VIGENTE.equals(unidade.getMatriz().getEstadoMatriz())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_004));
        }

        SisapsUtil.validarObrigatoriedade(unidade.getNome(), LABEL_NOME_UNIDADE, erros);

        SisapsUtil.validarObrigatoriedade(unidade.getParametroPeso(), LABEL_PESO_UNIDADE, erros);

        validarCorporativo(isAlterar, erros);

        if (!StringUtil.isVazioOuNulo(unidade.getNome()) && !isAlterar) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_UNIDADE_ERRO_001),
                    unidadeMediator.existeUnidadeMesmoNome(unidade));
        }

        if (isAlterar) {
            Unidade unidadeBase = unidadeMediator.loadPK(unidade.getPk());
            if (!StringUtil.isVazioOuNulo(unidade.getNome()) && !unidadeBase.getNome().equals(unidade.getNome())) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_UNIDADE_ERRO_001),
                        unidadeMediator.existeUnidadeMesmoNome(unidade));
            }
        }

        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarCorporativo(boolean isAlterar, ArrayList<ErrorMessage> erros) {
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(unidade.getTipoUnidade())) {
            if (!isAlterar) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_UNIDADE_ERRO_003));

            }

            SisapsUtil.validarObrigatoriedade(unidade.getFatorRelevancia(), LABEL_FATOR_REVELANCIA, erros);
            if (unidade.getFatorRelevancia() != null) {
                // Campo Fator de relevância deve ser maior do que zero (0) e menor ou igual a um (1).
                int menorZero = unidade.getFatorRelevancia().compareTo(BigDecimal.ZERO);
                int maiorUm = unidade.getFatorRelevancia().compareTo(BigDecimal.ONE);
                boolean condicao = (menorZero == -1  || menorZero == 0) || maiorUm == 1;
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_UNIDADE_ERRO_004),
                        condicao);
            }

        }
    }

    public void validarExclusao() {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.validarObrigatoriedade(unidade.getNome(), LABEL_NOME_UNIDADE, erros);

        SisapsUtil.validarObrigatoriedade(unidade.getParametroPeso(), LABEL_PESO_UNIDADE, erros);

        SisapsUtil.lancarNegocioException(erros);
    }
}