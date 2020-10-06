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

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraLocalizacaoSimulada {

    private String novaLocalizacaoSimulada;
    private ServidorVO servidorLogado;

    public RegraLocalizacaoSimulada(String novaLocalizacaoSimulada, ServidorVO servidorLogado) {
        this.novaLocalizacaoSimulada = novaLocalizacaoSimulada;
        this.servidorLogado = servidorLogado;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros,
                new ErrorMessage(ConstantesMensagens.MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_002),
                Util.isNuloOuVazio(novaLocalizacaoSimulada));

        if (novaLocalizacaoSimulada != null && erros.isEmpty()) {
            ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo =
                    BcPessoaAdapter.get().buscarComponenteOrganizacionalPorRotulo(novaLocalizacaoSimulada, null);
            if (buscarComponenteOrganizacionalPorRotulo == null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(
                        ConstantesMensagens.MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_001), true);

            }

            //Caso a localiza��o preenchida for igual a localiza��o de substitui��o, se existir, ou caso n�o exista e for igual a localiza��o do usu�rio logado,
            if ((servidorLogado.getLocalizacaoSimulada() != null && servidorLogado.getLocalizacaoSimulada().equals(
                    novaLocalizacaoSimulada))
                    || (servidorLogado.getLocalizacaoSimulada() == null && servidorLogado.getLocalizacaoAtual().equals(
                            novaLocalizacaoSimulada))) {

                SisapsUtil.adicionarErro(erros, new ErrorMessage(
                        ConstantesMensagens.MSG_APS_REGRA_SIMULAR_LOCALIZACAO_ERRO_003), true);
            }
            
            SisapsUtil.lancarNegocioException(erros);

        }

        SisapsUtil.lancarNegocioException(erros);

    }

}