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

        // Falha - Inspetor altera e salva ANEF com estado diferente de Designado ou Em edição.
        SisapsUtil.adicionarErro(erros, new ErrorMessage("ANEF no estado \"" + anef
                .getEstado().getDescricao() + "\" não pode ser alterado pelo Inspetor."), 
                !Arrays.asList(EstadoAQTEnum.DESIGNADO, EstadoAQTEnum.EM_EDICAO)
                .contains(anef.getEstado()));
        SisapsUtil.lancarNegocioException(erros);

    }
}