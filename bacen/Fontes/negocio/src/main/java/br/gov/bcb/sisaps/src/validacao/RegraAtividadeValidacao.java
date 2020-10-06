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

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAtividadeValidacao {

    private static final String LABEL_NOME_ATIVIDADE = "Nome da atividade";
    private static final String LABEL_PESO_ATIVIDADE = "Peso da atividade";
    private static final String LABEL_TIPO_ATIVIDADE = "Tipo de atividade";
    private static final String LABEL_UNIDADE = "Unidade";
    private static final String LABEL_GRUPO_RISCO_CONTROLE = "Grupo de risco e controle";
    private static final String LABEL_PARAMETRO_GRUPO_RISCO_CONTROLE = "Parâmetro grupo de risco e controle";
    private static final String LABEL_PESO_GRUPO_RISCO_CONTROLE = "Peso do grupo de risco e controle";

    private final Atividade atividade;
    private final AtividadeMediator atividadeMediator;
    private final boolean isAlteracao;

    public RegraAtividadeValidacao(Atividade atividade, AtividadeMediator atividadeMediator, boolean isAlteracao) {
        this.atividade = atividade;
        this.atividadeMediator = atividadeMediator;
        this.isAlteracao = isAlteracao;
    }

    public void validar() {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.validarObrigatoriedade(atividade.getParametroPeso(), LABEL_PESO_ATIVIDADE, erros);
        SisapsUtil.validarObrigatoriedade(atividade.getTipoAtividade(), LABEL_TIPO_ATIVIDADE, erros);

        if (TipoUnidadeAtividadeEnum.NEGOCIO.equals(atividade.getTipoAtividade())) {
            SisapsUtil
                    .validarObrigatoriedade(atividade.getParametroTipoAtividadeNegocio(), LABEL_TIPO_ATIVIDADE, erros);
        }

        SisapsUtil.validarObrigatoriedade(atividade.getNome(), LABEL_NOME_ATIVIDADE, erros);

        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(atividade.getTipoAtividade())) {
            SisapsUtil.validarObrigatoriedade(atividade.getUnidade(), LABEL_UNIDADE, erros);
        }

        if(!isAlteracao){
        	SisapsUtil.validarObrigatoriedade(atividade.getCelulasRiscoControle(), LABEL_GRUPO_RISCO_CONTROLE, erros);
        }

        if (atividade.getCelulasRiscoControle() != null) {
            for (CelulaRiscoControle celula : atividade.getCelulasRiscoControle()) {
                SisapsUtil.validarObrigatoriedade(celula.getParametroGrupoRiscoControle(),
                        LABEL_PARAMETRO_GRUPO_RISCO_CONTROLE, erros);
                SisapsUtil.validarObrigatoriedade(celula.getParametroPeso(), LABEL_PESO_GRUPO_RISCO_CONTROLE, erros);
            }
        }

        if (!StringUtil.isVazioOuNulo(atividade.getNome()) && !isAlteracao) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_001),
                    atividadeMediator.existeAtividadeMesmoNome(atividade));
        }

        if (EstadoMatrizEnum.VIGENTE.equals(atividade.getMatriz().getEstadoMatriz())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_004));
        }

        if (isAlteracao) {
            validarAlteracao(erros);
        }

        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarAlteracao(ArrayList<ErrorMessage> erros) {
        Atividade atividadeBase = atividadeMediator.loadPK(atividade.getPk());

        if (atividade.getTipoAtividade() != null) {
            if (atividadeBase.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.CORPORATIVO)
                    && atividade.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_002));
            }

            if (atividadeBase.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.NEGOCIO)
                    && atividade.getTipoAtividade().equals(TipoUnidadeAtividadeEnum.CORPORATIVO)) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_003));
            }
        }

        if (!atividadeBase.getNome().equals(atividade.getNome())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_ATIVIDADE_ERRO_001),
                    atividadeMediator.existeAtividadeMesmoNome(atividade));
        }
    }
}