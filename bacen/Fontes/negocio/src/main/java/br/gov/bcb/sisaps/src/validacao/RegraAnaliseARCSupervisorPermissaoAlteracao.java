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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnaliseARCSupervisorPermissaoAlteracao {

    private Ciclo ciclo;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;

    public RegraAnaliseARCSupervisorPermissaoAlteracao(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.ciclo = ciclo;
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(
                erros,
                new ErrorMessage("ARC no estado \"" + avaliacaoRiscoControle.getEstado().getDescricao()
                        + "\" não pode ser alterado pelo supervisor."),
                !Arrays.asList(EstadoARCEnum.ANALISE_DELEGADA,
                        EstadoARCEnum.EM_ANALISE, EstadoARCEnum.PREENCHIDO)
                        .contains(avaliacaoRiscoControle.getEstado()));
        SisapsUtil.lancarNegocioException(erros);

        // C003 Falha - Ciclo e matriz em estados que não permitem edição de
        // ARC.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_003), !ciclo
                .getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO));
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_004), !ciclo.getMatriz()
                .getEstadoMatriz().equals(EstadoMatrizEnum.VIGENTE));
        SisapsUtil.lancarNegocioException(erros);
    }
}