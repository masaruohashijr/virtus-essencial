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

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraCicloInclusaoValidacaoCampos {

    private static final String LABEL_ENTIDADE_SUPERVISIONAVEL = "Entidade supervisionável";
    private static final String LABEL_METODOLOGIA = "Metodologia";
    private static final String LABEL_DATA_INICIO_CICLO = "Data de início do ciclo";
    private final Ciclo ciclo;
    private final CicloMediator cicloMediator;

    public RegraCicloInclusaoValidacaoCampos(Ciclo ciclo) {
        this.ciclo = ciclo;
        this.cicloMediator = SpringUtilsExtended.get().getBean(CicloMediator.class);
    }

    public void validar(boolean isBatch) {

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.validarObrigatoriedade(ciclo.getEntidadeSupervisionavel(), LABEL_ENTIDADE_SUPERVISIONAVEL, erros);

        SisapsUtil.validarObrigatoriedade(ciclo.getMetodologia(), LABEL_METODOLOGIA, erros);

        SisapsUtil.validarObrigatoriedade(ciclo.getDataInicio(), LABEL_DATA_INICIO_CICLO, erros);

        SisapsUtil.lancarNegocioException(erros);

        if (ciclo.getEntidadeSupervisionavel() != null
                && SisapsUtil.isNuloOuVazio(ciclo.getEntidadeSupervisionavel().getPrioridade())) {
            erros.add(new ErrorMessage("Entidade supervisionável não possui prioridade definida."));
        }

        if (ciclo.getEntidadeSupervisionavel() != null && ciclo.getMetodologia() != null) {
            if (!isBatch) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_CICLO_ERRO_003),
                        !RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.SUPERVISOR));
            }
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_CICLO_ERRO_004),
                    cicloMediator.existeCicloVigenteParaES(ciclo.getEntidadeSupervisionavel()));
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_CICLO_ERRO_005), !(ciclo
                    .getMetodologia().getPk().equals(ciclo.getEntidadeSupervisionavel().getMetodologia().getPk())));
        }

        SisapsUtil.lancarNegocioException(erros);

        if (ciclo.getDataInicio() != null && !isBatch) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_CICLO_ERRO_001),
                    DataUtil.isDataMaior(ciclo.getDataInicio(), DataUtil.getDateTimeAtual().toDate()));
        }

        SisapsUtil.lancarNegocioException(erros);
    }
}