/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.RegraBase;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;

public class RegraCriterioPesquisaSemResultado extends RegraBase<List<? extends ObjetoPersistenteVO>> {

    @Override
    protected String getMensagemErro() {
        return ConstantesMensagens.MSG_APS_RATING_REFACA_PESQUISA;
    }

    @Override
    public void validar(List<? extends ObjetoPersistenteVO> lista) {
        boolean condicao = SisapsUtil.isNuloOuVazio(lista);
        validar(condicao);
    }
}
