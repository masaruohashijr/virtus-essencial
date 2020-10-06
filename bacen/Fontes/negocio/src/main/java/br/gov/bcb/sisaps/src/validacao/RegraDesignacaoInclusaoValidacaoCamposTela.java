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
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraDesignacaoInclusaoValidacaoCamposTela {

    private final List<ARCDesignacaoVO> arcsDesignacao;
    private final ServidorVO servidorEquipe;
    private final ServidorVO servidorUnidade;
    private final String matriculaLogado;

    public RegraDesignacaoInclusaoValidacaoCamposTela(String matriculaLogado,
            List<ARCDesignacaoVO> arcsDesignacao, ServidorVO servidorEquipe, ServidorVO servidorUnidade) {
        this.arcsDesignacao = arcsDesignacao;
        this.servidorEquipe = servidorEquipe;
        this.servidorUnidade = servidorUnidade;
        this.matriculaLogado = matriculaLogado;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        // C001 Falha - Clicar no botão designar sem selecionar, ao menos, um ARC.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_001),
                CollectionUtils.isEmpty(arcsDesignacao));
        SisapsUtil.lancarNegocioException(erros);
        // C002 Falha - Clicar no botão designar sem selecionar um servidor.
        SisapsUtil.adicionarErro(erros,
                new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_002),
                (servidorEquipe == null && servidorUnidade == null));
        SisapsUtil.lancarNegocioException(erros);
        // C003 Falha - Clicar no botão designar tendo selecionado os campos 'Servidores da equipe' e 'Outros servidores' simultaneamente.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_003),
                (servidorEquipe != null && servidorUnidade != null));
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_004),
                !AvaliacaoRiscoControleMediator.get().validarEstadoMatriz(arcsDesignacao));

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_DELEGACAO_ERRO),
                AvaliacaoRiscoControleMediator.get().validarUsuarioDelegado(matriculaLogado, arcsDesignacao));

        SisapsUtil.lancarNegocioException(erros);
    }

}