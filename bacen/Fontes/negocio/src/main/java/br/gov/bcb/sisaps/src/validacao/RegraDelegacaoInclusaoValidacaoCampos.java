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
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraDelegacaoInclusaoValidacaoCampos {

    private final ServidorVO servidorEquipe;
    private final ServidorVO servidorUnidade;
    private final boolean usuarioEDesignado;

    public RegraDelegacaoInclusaoValidacaoCampos(boolean usuarioEDesignado, ServidorVO servidorEquipe,
            ServidorVO servidorUnidade) {
        this.servidorEquipe = servidorEquipe;
        this.servidorUnidade = servidorUnidade;
        this.usuarioEDesignado = usuarioEDesignado;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros,
                new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_ERRO_002),
                (servidorEquipe == null && servidorUnidade == null));

        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DELEGACAO_ERRO_001),
                (servidorEquipe != null && servidorUnidade != null));

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_DESIGNACAO_DELEGACAO_ERRO),
                usuarioEDesignado);

        SisapsUtil.lancarNegocioException(erros);
    }
}