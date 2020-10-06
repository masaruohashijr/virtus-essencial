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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAlterarPercentualANEFValidacao {

    private static final String O_PERCENTUAL_PARA = "O percentual para '";

    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    private List<PesoAQT> listaPesos = new ArrayList<PesoAQT>();

    private List<PesoAQT> listaPesosVigente = new ArrayList<PesoAQT>();

    private List<PesoAQT> listaPesosRascunhos = new ArrayList<PesoAQT>();

    public RegraAlterarPercentualANEFValidacao(List<PesoAQT> listaPesosNovos, List<PesoAQT> listaPesosVigente,
            List<PesoAQT> listaPesosRascunhos) {
        this.listaPesos = listaPesosNovos;
        this.listaPesosVigente = listaPesosVigente;
        this.listaPesosRascunhos = listaPesosRascunhos;
    }

    public void validar() {
        validarPercentuais();
        somaPercentual();
        validarPercentuaisEmEdicao();
        validarPercentuaisVigente();
        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarPercentuais() {
        for (PesoAQT peso : listaPesos) {
            if (peso.getValor() == null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(O_PERCENTUAL_PARA
                        + peso.getParametroAQT().getDescricao() + "' é de preenchimento obrigatório."));
            } else
            if (peso.getValor() <= 0) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(O_PERCENTUAL_PARA
                        + peso.getParametroAQT().getDescricao() + "' deve ser maior que zero."));
            }

        }

    }

    private void somaPercentual() {
        if (!listaPesos.isEmpty()) {
            short soma = 0;
            boolean contem = false;
            for (PesoAQT peso : listaPesos) {
                if (peso.getValor() == null || peso.getValor() == 0) {
                    contem = true;
                    break;
                }
                if (!contem) {
                    soma += peso.getValor();
                }
            }
            if (!contem && (soma > 100 || soma < 100)) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage("A soma dos percentuais deve ser 100%."));
            }
            soma = 0;
        }
    }

    private void validarPercentuaisEmEdicao() {
        List<Boolean> pesosIguaisEmEdicao = new ArrayList<Boolean>();
        for (int i = 0; i < listaPesosRascunhos.size(); i++) {
            if (!listaPesos.isEmpty() && listaPesos.get(i).getValor() != null
                    && listaPesos.get(i).getValor().equals(listaPesosRascunhos.get(i).getValor())) {
                pesosIguaisEmEdicao.add(true);
            } else {
                pesosIguaisEmEdicao.add(false);
            }
        }
        if (!pesosIguaisEmEdicao.isEmpty() && !pesosIguaisEmEdicao.contains(false)) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Novos percentuais iguais aos percentuais da coluna 'Em edição'."));
        }

    }
    
    private void validarPercentuaisVigente() {
        List<Boolean> pesosIguaisVigente = new ArrayList<Boolean>();
        for (int i = 0; i < listaPesosVigente.size(); i++) {
            if (!listaPesos.isEmpty() && listaPesos.get(i).getValor() != null
                    && listaPesos.get(i).getValor().equals(listaPesosVigente.get(i).getValor())) {
                pesosIguaisVigente.add(true);
            } else {
                pesosIguaisVigente.add(false);
            }
        }

        if (!pesosIguaisVigente.isEmpty() && !pesosIguaisVigente.contains(false)) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Novos percentuais iguais aos percentuais da coluna 'Vigente'."));
        }
    }

}