package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AvaliacaoRiscoControleExternoDao;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.AnaliseQuantitativaAQTDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class VerificarPendenciasCorecMediator {

    private static final String NAO_APLICAVEL = "Não aplicável";

    @Autowired
    private AnaliseQuantitativaAQTDAO analiseQuantitativaAQTDAO;

    @Autowired
    private AvaliacaoRiscoControleExternoDao analiseAvaliacaoRiscoControleExternoDao;

    @Autowired
    private AvaliacaoRiscoControleExternoMediator avaliacaoRiscoControleExternoMediator;

    public static VerificarPendenciasCorecMediator get() {
        return SpringUtils.get().getBean(VerificarPendenciasCorecMediator.class);
    }

    public ArrayList<ErrorMessage> mostraAlertaBotaoVerificarPendencia(Ciclo ciclo) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());

        mostraAlertaVerificarPendenciaMatriz(ciclo, erros);
        mostraAlertaVerificarARCPerfilVigente(ciclo, perfilAtual, erros);
        mostraAlertaVerificarPendenciaSintese(ciclo, perfilAtual, erros);
        mostraAlertaVerificarARCAnterior(ciclo, perfilAtual, erros);
        mostraAlertaVerificarPerfilVigenteNotaCorec(ciclo, perfilAtual, erros);
        mostraAlertaVerificarPerfilAtuacaoConclusaoGrauPreocupacao(ciclo, perfilAtual, erros);
        mostraAlertaVerificarPespectivaSituacao(ciclo, perfilAtual, erros);
        mostraAlertaVerificarANEFVigenteSemNota(ciclo, perfilAtual, erros);
        mostraAlertaVerificarANEFRascunhoSemNota(ciclo, erros);
        mostraAlertaVerificarCicloAnterior(ciclo, erros);
        mostraAlertaVerificacaoPendenciaQuadroPosicaoFinanceira(ciclo, perfilAtual, erros);
        mostraAlertaVerficarGrauPreocupacao(perfilAtual, erros);
        mostraAlertaVerficarPespectiva(ciclo, perfilAtual, erros);
        mostraAlertaVerficarNotaFinalQualitativa(ciclo, perfilAtual, erros);
        mostraAlertaVerficarNotaFinalAnaliseQuantitativa(ciclo, perfilAtual, erros);
        mostraAlertaVerficarGrauPreocupacaoGerente(ciclo, erros);
        mostraAlertaVerficarPerfilAtuacaoGerente(ciclo, erros);
        mostraAlertaVerficarConclusaoGerente(ciclo, erros);
        mostraAlertaVerficarPerspectivaGerente(ciclo, erros);
        mostraAlertaVerficarSituacaoGerente(ciclo, erros);
        return erros;

    }

    public ArrayList<String> mensagensAlerta(Ciclo ciclo) {
        ArrayList<String> alertas = new ArrayList<String>();
        mostraAlertaVerificarPercentuaisIguais(ciclo, alertas);
        mostraAlertaVerificarMatrizEsbocada(ciclo, alertas);
        mostraAlertaVerificarGrauPerspectivaConclusaoSituacaoGerente(ciclo, alertas);
        mostraAlertaVerificarPercentuaisIguaisAE(ciclo, alertas);
        return alertas;
    }

    private void mostraAlertaVerificarGrauPerspectivaConclusaoSituacaoGerente(Ciclo ciclo, ArrayList<String> alertas) {

        if (houveAlteracaoPerfilAtuacaoGerente(ciclo)) {
            alertas.add("A alteração no Perfil de atuação de rascunho será perdida no encerramento do ciclo.");
        }

        if (houveAlteracaoConclusaoGerente(ciclo)) {
            alertas.add("A alteração na Conclusão de rascunho será perdida no encerramento do ciclo.");
        }

        if (houveAlteracaoPerspectivaGerente(ciclo)) {
            alertas.add("A alteração na Perspectiva de rascunho será perdida no encerramento do ciclo.");
        }

        if (houveAlteracaoSituacaoGerente(ciclo)) {
            alertas.add("A alteração na Situação de rascunho será perdida no encerramento do ciclo.");
        }

        if (houveAlteracaoGrauPreocupacaoGerente(ciclo)) {
            alertas.add("A alteração na Nota final da ES de rascunho será perdida no encerramento do ciclo.");
        }

    }

    private void mostraAlertaVerificarPercentuaisIguais(Ciclo ciclo, ArrayList<String> erros) {
        if (PesoAQTMediator.get().validarPercentuaisVigenteIguaisRascunhos(ciclo)) {
            erros.add("As alterações dos percentuais da análise econômico-financeira "
                    + "de rascunho  serão perdidas no encerramento do ciclo.");
        }
    }

    private void mostraAlertaVerificarPercentuaisIguaisAE(Ciclo ciclo, ArrayList<String> erros) {
        if (PesoAQTMediator.get().validarPercentuaisVigenteIguaisRascunhosAE(ciclo)) {
            erros.add("As alterações dos percentuais do bloco 'Atividade x Governança' de rascunho serão perdidas no encerramento do ciclo.");
        }
    }

    private void mostraAlertaVerificarCicloAnterior(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null) {
            AnaliseQuantitativaAQTMediator aqtMediator = AnaliseQuantitativaAQTMediator.get();
            String message =
                    "Todos os ANEFs devem ter atualização no ciclo corrente."
                            + " O ANEF vigente não pode ser o mesmo ANEF do Corec anterior.";
            List<AnaliseQuantitativaAQT> listaANEFSRascunho =
                    AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
            for (AnaliseQuantitativaAQT aqt : listaANEFSRascunho) {
                List<AnaliseQuantitativaAQT> listaAnefsConcluidos =
                        analiseQuantitativaAQTDAO.listarANEFConsulta(aqt.getParametroAQT(), ciclo);
                if (listaAnefsConcluidos.size() < 2) {
                    erros.add(new ErrorMessage(message));
                    break;
                } else {
                    verificarAnefsConcluidos(erros, aqtMediator, message, listaAnefsConcluidos);
                }
            }
        }
    }

    private void verificarAnefsConcluidos(ArrayList<ErrorMessage> erros, AnaliseQuantitativaAQTMediator aqtMediator,
            String message, List<AnaliseQuantitativaAQT> listaAnefsConcluidos) {
        int valor;
        for (int i = 0; i < listaAnefsConcluidos.size(); i++) {
            valor = i + 1;
            if (valor < listaAnefsConcluidos.size()) {
                if (listaAnefsConcluidos.get(i).getParametroAQT().getPk()
                        .equals(listaAnefsConcluidos.get(valor).getParametroAQT().getPk())) {
                    if (!aqtMediator.estadoConcluido(listaAnefsConcluidos.get(i).getEstado())
                            || !AnaliseQuantitativaAQTMediator.get().estadoConcluido(
                                    listaAnefsConcluidos.get(valor).getEstado())) {
                        erros.add(new ErrorMessage(message));
                        break;

                    }
                }
            }
        }
    }

    private void mostraAlertaVerificarANEFRascunhoSemNota(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        List<AnaliseQuantitativaAQT> listaANEFSRascunho =
                AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : listaANEFSRascunho) {
            if (!AnaliseQuantitativaAQTMediator.get().estadosPrevistoDesignado(anef.getEstado())) {
                erros.add(new ErrorMessage(
                        "Todos os ANEFs rascunho devem estar em estado \'Previsto\' ou \'Designado\'."));
                break;
            }
        }

    }

    private void mostraAlertaVerificarANEFVigenteSemNota(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        List<AnaliseQuantitativaAQT> listaANEFSVigentes =
                AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(
                        VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilAtual.getPk(),
                                TipoObjetoVersionadorEnum.AQT));
        for (AnaliseQuantitativaAQT anef : listaANEFSVigentes) {
            if (anef.getValorNota() == null && anef.getNotaSupervisor() == null) {
                erros.add(new ErrorMessage("Todos os ANEFs vigentes precisam ter nota. Não podem ter ANEFs \'*A\'."));
                break;
            }
        }
    }

    private void mostraAlertaVerificarPerfilVigenteNotaCorec(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        if (perfilAtual != null && ciclo.getMatriz() != null) {
            String msgErro =
                    "Todos os ARCs vigentes que tiveram ajuste no COREC anterior precisam "
                            + "ser avaliados novamente.";
            List<CelulaRiscoControle> celulasRiscoControle =
                    CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());
            boolean jaAdicionouErro = false;
            AvaliacaoRiscoControle arcExterno = null;
            if (ciclo.getMatriz().getPercentualGovernancaCorporativoInt() > 0) {
            	arcExterno = avaliacaoRiscoControleExternoMediator.buscarArcExternoPerfilAtual(ciclo, perfilAtual);
            }
            if (arcExterno != null && arcExterno.getAvaliacaoRiscoControleVigente() != null
                    && arcExterno.getAvaliacaoRiscoControleVigente().getNotaCorec() != null) {
                erros.add(new ErrorMessage(msgErro));
                jaAdicionouErro = true;
            }
            if (!jaAdicionouErro) {
                for (CelulaRiscoControle celula : celulasRiscoControle) {
                    if ((celula.getArcRisco().getAvaliacaoRiscoControleVigente() != null && celula.getArcRisco()
                            .getAvaliacaoRiscoControleVigente().getNotaCorec() != null)
                            || (celula.getArcControle().getAvaliacaoRiscoControleVigente() != null && celula
                                    .getArcControle().getAvaliacaoRiscoControleVigente().getNotaCorec() != null)) {
                        erros.add(new ErrorMessage(msgErro));
                        break;
                    }
                }
            }
        }
    }

    private void mostraAlertaVerificarARCAnterior(Ciclo ciclo, PerfilRisco perfilAtual, ArrayList<ErrorMessage> erros) {
        if (perfilAtual != null && ciclo.getMatriz() != null) {
            String msgErro = "Todos os ARCs vigentes precisam ter nota. Não podem ter ARCs \'*A\'.";
            List<CelulaRiscoControle> celulasRiscoControle =
                    CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());
            boolean jaAdicionouErro = false;
            AvaliacaoRiscoControle arcExterno = null;
            if (ciclo.getMatriz().getPercentualGovernancaCorporativoInt() > 0) {
            	arcExterno = analiseAvaliacaoRiscoControleExternoDao.buscarUltimoArcExterno(ciclo.getPk());
            }
            if (arcExterno != null && arcExterno.getAvaliacaoRiscoControleVigente() == null) {
                erros.add(new ErrorMessage(msgErro));
                jaAdicionouErro = true;
            }
            if (!jaAdicionouErro) {
                for (CelulaRiscoControle celula : celulasRiscoControle) {
                    if (celula.getArcRisco().getAvaliacaoRiscoControleVigente() == null
                            || celula.getArcControle().getAvaliacaoRiscoControleVigente() == null) {
                        erros.add(new ErrorMessage(msgErro));
                        break;
                    }
                }
            }
        }
    }

    private void mostraAlertaVerificarARCPerfilVigente(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        if (perfilAtual != null && ciclo.getMatriz() != null) {
            String msgErro = "Todos os ARCs rascunho devem estar em estado \'Previsto\' ou \'Designado\'.";
            List<CelulaRiscoControle> celulasRiscoControle =
                    CelulaRiscoControleMediator.get().buscarCelulasPorPerfilRisco(perfilAtual.getPk());
            boolean jaAdicionouErro = false;
            AvaliacaoRiscoControle arcExterno = null;
            if (ciclo.getMatriz().getPercentualGovernancaCorporativoInt() > 0) {
            	arcExterno = avaliacaoRiscoControleExternoMediator.buscarArcExternoPerfilAtual(ciclo, perfilAtual);
            }
            if (arcExterno != null
                    && !AvaliacaoRiscoControleMediator.get().estadoPrevistoDesignado(arcExterno.getEstado())) {
                erros.add(new ErrorMessage(msgErro));
                jaAdicionouErro = true;
            }
            if (!jaAdicionouErro) {
                for (CelulaRiscoControle celula : celulasRiscoControle) {
                    if (!AvaliacaoRiscoControleMediator.get().estadoPrevistoDesignado(celula.getArcRisco().getEstado())
                            || !AvaliacaoRiscoControleMediator.get().estadoPrevistoDesignado(
                                    celula.getArcControle().getEstado())) {
                        erros.add(new ErrorMessage(msgErro));
                        break;
                    }
                }
            }
        }
    }

    private void mostraAlertaVerificarPendenciaMatriz(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        if (ciclo.getMatriz() == null) {
            erros.add(new ErrorMessage("Deve haver uma matriz vigente cadastrada."));
        }
    }

    private void mostraAlertaVerificarPerfilAtuacaoConclusaoGrauPreocupacao(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        PerfilAtuacaoES perfilAtuacao = PerfilAtuacaoESMediator.get().buscarPorPerfilRisco(perfilAtual.getPk());

        if (perfilAtuacao == null || perfilAtuacao.getDocumento() == null) {
            erros.add(new ErrorMessage("Perfil de atuação deve conter texto."));
        }

        ConclusaoES conclusao = ConclusaoESMediator.get().buscarPorPerfilRisco(perfilAtual.getPk());

        if (conclusao == null || conclusao.getDocumento() == null) {
            erros.add(new ErrorMessage("Conclusão deve conter texto."));
        }

        String notaFinal =
                GrauPreocupacaoESMediator.get().getNotaFinal(perfilAtual, PerfilAcessoEnum.SUPERVISOR,
                        ciclo.getMetodologia());

        if (!GrauPreocupacaoESMediator.get().possuiNotaVigente(notaFinal)) {
            erros.add(new ErrorMessage("Nota final da ES deve ter atributo diferente de vazio."));
        }
    }

    private void mostraAlertaVerificarPespectivaSituacao(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        PerspectivaES perspectiva = PerspectivaESMediator.get().buscarPorPerfilRisco(perfilAtual.getPk());

        if (perspectiva == null || perspectiva.getParametroPerspectiva() == null
                || perspectiva.getParametroPerspectiva().getNome().equalsIgnoreCase(NAO_APLICAVEL)) {
            erros.add(new ErrorMessage(
                    "Perspectiva deve ter atributo diferente de vazio e diferente de \'Não Aplicável\'."));
        }

        SituacaoES situacao = SituacaoESMediator.get().buscarPorPerfilRisco(perfilAtual.getPk());

        if (situacao == null || situacao.getParametroSituacao() == null) {
            erros.add(new ErrorMessage("Situação deve ter atributo diferente de vazio."));
        }
    }

    private void mostraAlertaVerificacaoPendenciaQuadroPosicaoFinanceira(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        QuadroPosicaoFinanceira quadroCicloAtual =
                QuadroPosicaoFinanceiraMediator.get().obterUltimaVersaoQuadroVigente(perfilAtual);
        if (quadroCicloAtual == null) {
            erros.add(new ErrorMessage("Deve haver um quadro da posição financeira cadastrado."));
        }

        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());

        if (cicloAnterior != null && quadroCicloAtual != null) {
            String dataBaseCorecAnterior = getDataBaseCorecAnterior(cicloAnterior);
            if (quadroCicloAtual.getCodigoDataBase().compareTo(Integer.valueOf(dataBaseCorecAnterior)) < 1) {
                erros.add(new ErrorMessage(
                        "Data-base do quadro da posição financeira deve ser posterior à data do Corec anterior."));
            }
        }
    }

    private String getDataBaseCorecAnterior(Ciclo cicloAnterior) {
        String dataBaseCorecAnterior = DataUtil.converterDateParaDataBase(cicloAnterior.getDataPrevisaoCorec());
        String mes = dataBaseCorecAnterior.substring(0, 2);
        String ano = dataBaseCorecAnterior.substring(3);
        return ano + mes;
    }

    private void mostraAlertaVerificarPendenciaSintese(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        if (ciclo.getMatriz() != null) {
            List<SinteseDeRisco> listaSinteses = PerfilRiscoMediator.get().getSintesesDeRiscoPerfilRisco(perfilAtual);

            List<ParametroGrupoRiscoControle> gruposSinteseObrigatorios =
                    ParametroGrupoRiscoControleMediator.get().buscarGruposSinteseObrigatorios(ciclo.getMetodologia());

            if (CollectionUtils.isNotEmpty(gruposSinteseObrigatorios)) {
                for (ParametroGrupoRiscoControle grupo : gruposSinteseObrigatorios) {
                    boolean existeSintese = false;
                    if (CollectionUtils.isNotEmpty(listaSinteses)) {
                        for (SinteseDeRisco sintese : listaSinteses) {
                            if (sintese.getParametroGrupoRiscoControle().getPk().equals(grupo.getPk())) {
                                existeSintese = true;
                                break;
                            }
                        }
                    }
                    if (!existeSintese) {
                        erros.add(new ErrorMessage(
                                "Sínteses obrigatórias (mesmo sem ARCs correspondentes na matriz) devem conter texto."));
                        break;
                    }
                }
            }
        }
    }

    private void mostraAlertaVerificarMatrizEsbocada(Ciclo ciclo, ArrayList<String> erros) {
        if (ciclo.getMatriz() != null) {
            Matriz matrizVigente = MatrizCicloMediator.get().getUltimaMatrizCiclo(ciclo);
            Matriz matrizEmEdicao = MatrizCicloMediator.get().getMatrizEmEdicao(ciclo);

            if (matrizVigente != null && matrizEmEdicao != null) {
                String msg = "As alterações da matriz esboçada serão perdidas no encerramento do ciclo.";
                if (validarAtividadeUnidadeCelula(erros, msg, matrizVigente, matrizEmEdicao)) {
                    erros.add(msg);
                }
            }
        }
    }

    //CHECKSTYLE:OFF
    private boolean validarAtividadeUnidadeCelula(ArrayList<String> erros, String msg, Matriz matrizVigente,
            Matriz matrizEmEdicao) {
        boolean pendencia = false;
        if (atividadesDiferentes(matrizVigente, matrizEmEdicao)) {
            pendencia = true;
        } else {
            for (int j = 0; j < matrizVigente.getAtividades().size(); j++) {
                Atividade atividadeVigente = matrizVigente.getAtividades().get(j);
                Atividade atividadeEdicao = matrizEmEdicao.getAtividades().get(j);

                Unidade unidadeVigente = atividadeVigente.getUnidade();
                Unidade unidadeEdicao = atividadeVigente.getUnidade();

                if (pesoCelulaDiferente(atividadeVigente, atividadeEdicao, j)
                        || celulasDiferentes(atividadeVigente, atividadeEdicao)) {
                    pendencia = true;
                    break;
                } else if (pesoAtividadeDiferente(atividadeVigente, atividadeEdicao)
                        || nomeAtividadeDiferente(atividadeVigente, atividadeEdicao)
                        || tipoAtividadeDiferente(atividadeVigente, atividadeEdicao)) {
                    pendencia = true;
                    break;
                } else if (nomeUnidadeDiferente(unidadeVigente, unidadeEdicao)
                        || pesoUnidadeDiferente(unidadeVigente, unidadeEdicao)
                        || unidadeRemovida(unidadeVigente, unidadeEdicao)) {
                    pendencia = true;
                    break;
                } else if (percentualBlocoDiferente(matrizVigente, matrizEmEdicao)) {
                    pendencia = true;
                    break;
                }
            }
        }
        return pendencia;
    }//CHECKSTYLE:ON

    private boolean unidadeRemovida(Unidade unidadeVigente, Unidade unidadeEdicao) {
        return unidadeEdicao == null && unidadeVigente != null;
    }

    private boolean pesoCelulaDiferente(Atividade atividadeVigente, Atividade atividadeEdicao, int j) {
        boolean valor = false;
        if (!atividadeVigente.getCelulasRiscoControle().isEmpty()
                && !atividadeEdicao.getCelulasRiscoControle().isEmpty()) {
            CelulaRiscoControle celVigente = atividadeVigente.getCelulasRiscoControle().get(j);
            CelulaRiscoControle celEdicao = atividadeEdicao.getCelulasRiscoControle().get(j);
            valor =
                    celVigente != null && celEdicao != null
                            && celVigente.getParametroPeso().getValor().equals(celEdicao.getParametroPeso().getValor());
        }
        return valor;

    }

    private boolean percentualBlocoDiferente(Matriz matrizVigente, Matriz matrizEmEdicao) {
        return !matrizEmEdicao.getPercentualNegocio().equals(matrizVigente.getPercentualNegocio())
                || !matrizEmEdicao.getPercentualCorporativo().equals(matrizVigente.getPercentualCorporativo());
    }

    private boolean pesoUnidadeDiferente(Unidade unidadeVigente, Unidade unidadeEdicao) {
        return unidadeVigente != null && unidadeEdicao != null
                && !unidadeEdicao.getParametroPeso().getValor().equals(unidadeEdicao.getParametroPeso().getValor());
    }

    private boolean nomeUnidadeDiferente(Unidade unidadeVigente, Unidade unidadeEdicao) {
        return unidadeVigente != null && unidadeEdicao != null
                && !unidadeEdicao.getNome().equals(unidadeVigente.getNome());
    }

    private boolean celulasDiferentes(Atividade atividadeVigente, Atividade atividadeEdicao) {
        return atividadeVigente.getNome().equals(atividadeEdicao.getNome())
                && atividadeEdicao.getCelulasRiscoControle().size() != atividadeVigente.getCelulasRiscoControle()
                        .size();
    }

    private boolean tipoAtividadeDiferente(Atividade atividadeVigente, Atividade atividadeEdicao) {
        return !atividadeEdicao.getTipoAtividade().equals(atividadeVigente.getTipoAtividade());
    }

    private boolean atividadesDiferentes(Matriz matrizVigente, Matriz matrizEmEdicao) {
        return matrizEmEdicao.getAtividades().size() != matrizVigente.getAtividades().size();
    }

    private boolean nomeAtividadeDiferente(Atividade atividadeVigente, Atividade atividadeEdicao) {
        return !atividadeEdicao.getNome().equals(atividadeVigente.getNome());
    }

    private boolean pesoAtividadeDiferente(Atividade atividadeVigente, Atividade atividadeEdicao) {
        return atividadeVigente != null
                && atividadeEdicao != null
                && !atividadeEdicao.getParametroPeso().getValor()
                        .equals(atividadeVigente.getParametroPeso().getValor());
    }

    private void mostraAlertaVerficarGrauPreocupacao(PerfilRisco perfilRisco, ArrayList<ErrorMessage> erros) {
        if (GrauPreocupacaoESMediator.get().exibirCorecAnteriorNotaFinalES(perfilRisco)) {
            erros.add(new ErrorMessage(
                    "A 'Nota final' da ES deve ser avaliada, pois houve ajuste no COREC anterior."));
        }

        GrauPreocupacaoES grau = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());

        if (grau == null || grau.getPercentualAnef() == null) {
            erros.add(new ErrorMessage(
                    "Os percentuais das análises econômico-financeira e de riscos e controles para cálculo da Nota Final devem ser confirmados"));
        }
    }

    private void mostraAlertaVerficarPespectiva(Ciclo ciclo, PerfilRisco perfilAtual, ArrayList<ErrorMessage> erros) {
        if (PerspectivaESMediator.get().exibirPerspectivaCorec(ciclo)) {
            erros.add(new ErrorMessage(
                    "A 'Perspectiva' da ES deve ser avaliada, pois houve ajuste no COREC anterior."));
        }
    }

    private void mostraAlertaVerficarNotaFinalQualitativa(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null) {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            NotaMatriz notaMatriz = NotaMatrizMediator.get().getUltimaNotaMatriz(ciclo.getMatriz());

            if (ajusteCorec != null && ajusteCorec.getNotaQualitativa() != null && notaMatriz == null) {
                erros.add(new ErrorMessage(
                        "A 'Nota final' qualitativa deve ser avaliada, pois houve ajuste no COREC anterior."));
            }
        }
    }

    private void mostraAlertaVerficarNotaFinalAnaliseQuantitativa(Ciclo ciclo, PerfilRisco perfilAtual,
            ArrayList<ErrorMessage> erros) {
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null) {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            NotaAjustadaAEF notaSupervisor = NotaAjustadaAEFMediator.get().buscarNotaAjustadaAEF(ciclo, perfilAtual);

            if (ajusteCorec != null && ajusteCorec.getNotaQuantitativa() != null && notaSupervisor == null) {
                erros.add(new ErrorMessage("A 'Nota final' da análise econômico-financeira deve ser avaliada, "
                        + "pois houve ajuste no COREC anterior."));
            }
        }
    }

    private void mostraAlertaVerficarSituacaoGerente(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        SituacaoES situacao = SituacaoESMediator.get().buscarSituacaoESRascunho(ciclo.getPk());
        if (situacao != null && SimNaoEnum.SIM.equals(situacao.getPendente())) {
            erros.add(new ErrorMessage("A alteração na 'Situação' deve ser confirmada pelo gerente."));
        }

    }

    private void mostraAlertaVerficarPerspectivaGerente(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        PerspectivaES perspectiva = PerspectivaESMediator.get().buscarPerspectivaESRascunho(ciclo.getPk());
        if (perspectiva != null && SimNaoEnum.SIM.equals(perspectiva.getPendente())) {
            erros.add(new ErrorMessage("A alteração na 'Perspectiva' deve ser confirmada pelo gerente."));
        }

    }

    private void mostraAlertaVerficarConclusaoGerente(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        ConclusaoES conclusao = ConclusaoESMediator.get().buscarConclusaoESRascunho(ciclo.getPk());
        if (conclusao != null && SimNaoEnum.SIM.equals(conclusao.getPendente())) {
            erros.add(new ErrorMessage("A alteração na 'Conclusão' deve ser confirmada pelo gerente."));
        }
    }

    private void mostraAlertaVerficarPerfilAtuacaoGerente(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        PerfilAtuacaoES perfilAtuacao = PerfilAtuacaoESMediator.get().buscarPerfilAtuacaoESRascunho(ciclo.getPk());
        if (perfilAtuacao != null && SimNaoEnum.SIM.equals(perfilAtuacao.getPendente())) {
            erros.add(new ErrorMessage("A alteração no 'Perfil de atuação' deve ser confirmado pelo gerente."));
        }
    }

    private void mostraAlertaVerficarGrauPreocupacaoGerente(Ciclo ciclo, ArrayList<ErrorMessage> erros) {
        GrauPreocupacaoES grauPreocupacao =
                GrauPreocupacaoESMediator.get().buscarGrauPreocupacaoESRascunho(ciclo.getPk());
        if (grauPreocupacao != null && SimNaoEnum.SIM.equals(grauPreocupacao.getPendente())) {
            erros.add(new ErrorMessage("A alteração na 'Nota final' da ES deve ser confirmada pelo gerente."));
        }

    }

    private boolean houveAlteracaoGrauPreocupacaoGerente(Ciclo ciclo) {
        GrauPreocupacaoES grauPreocupacao =
                GrauPreocupacaoESMediator.get().buscarGrauPreocupacaoESRascunho(ciclo.getPk());
        return grauPreocupacao != null && grauPreocupacao.getPendente() == null;
    }

    private boolean houveAlteracaoPerspectivaGerente(Ciclo ciclo) {
        PerspectivaES perspectiva = PerspectivaESMediator.get().buscarPerspectivaESRascunho(ciclo.getPk());
        return perspectiva != null && perspectiva.getPendente() != null
                && SimNaoEnum.NAO.equals(perspectiva.getPendente());
    }

    private boolean houveAlteracaoConclusaoGerente(Ciclo ciclo) {
        ConclusaoES conclusao = ConclusaoESMediator.get().buscarConclusaoESRascunho(ciclo.getPk());
        return conclusao != null && conclusao.getPendente() != null && SimNaoEnum.NAO.equals(conclusao.getPendente());
    }

    private boolean houveAlteracaoPerfilAtuacaoGerente(Ciclo ciclo) {
        PerfilAtuacaoES perfilAtuacao = PerfilAtuacaoESMediator.get().buscarPerfilAtuacaoESRascunho(ciclo.getPk());
        return perfilAtuacao != null && perfilAtuacao.getPendente() != null
                && SimNaoEnum.NAO.equals(perfilAtuacao.getPendente());
    }

    private boolean houveAlteracaoSituacaoGerente(Ciclo ciclo) {
        SituacaoES situacao = SituacaoESMediator.get().buscarSituacaoESRascunho(ciclo.getPk());
        return situacao != null && situacao.getPendente() != null && SimNaoEnum.NAO.equals(situacao.getPendente());
    }

}
