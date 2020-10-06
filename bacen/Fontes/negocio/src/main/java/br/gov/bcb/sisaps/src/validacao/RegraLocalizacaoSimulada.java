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

            //Caso a localização preenchida for igual a localização de substituição, se existir, ou caso não exista e for igual a localização do usuário logado,
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