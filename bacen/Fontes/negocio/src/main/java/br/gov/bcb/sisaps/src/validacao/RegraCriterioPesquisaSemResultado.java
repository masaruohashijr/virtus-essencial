/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
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
