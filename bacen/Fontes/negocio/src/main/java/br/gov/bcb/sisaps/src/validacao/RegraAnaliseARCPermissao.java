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

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAnaliseARCPermissao {

    private Ciclo ciclo;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private String matricula;
    private Matriz matriz;

    public RegraAnaliseARCPermissao(Ciclo ciclo, Matriz matriz, AvaliacaoRiscoControle avaliacaoRiscoControle,
            String matricula) {
        this.ciclo = ciclo;
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
        this.matriz = matriz;

        this.matricula = matricula;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        // C001 Falha - Inspetor altera e salva ARC n�o delegado para ele.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_007),
                !(DelegacaoMediator.get().isARCDelegado(avaliacaoRiscoControle, matricula) || CicloMediator.get()
                        .isSupervisor(ciclo.getEntidadeSupervisionavel().getLocalizacao())));
        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(
                erros,
                new ErrorMessage("Opera��o n�o permitida para ARC no estado \""
                        + avaliacaoRiscoControle.getEstado().getDescricao() + "\"."),
                !Arrays.asList(EstadoARCEnum.ANALISE_DELEGADA,
                        EstadoARCEnum.EM_ANALISE, EstadoARCEnum.PREENCHIDO)
                        .contains(avaliacaoRiscoControle.getEstado()));
        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_008),
                !EstadoCicloEnum.EM_ANDAMENTO.equals(ciclo.getEstadoCiclo().getEstado()));

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_009),
                !EstadoMatrizEnum.VIGENTE.equals(matriz.getEstadoMatriz()));

        SisapsUtil.lancarNegocioException(erros);

    }

}