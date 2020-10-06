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

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraConclusaoEdicaoANEFInspetorValidacao {

    private static final String VAZIA = "'";
    private final AnaliseQuantitativaAQT aqt;
    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    public RegraConclusaoEdicaoANEFInspetorValidacao(AnaliseQuantitativaAQT aqt) {
        this.aqt = aqt;
    }

    public void validar() {
        validarCamposObrigatorios();
        validarPreenchimentoJustificativaItensElemento();
        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarCamposObrigatorios() {
        for (ElementoAQT elemento : aqt.getElementos()) {
            if (elemento.getParametroNotaInspetor() == null) {
                String mensagem =
                        "Campo 'Nota inspetor' do elemento " + VAZIA + elemento.getParametroElemento().getDescricao()
                                + VAZIA + " é de preenchimento obrigatório.";
                erros.add(new ErrorMessage(mensagem));
            }
        }
        erros.addAll(validarAvaliacaoARCInspetor(aqt.getAvaliacaoInspetor()));
    }

    private void validarPreenchimentoJustificativaItensElemento() {
        boolean existeNotaDiferenteNA = false;
        for (ElementoAQT elemento : aqt.getElementos()) {
            boolean preencheuJustificativa = false;
            for (ItemElementoAQT item : elemento.getItensElemento()) {
                if (item.getDocumento() != null
                        && StringUtils.isNotBlank(SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(item.getDocumento()
                                .getJustificativa()))) {
                    preencheuJustificativa = true;
                }
            }
            if (elemento.getParametroNotaInspetor() != null
                    && !elemento.getParametroNotaInspetor().getNotaNA().equals(SimNaoEnum.SIM)) {
                existeNotaDiferenteNA = true;
                if (!preencheuJustificativa) {
                    String mensagem =
                            ConstantesMensagens.MSG_APS_ANEF_ERRO_0015 + VAZIA
                                    + elemento.getParametroElemento().getDescricao() + "'.";
                    erros.add(new ErrorMessage(mensagem));
                }
            }
        }
        if (!existeNotaDiferenteNA && todasNotasDiferentesNull(aqt.getElementos())) {
            erros.add(new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_017));
        }
    }

    private boolean todasNotasDiferentesNull(List<ElementoAQT> elementos) {
        boolean notasDiferentesDeNull = false;
        for (ElementoAQT elemento : elementos) {
            if (elemento.getParametroNotaInspetor() == null) {
                notasDiferentesDeNull = true;
                break;
            }
        }
        return !notasDiferentesDeNull;
    }

    public ArrayList<ErrorMessage> validarAvaliacaoARCInspetor(AvaliacaoAQT avaliacaoAQT) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (avaliacaoAQT != null) {
            SisapsUtil.validarObrigatoriedade(avaliacaoAQT.getParametroNota(), "Nota de ajuste", erros);

            if (StringUtils.isEmpty(avaliacaoAQT.getJustificativa())) {
                String mensagem = "Campo 'Justificativa do ajuste' é de preenchimento obrigatório.";
                erros.add(new ErrorMessage(mensagem));
            }
        }
        return erros;
    }

}