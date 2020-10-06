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

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraLiberacaoMatrizValidacao {

    private final Matriz matriz;
    private final MetodologiaMediator metodologiaMediator;
    private final ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

    public RegraLiberacaoMatrizValidacao(Matriz matriz, MetodologiaMediator metodologiaMediator) {
        this.matriz = matriz;
        this.metodologiaMediator = metodologiaMediator;
    }

    public void validar() {

        if (EstadoMatrizEnum.VIGENTE.equals(matriz.getEstadoMatriz())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_001),
                    EstadoMatrizEnum.VIGENTE.equals(matriz.getEstadoMatriz()));
        } else if (EstadoCicloEnum.COREC.equals(matriz.getCiclo().getEstadoCiclo().getEstado())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_015),
                    EstadoCicloEnum.COREC.equals(matriz.getCiclo().getEstadoCiclo().getEstado()));
        } else {

            ParametroPeso maiorPeso =
                    metodologiaMediator.buscarMaiorPesoMetodologia(matriz.getCiclo().getMetodologia());
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_002),
                    matriz.getUnidadeCorporativa() == null);
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_008),
                    matriz.getUnidadeCorporativa() != null
                            && !matriz.getUnidadeCorporativa().getParametroPeso().getPk().equals(maiorPeso.getPk()));

            if (matriz.getPercentualCorporativoInt() == 0 && matriz.getPercentualNegocioInt() == 100) {
                String msgCoorpotativa =
                        "Para bloco corporativo com participação 0%, exclua atividades e ARCs associados.";
                if (!Util.isNuloOuVazio(matriz.getAtividadesCorporativa())) {
                    SisapsUtil.adicionarErro(erros, new ErrorMessage(msgCoorpotativa));
                }
                validarNegocio(maiorPeso);
            } else if (matriz.getPercentualNegocioInt() == 0 && matriz.getPercentualCorporativoInt() == 100) {
                String msgNegocio =
                        "Para bloco de negócio com participação 0%, exclua unidades, atividades e ARCs associados.";
                if (!Util.isNuloOuVazio(matriz.getAtividadesNegocio())
                        || !Util.isNuloOuVazio(matriz.getUnidadesNegocio())) {
                    SisapsUtil.adicionarErro(erros, new ErrorMessage(msgNegocio));
                }
                validarCorporativo(maiorPeso);
            } else {
                validarCorporativo(maiorPeso);
                validarNegocio(maiorPeso);
            }

        }
        SisapsUtil.lancarNegocioException(erros);
    }

    private void validarCorporativo(ParametroPeso maiorPeso) {
        if (CollectionUtils.isEmpty(matriz.getAtividadesCorporativa())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_003));
        } else {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_006),
                    !existeAtividadeComMaiorPeso(matriz.getAtividadesCorporativa(), maiorPeso.getPk()));
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_011),
                    !atividadePossuiArcComPesoMax(matriz.getAtividadesCorporativa(), maiorPeso.getPk()));
        }
    }

    private void validarNegocio(ParametroPeso maiorPeso) {
        if (CollectionUtils.isEmpty(matriz.getAtividadesNegocio())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_004));
        } else {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_007),
                    !existeAtividadeComMaiorPeso(matriz.getAtividadesNegocio(), maiorPeso.getPk()));
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_012),
                    !atividadePossuiArcComPesoMax(matriz.getAtividadesNegocio(), maiorPeso.getPk()));
        }

        if (CollectionUtils.isNotEmpty(matriz.getUnidadesNegocio())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_005),
                    existeUnidadeSemAtividadeAssociada(matriz.getUnidadesNegocio()));
            SisapsUtil
                    .adicionarErro(
                            erros,
                            new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_009),
                            !(existeUnidadeComMaiorPeso(matriz.getUnidadesNegocio(), maiorPeso.getPk()) || existeAtividadeComMaiorPeso(
                                    matriz.getAtividadesMatriz(), maiorPeso.getPk())));
            if (CollectionUtils.isNotEmpty(matriz.getAtividadesNegocio())
                    && existeAtividadeAssociadaAUnidade(matriz.getUnidadesNegocio())
                    && !unidadePossuiAtividadeComPesoMax(matriz.getUnidadesNegocio(), maiorPeso.getPk())) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_MATRIZ_ERRO_010));
            }
        }
    }

    private boolean existeUnidadeComMaiorPeso(List<Unidade> unidades, Integer idMaiorPeso) {
        if (CollectionUtils.isNotEmpty(unidades)) {
            for (Unidade unidade : unidades) {
                if (unidade.getParametroPeso().getPk().equals(idMaiorPeso)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeAtividadeComMaiorPeso(List<Atividade> atividades, Integer idMaiorPeso) {
        if (CollectionUtils.isNotEmpty(atividades)) {
            for (Atividade atividade : atividades) {
                if (atividade.getParametroPeso().getPk().equals(idMaiorPeso)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeArcComMaiorPeso(List<CelulaRiscoControle> celulasRiscoControle, Integer idMaiorPeso) {
        for (CelulaRiscoControle celula : celulasRiscoControle) {
            if (celula.getParametroPeso().getPk().equals(idMaiorPeso)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeUnidadeSemAtividadeAssociada(List<Unidade> unidades) {
        if (CollectionUtils.isNotEmpty(unidades)) {
            for (Unidade unidade : unidades) {
                if (CollectionUtils.isEmpty(unidade.getAtividades())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeAtividadeAssociadaAUnidade(List<Unidade> unidades) {
        if (CollectionUtils.isNotEmpty(unidades)) {
            for (Unidade unidade : unidades) {
                if (CollectionUtils.isNotEmpty(unidade.getAtividades())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean unidadePossuiAtividadeComPesoMax(List<Unidade> unidades, Integer idMaiorPeso) {
        boolean retorno = false;
        if (CollectionUtils.isNotEmpty(unidades)) {
            for (Unidade unidade : unidades) {
                if (existeAtividadeComMaiorPeso(unidade.getAtividades(), idMaiorPeso)) {
                    retorno = true;
                } else {
                    return false;
                }
            }
        }
        return retorno;
    }

    private boolean atividadePossuiArcComPesoMax(List<Atividade> atividades, Integer idMaiorPeso) {
        boolean retorno = false;
        if (CollectionUtils.isNotEmpty(atividades)) {
            for (Atividade atividade : atividades) {
                if (existeArcComMaiorPeso(atividade.getCelulasRiscoControle(), idMaiorPeso)) {
                    retorno = true;
                } else {
                    return false;
                }
            }
        }
        return retorno;
    }

}