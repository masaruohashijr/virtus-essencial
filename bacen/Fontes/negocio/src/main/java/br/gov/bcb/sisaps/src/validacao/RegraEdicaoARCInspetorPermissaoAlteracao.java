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

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraEdicaoARCInspetorPermissaoAlteracao {
    
    private Ciclo ciclo;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private DesignacaoMediator designacaoMediator;

    public RegraEdicaoARCInspetorPermissaoAlteracao(Ciclo ciclo,
            AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.ciclo = ciclo;
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
        this.designacaoMediator = SpringUtilsExtended.get().getBean(DesignacaoMediator.class);
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        
        // C001 Falha - Inspetor altera e salva ARC n�o designado para ele.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_001), 
                !designacaoMediator.isARCDesignado(avaliacaoRiscoControle, usuario.getMatricula()));
        SisapsUtil.lancarNegocioException(erros);

        // C002 Falha - Inspetor altera e salva ARC com estado diferente de 2 - Designado ou 3 - Em edi��o.
        SisapsUtil.adicionarErro(erros, new ErrorMessage("ARC no estado \"" + avaliacaoRiscoControle
                .getEstado().getDescricao() + "\" n�o pode ser alterado pelo Inspetor."), 
                !Arrays.asList(EstadoARCEnum.DESIGNADO, EstadoARCEnum.EM_EDICAO)
                .contains(avaliacaoRiscoControle.getEstado()));
        SisapsUtil.lancarNegocioException(erros);

        // C003 Falha - Ciclo e matriz em estados que n�o permitem edi��o de ARC.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_003), 
                !ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO));
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_004), 
                !ciclo.getMatriz().getEstadoMatriz().equals(EstadoMatrizEnum.VIGENTE));
        SisapsUtil.lancarNegocioException(erros);
    }
}