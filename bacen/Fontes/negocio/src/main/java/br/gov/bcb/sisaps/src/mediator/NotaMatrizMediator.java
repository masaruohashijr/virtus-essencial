package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.NotaMatrizDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.validacao.RegraNotaMatrizSalvarValidacaoCampos;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class NotaMatrizMediator {

    private static final String REPLACEMENT = "";
    private static final String ATUALIZADO_POR = "Atualizado por ";
    private static final String REGEX = "<.*?>";
    private static final String SEM_ALTERACOES_SALVAS = "Sem alterações salvas.";

    @Autowired
    private NotaMatrizDAO notaMatrizDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    @Autowired
    private CicloMediator cicloMediator;

    public static NotaMatrizMediator get() {
        return SpringUtils.get().getBean(NotaMatrizMediator.class);
    }

    public NotaMatriz getUltimaNotaMatriz(Matriz matriz) {
        return matriz == null ? null : notaMatrizDAO.getUltimaNotaMatriz(matriz);
    }

    public NotaMatriz buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return notaMatrizDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    @Transactional
    public void criarNotaMatrizNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        NotaMatriz notaMatrizCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        if (notaMatrizCicloAtual != null) {
            NotaMatriz novaNotaMatriz = new NotaMatriz();
            novaNotaMatriz.setMatriz(novoCiclo.getMatriz());
            novaNotaMatriz.setJustificativaNota(notaMatrizCicloAtual.getJustificativaNota());
            novaNotaMatriz.setNotaFinalMatriz(notaMatrizCicloAtual.getNotaFinalMatriz());
            novaNotaMatriz.setOperadorAtualizacao(notaMatrizCicloAtual.getOperadorAtualizacao());
            novaNotaMatriz.setUltimaAtualizacao(notaMatrizCicloAtual.getUltimaAtualizacao());
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novaNotaMatriz,
                    TipoObjetoVersionadorEnum.NOTA_MATRIZ);
            novaNotaMatriz.setAlterarDataUltimaAtualizacao(false);
            notaMatrizDAO.save(novaNotaMatriz);
        }
    }

    public List<String> notaAjustada(Matriz matriz, PerfilRisco perfilRisco, PerfilAcessoEnum perfilAcessoEnum) {
        return notaAjustada(matriz, perfilRisco, perfilAcessoEnum, false);
    }

    public List<String> notaAjustada(Matriz matriz, PerfilRisco perfilRisco, PerfilAcessoEnum perfilAcessoEnum,
            boolean isAtaCorec) {
        List<String> notas = new ArrayList<String>();
        PerfilRisco perfilRiscoM = perfilRisco;
        if (matriz != null) {
            Ciclo cicloInicializado = null;
            if (matriz.getCiclo() != null) {
                cicloInicializado = cicloMediator.load(matriz.getCiclo());
            }
            NotaMatriz notaSupervisor;
            AjusteCorec notaCorec;
            if (perfilRisco == null) {
                perfilRiscoM = perfilRiscoMediator.obterPerfilRiscoAtual(cicloInicializado.getPk());
            }
            NotaMatriz notaMatriz = PerfilRiscoMediator.get().getNotaMatrizPerfilRisco(perfilRiscoM);
            if (cicloAndamentoCorecDiferenteSupervisor(matriz, perfilAcessoEnum)) {
                if (notaMatriz == null && !isAtaCorec) {
                    notaCorec = notaCicloAnterior(cicloInicializado);
                    preencherListaNotaCorec(notaCorec, notas);
                } else {
                    notaSupervisor = notaMatriz;
                    preencherListaNotaJustificativaSupervisor(notaSupervisor, notas);
                }
            } else {
                PerfilRisco perfilRiscoAtual = perfilRiscoMediator.obterPerfilRiscoAtual(cicloInicializado.getPk());
                if (!perfilRiscoM.getPk().equals(perfilRiscoAtual.getPk())) {
                    if (notaMatriz == null && !isAtaCorec) {
                        notaCorec = notaCicloAnterior(cicloInicializado);
                        preencherListaNotaCorec(notaCorec, notas);
                    } else {
                        preencherListaNotaJustificativaSupervisor(notaMatriz, notas);
                    }
                } else {
                    notaCorec = notaCicloAtual(cicloInicializado);
                    if (notaCorec.getPk() != null && !isAtaCorec) {
                        preencherListaNotaCorec(notaCorec, notas);
                    } else {
                        preencherListaNotaJustificativaSupervisor(notaMatriz, notas);
                    }
                }
            }
        }
        return notas;
    }

    private void preencherListaNotaJustificativaSupervisor(NotaMatriz nota, List<String> listaNotaJustificativa) {
        if (nota == null || (nota != null && nota.getNotaFinalMatriz() == null)) {
            listaNotaJustificativa.add("");
            listaNotaJustificativa.add("");
        } else {
            if (nota.getNotaFinalMatriz().getIsNotaElemento() == null
                    || nota.getNotaFinalMatriz().getIsNotaElemento().booleanValue()) {
                listaNotaJustificativa.add(nota.getNotaFinalMatriz().getDescricaoValor());
            } else {
                listaNotaJustificativa.add(nota.getNotaFinalMatriz().getDescricao());
            }
            listaNotaJustificativa.add(nota.getJustificativaNota());
        }
    }

    private void preencherListaNotaCorec(AjusteCorec nota, List<String> mapNota) {
        if (nota.getNotaQualitativa() == null) {
            mapNota.add("");
        } else {
            if (nota.getNotaQualitativa().getIsNotaElemento() == null
                    || nota.getNotaQualitativa().getIsNotaElemento().booleanValue()) {
                mapNota.add(nota.getNotaQualitativa().getDescricaoValor());
            } else {
                mapNota.add(nota.getNotaQualitativa().getDescricao());
            }
        }
        mapNota.add(Constantes.COREC);
    }

    @Transactional(readOnly = true)
    public AjusteCorec notaCicloAtual(Ciclo ciclo) {
        AjusteCorec notaCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo.getPk());
        return notaCorec == null ? new AjusteCorec() : notaCorec;
    }

    @Transactional(readOnly = true)
    public AjusteCorec notaCicloAnterior(Ciclo ciclo) {
        Ciclo cicloAnterior =
                cicloMediator.consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null && !ciclo.getPk().equals(cicloAnterior.getPk())) {
            AjusteCorec notaCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            return notaCorec == null ? new AjusteCorec() : notaCorec;
        } else {
            return new AjusteCorec();
        }
    }

    public boolean cicloAndamentoCorecDiferenteSupervisor(Matriz matriz, PerfilAcessoEnum perfilAcessoEnum) {
        Ciclo cicloInicializado = null;
        if (matriz != null && matriz.getCiclo() != null) {
            cicloInicializado = cicloMediator.load(matriz.getCiclo());
        }
        boolean diferenteDeSupervisorEGerente = !perfilAcessoEnum.equals(PerfilAcessoEnum.SUPERVISOR)
                && !perfilAcessoEnum.equals(PerfilAcessoEnum.GERENTE);
        return matriz != null && (((cicloMediator.cicloCorec(cicloInicializado)) && diferenteDeSupervisorEGerente)
                || cicloMediator.cicloEmAndamento(cicloInicializado));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void incluirNovaNotaMatriz(Matriz matriz, Ciclo ciclo, NotaMatriz notaAnterior) {

        if (notaAnterior != null) {
            perfilRiscoMediator.excluirVersaoDoPerfilRisco(perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk()),
                    notaAnterior.getVersaoPerfilRisco());
        }

        NotaMatriz novaNota = new NotaMatriz();
        novaNota.setMatriz(matriz);
        novaNota.setNotaMatrizAnterior(notaAnterior);
        novaNota.setVersaoPerfilRisco(perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(ciclo, novaNota,
                TipoObjetoVersionadorEnum.NOTA_MATRIZ));
        notaMatrizDAO.saveOrUpdate(novaNota);
        notaMatrizDAO.flush();
    }

    public NotaMatriz buscarNotaMatrizRascunho(Matriz matriz) {
        return notaMatrizDAO.buscarNotaMatrizRascunho(matriz);
    }

    @Transactional
    public String salvarNovaNotaMatrizAjustada(NotaMatriz notaMatriz) {
        notaMatrizDAO.saveOrUpdate(notaMatriz);
        return "Nota ajustada salva com sucesso.";
    }

    @Transactional
    public String confirmarNotaMatriz(NotaMatriz nota, String notaCalculadaFinal) {
        new RegraNotaMatrizSalvarValidacaoCampos(nota, notaCalculadaFinal).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(nota.getJustificativaNota())) {
            nota.setJustificativaNota(null);
        }

        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(nota.getMatriz().getCiclo().getPk());
        NotaMatriz notaVigentePerfilAtual = buscarPorPerfilRisco(perfilAtual.getPk());
        PerfilRisco novoPerfilRisco = PerfilRiscoMediator.get()
                .gerarVersaoPerfilRiscoPublicacaoSinteseAQT(nota.getMatriz().getCiclo(), perfilAtual);

        if (notaVigentePerfilAtual != null) {
            novoPerfilRisco.getVersoesPerfilRisco().remove(notaVigentePerfilAtual.getVersaoPerfilRisco());
        }

        VersaoPerfilRisco novaVersaoNota =
                VersaoPerfilRiscoMediator.get().criarVersao(nota, TipoObjetoVersionadorEnum.NOTA_MATRIZ);
        novoPerfilRisco.getVersoesPerfilRisco().add(novaVersaoNota);
        nota.setVersaoPerfilRisco(novaVersaoNota);
        PerfilRiscoMediator.get().saveOrUpdate(novoPerfilRisco);

        notaMatrizDAO.saveOrUpdate(nota);
        notaMatrizDAO.flush();

        duplicarNota(nota);

        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(perfilAtual.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.NOTA_MATRIZ);

        return "Ajuste de nota final confirmado com sucesso.";
    }

    private void duplicarNota(NotaMatriz nota) {
        NotaMatriz notaRascunho = new NotaMatriz();
        notaRascunho.setJustificativaNota(nota.getJustificativaNota());
        notaRascunho.setMatriz(nota.getMatriz());
        notaRascunho.setUltimaAtualizacao(nota.getUltimaAtualizacao());
        notaRascunho.setOperadorAtualizacao(nota.getOperadorAtualizacao());
        notaRascunho.setVersaoPerfilRisco(null);
        notaRascunho.setNotaFinalMatriz(nota.getNotaFinalMatriz());
        notaRascunho.setNotaMatrizAnterior(nota);
        notaMatrizDAO.saveOrUpdate(notaRascunho);
        notaMatrizDAO.flush();
    }

    public boolean isHabilitarbotaoConfirmar(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return compararNotaVigenteRascunho(notaMatrizRascunho, notaMatrizVigente);
    }

    private boolean compararNotaVigenteRascunho(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        if (notaMatrizRascunho == null && notaMatrizVigente == null) {
            return false;
        } else if (notaMatrizRascunho != null && notaMatrizVigente == null) {
            return true;
        } else if (notaMatrizRascunho != null && notaMatrizVigente != null) {
            if (paramentrosDiferentesNulo(notaMatrizRascunho, notaMatrizVigente)
                    && notaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && justificativaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && dataIgual(notaMatrizRascunho, notaMatrizVigente)) {
                return false;
            } else if (notaMatrizRascunho.getPk() != null && notaMatrizRascunho.getNotaFinalMatriz() == null
                    && justificativaVaziaOuNula(notaMatrizRascunho) && notaMatrizVigente.getPk() != null
                    && notaMatrizVigente.getNotaFinalMatriz() == null && justificativaVaziaOuNula(notaMatrizVigente)
                    && dataIgual(notaMatrizRascunho, notaMatrizVigente)) {
                return false;
            } else if (notaMatrizRascunho.getNotaFinalMatriz() == null && justificativaVaziaOuNula(notaMatrizRascunho)
                    && notaMatrizVigente.getNotaFinalMatriz() != null
                    && notaMatrizVigente.getJustificativaNota() != null) {
                return true;
            } else if (notaMatrizVigente.getNotaFinalMatriz() == null && justificativaVaziaOuNula(notaMatrizVigente)
                    && notaMatrizRascunho.getNotaFinalMatriz() != null
                    && notaMatrizRascunho.getJustificativaNota() != null) {
                return true;
            } else if (paramentrosDiferentesNulo(notaMatrizRascunho, notaMatrizVigente)
                    && notaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && !justificativaIgual(notaMatrizRascunho, notaMatrizVigente)) {
                return true;
            } else if (paramentrosDiferentesNulo(notaMatrizRascunho, notaMatrizVigente)
                    && justificativaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && !notaIgual(notaMatrizRascunho, notaMatrizVigente)) {
                return true;
            } else if (paramentrosDiferentesNulo(notaMatrizRascunho, notaMatrizVigente)
                    && notaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && justificativaIgual(notaMatrizRascunho, notaMatrizVigente)
                    && !dataIgual(notaMatrizRascunho, notaMatrizVigente)) {
                return true;
            } else if (notaMatrizRascunho.getPk() == null && notaMatrizRascunho.getNotaFinalMatriz() == null
                    && justificativaVaziaOuNula(notaMatrizRascunho) && notaMatrizVigente.getPk() == null
                    && notaMatrizVigente.getNotaFinalMatriz() == null && justificativaVaziaOuNula(notaMatrizVigente)) {
                return false;
            } else if (paramentrosIguaisNulo(notaMatrizRascunho, notaMatrizVigente)
                    && justificativaNulasIgual(notaMatrizRascunho, notaMatrizVigente)
                    && notaMatrizRascunho.getUltimaAtualizacao() == null
                    && notaMatrizVigente.getUltimaAtualizacao() != null) {
                return false;
            }
        }
        return true;
    }

    private boolean exibirDataHoraRascunho(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        if (dadosIguaisNuloRascunhoVigente(notaMatrizRascunho, notaMatrizVigente)
                || dadosDiferentesNuloRascunhoVigente(notaMatrizRascunho, notaMatrizVigente)) {
            return true;
        }
        return false;
    }

    private boolean dadosDiferentesNuloRascunhoVigente(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return paramentrosDiferentesNulo(notaMatrizRascunho, notaMatrizVigente)
                && notaIgual(notaMatrizRascunho, notaMatrizVigente)
                && justificativaIgual(notaMatrizRascunho, notaMatrizVigente)
                && dataIgual(notaMatrizRascunho, notaMatrizVigente)
                && operadorIgual(notaMatrizRascunho, notaMatrizVigente);
    }

    private boolean dadosIguaisNuloRascunhoVigente(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return paramentrosIguaisNulo(notaMatrizRascunho, notaMatrizVigente)
                && justificativaNulasIgual(notaMatrizRascunho, notaMatrizVigente)
                && dataIgual(notaMatrizRascunho, notaMatrizVigente)
                && operadorIgual(notaMatrizRascunho, notaMatrizVigente);
    }

    private boolean justificativaVaziaOuNula(NotaMatriz notaMatrizRascunho) {
        return notaMatrizRascunho.getJustificativaNota() == null || notaMatrizRascunho.getJustificativaNota().isEmpty();
    }

    private boolean paramentrosDiferentesNulo(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return notaMatrizRascunho.getNotaFinalMatriz() != null && notaMatrizVigente.getNotaFinalMatriz() != null;
    }

    private boolean paramentrosIguaisNulo(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return notaMatrizRascunho.getNotaFinalMatriz() == null && notaMatrizVigente.getNotaFinalMatriz() == null;
    }

    private boolean dataIgual(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return notaMatrizRascunho.getData(notaMatrizRascunho.getUltimaAtualizacao())
                .equals(notaMatrizVigente.getData(notaMatrizVigente.getUltimaAtualizacao()));
    }

    private boolean operadorIgual(NotaMatriz notaMatrizVigente, NotaMatriz notaMatrizRascunho) {
        return notaMatrizVigente.getOperadorAtualizacao().equals(notaMatrizRascunho.getOperadorAtualizacao());
    }

    private boolean justificativaIgual(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return notaMatrizRascunho.getJustificativaNota() != null && notaMatrizVigente.getJustificativaNota() != null
                && notaMatrizVigente.getJustificativaNota().replaceAll(REGEX, REPLACEMENT)
                        .equals(notaMatrizRascunho.getJustificativaNota().replaceAll(REGEX, REPLACEMENT));
    }

    private boolean justificativaNulasIgual(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return (notaMatrizRascunho.getJustificativaNota() == null && notaMatrizVigente.getJustificativaNota() == null)
                || (notaMatrizRascunho.getJustificativaNota().equals(REPLACEMENT)
                        && notaMatrizVigente.getJustificativaNota().equals(REPLACEMENT));
    }

    private boolean notaIgual(NotaMatriz notaMatrizRascunho, NotaMatriz notaMatrizVigente) {
        return notaMatrizVigente.getNotaFinalMatriz().getValor()
                .equals(notaMatrizRascunho.getNotaFinalMatriz().getValor());
    }

    public String dataHoraUsuarioAjusteVigenteCorec(NotaMatriz notaVigente) {
        if (notaVigente.getPk() == null) {
            return REPLACEMENT;
        } else {
            Ciclo ciclo = notaVigente.getMatriz().getCiclo();
            if ((notaVigente.getNotaFinalMatriz() == null && notaVigente.getNomeOperador() == null)
                    && NotaMatrizMediator.get().notaCicloAnterior(ciclo).getNotaQualitativa() != null) {
                return REPLACEMENT;
            }
        }

        return ATUALIZADO_POR + Util.nomeOperador(notaVigente.getOperadorAtualizacao()) + Constantes.EM
                + notaVigente.getData(notaVigente.getUltimaAtualizacao());
    }

    public String ajusteNotaFinal(NotaMatriz notaVigente) {
        String nota = null;
        Ciclo ciclo = notaVigente.getMatriz().getCiclo();
        if (notaVigente.getPk() == null && notaVigente.getNotaFinalMatriz() == null
                && NotaMatrizMediator.get().notaCicloAnterior(ciclo).getNotaQualitativa() != null) {
            nota = NotaMatrizMediator.get().notaCicloAnterior(notaVigente.getMatriz().getCiclo()).getNotaQualitativa()
                    .getDescricao() + " (Corec)";
        } else if (notaVigente.getNotaFinalMatriz() != null) {
            nota = notaVigente.getNotaFinalMatriz().getDescricao();
        }

        return nota;
    }

    public String dataHoraUsuarioAjusteRascunho(NotaMatriz notaRascunho, NotaMatriz notaVigente) {
        if (notaRascunho == null || notaRascunho.getPk() == null) {
            return SEM_ALTERACOES_SALVAS;
        } else if (exibirDataHoraRascunho(notaRascunho, notaVigente)) {
            return SEM_ALTERACOES_SALVAS;
        } else {
            return "Última alteração salva " + Util.nomeOperador(notaRascunho.getOperadorAtualizacao()) + Constantes.EM
                    + notaRascunho.getData(notaRascunho.getUltimaAtualizacao());
        }
    }

    public List<NotaMatriz> buscarNotaMatrizPorCiclo(Integer pkCiclo) {
        return notaMatrizDAO.buscarPorCiclo(pkCiclo);
    }

    @Transactional
    public void atualizarDadosNovaMetodologia(List<NotaMatriz> notas, Metodologia metodologia) {
        for (NotaMatriz nota : notas) {
            if (nota != null) {
                if (nota.getNotaFinalMatriz() != null) {
                    ParametroNota novaNota = ParametroNotaMediator.get().buscarPorDescricao(metodologia,
                            nota.getNotaFinalMatriz().getDescricaoValor());
                    nota.setNotaFinalMatriz(novaNota);
                }
            }
            nota.setAlterarDataUltimaAtualizacao(false);
            notaMatrizDAO.update(nota);
        }
    }

}
