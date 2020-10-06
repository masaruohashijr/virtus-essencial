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

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraEdicaoANEFInspetorPermissaoAlteracao {
    
    private AnaliseQuantitativaAQT anef;

    public RegraEdicaoANEFInspetorPermissaoAlteracao(AnaliseQuantitativaAQT anef) {
        this.anef = anef;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        // Falha - Inspetor altera e salva ANEF com estado diferente de Designado ou Em edi��o.
        SisapsUtil.adicionarErro(erros, new ErrorMessage("ANEF no estado \"" + anef
                .getEstado().getDescricao() + "\" n�o pode ser alterado pelo Inspetor."), 
                !Arrays.asList(EstadoAQTEnum.DESIGNADO, EstadoAQTEnum.EM_EDICAO)
                .contains(anef.getEstado()));
        SisapsUtil.lancarNegocioException(erros);

    }
}