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

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.TendenciaMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraConclusaoEdicaoARCInspetorValidacao {

    private final AvaliacaoRiscoControle arc;
    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    public RegraConclusaoEdicaoARCInspetorValidacao(AvaliacaoRiscoControle arc) {
        this.arc = arc;
    }

    public void validar() {
        validarCamposObrigatorios();
        validarPreenchimentoJustificativaItensElemento();
        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarCamposObrigatorios() {
        for (Elemento elemento : arc.getElementos()) {
            erros.addAll(new RegraEdicaoARCInspetorCamposObrigatoriosElemento(elemento, false).validar());
        }
        erros.addAll(TendenciaMediator.get().validarTendencia(arc.getTendenciaARCInspetor()));
        erros.addAll(validarAvaliacaoARCInspetor(arc.getAvaliacaoARCInspetor()));
    }
    
    private void validarPreenchimentoJustificativaItensElemento() {
        boolean existeNotaDiferenteNA = false;
        for (Elemento elemento : arc.getElementos()) {
            boolean preencheuJustificativa = false;
            for (ItemElemento item : elemento.getItensElemento()) {
                if (item.getDocumento() != null
                        && StringUtils.isNotBlank(SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(item.getDocumento()
                                .getJustificativa()))) {
                    preencheuJustificativa = true;
                }
            }
            if (elemento.getParametroNotaInspetor() != null
                    && elemento.getParametroNotaInspetor().getNaoAplicavel().equals(SimNaoEnum.NAO)) {
                existeNotaDiferenteNA = true;
                if (!preencheuJustificativa) {
                    erros.add(new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_006));
                    break;
                }
            }
        }
        if (!existeNotaDiferenteNA) {
            erros.add(new ErrorMessage(ConstantesMensagens.MSG_APS_ARC_ERRO_010));
        }
    }

    public ArrayList<ErrorMessage> validarAvaliacaoARCInspetor(AvaliacaoARC avaliacaoARC) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (avaliacaoARC != null) {
            SisapsUtil.validarObrigatoriedade(avaliacaoARC.getParametroNota(), "Nota de ajuste", erros);
            SisapsUtil.validarObrigatoriedade(
                    SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(avaliacaoARC.getJustificativa()),
                    "Justificativa do ajuste", erros);
        }
        return erros;
    }

}