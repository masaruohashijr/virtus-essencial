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
                        + "\" não pode ser alterado pelo supervisor."),
                !Arrays.asList(EstadoAQTEnum.ANALISE_DELEGADA,
                        EstadoAQTEnum.EM_ANALISE, EstadoAQTEnum.PREENCHIDO)
                        .contains(anef.getEstado()));
        SisapsUtil.lancarNegocioException(erros);
    }
}