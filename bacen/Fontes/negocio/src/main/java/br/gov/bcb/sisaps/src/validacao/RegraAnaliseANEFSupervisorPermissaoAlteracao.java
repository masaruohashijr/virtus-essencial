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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnaliseANEFSupervisorPermissaoAlteracao {

    private AnaliseQuantitativaAQT anef;

    public RegraAnaliseANEFSupervisorPermissaoAlteracao(AnaliseQuantitativaAQT anef) {
        this.anef = anef;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(
                erros,
                new ErrorMessage("ANEF no estado \"" + anef.getEstado().getDescricao()
                        + "\" n�o pode ser alterado pelo supervisor."),
                !Arrays.asList(EstadoAQTEnum.ANALISE_DELEGADA,
                        EstadoAQTEnum.EM_ANALISE, EstadoAQTEnum.PREENCHIDO)
                        .contains(anef.getEstado()));
        SisapsUtil.lancarNegocioException(erros);
    }
}