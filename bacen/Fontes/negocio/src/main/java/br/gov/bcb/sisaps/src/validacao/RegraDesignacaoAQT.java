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

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraDesignacaoAQT {

    private final ServidorVO servidorEquipe;
    private final ServidorVO servidorUnidade;

    public RegraDesignacaoAQT(ServidorVO servidorEquipe, ServidorVO servidorUnidade) {
        this.servidorEquipe = servidorEquipe;
        this.servidorUnidade = servidorUnidade;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        // C002 Falha - Clicar no botão designar sem selecionar um servidor.
        SisapsUtil.adicionarErro(erros,
                new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_DELEGACAO_AQT_ERRO_001),
                (servidorEquipe == null && servidorUnidade == null));
        SisapsUtil.lancarNegocioException(erros);
        // C003 Falha - Clicar no botão designar tendo selecionado os campos 'Servidores da equipe' e 'Outros servidores' simultaneamente.
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_003),
                (servidorEquipe != null && servidorUnidade != null));
        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_REGRA_PERFIL_ACESSO_ERRO_002),
                !RegraPerfilAcessoMediator.perfilSupervisor());

        SisapsUtil.lancarNegocioException(erros);
    }

}