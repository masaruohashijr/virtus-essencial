package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.NotaAjustadaAEFDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.EventoConsolidadoMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraNotaAjustadaAEF;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class NotaAjustadaAEFMediator {

    private static final String REPLACEMENT = "";

    private static final String REGEX = "<.*?>";

    private static final String SEM_ALTERACOES_SALVAS = "Sem alterações salvas.";

    private static final String ATUALIZADO_POR = "Atualizado por ";

    @Autowired
    private NotaAjustadaAEFDAO notaAjustadaAEFDAO;

    @Autowired
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static NotaAjustadaAEFMediator get() {
        return SpringUtils.get().getBean(NotaAjustadaAEFMediator.class);
    }

    public NotaAjustadaAEF buscarNotaAjustadaAEF(Ciclo ciclo, PerfilRisco perfilRisco) {
        VersaoPerfilRisco versao =
                versaoPerfilRiscoMediator.buscarVersaoPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.NOTA_AQT);
        return notaAjustadaAEFDAO.buscarNotaAjustadaAEF(ciclo, versao);
    }

    public NotaAjustadaAEF buscarNotaAjustadaRascunho(Ciclo ciclo) {
        return notaAjustadaAEFDAO.buscarNotaAjustadaAEFRascunho(ciclo);
    }

    @Transactional
    public void criarNotaAjustadaAEFNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Copiar a nota ajustada da análise econômico-financeira pelo supervisor, assim 
        // como sua justificativa, se existir do perfil de risco vigente do ciclo Corec e 
        // colocar no perfil vigente do novo ciclo.
        NotaAjustadaAEF notaAjustadaAEFCicloAtual =
                buscarNotaAjustadaAEF(perfilRiscoCicloAtual.getCiclo(), perfilRiscoCicloAtual);
        if (notaAjustadaAEFCicloAtual != null) {
            NotaAjustadaAEF novaNotaAjustadaAEF = new NotaAjustadaAEF();
            novaNotaAjustadaAEF.setCiclo(novoCiclo);
            novaNotaAjustadaAEF.setJustificativa(notaAjustadaAEFCicloAtual.getJustificativa());
            novaNotaAjustadaAEF.setParamentroNotaAQT(notaAjustadaAEFCicloAtual.getParamentroNotaAQT());
            novaNotaAjustadaAEF.setOperadorAtualizacao(notaAjustadaAEFCicloAtual.getOperadorAtualizacao());
            novaNotaAjustadaAEF.setUltimaAtualizacao(notaAjustadaAEFCicloAtual.getUltimaAtualizacao());
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novaNotaAjustadaAEF,
                    TipoObjetoVersionadorEnum.NOTA_AQT);
            novaNotaAjustadaAEF.setAlterarDataUltimaAtualizacao(false);
            notaAjustadaAEFDAO.save(novaNotaAjustadaAEF);
        }
    }

    public String ajusteNotaFinal(NotaAjustadaAEF notaVigente) {
        String nota = null;
        if (notaVigente.getPk() == null && notaVigente.getParamentroNotaAQT() == null
                && NotaMatrizMediator.get().notaCicloAnterior(notaVigente.getCiclo()).getNotaQuantitativa() != null) {
            nota =
                    NotaMatrizMediator.get().notaCicloAnterior(notaVigente.getCiclo()).getNotaQuantitativa()
                            .getDescricao()
                            + " (Corec)";
        } else if (notaVigente.getParamentroNotaAQT() != null) {
            nota = notaVigente.getParamentroNotaAQT().getDescricao();
        }

        return nota;
    }

    public String dataHoraUsuarioAjusteVigenteCorec(NotaAjustadaAEF notaVigente) {
        if (notaVigente.getPk() == null) {
            return REPLACEMENT;
        } else if ((notaVigente.getParamentroNotaAQT() == null && notaVigente.getNomeOperador() == null)
                && NotaMatrizMediator.get().notaCicloAnterior(notaVigente.getCiclo()).getNotaQuantitativa() != null) {
            return REPLACEMENT;
        }

        return ATUALIZADO_POR + Util.nomeOperador(notaVigente.getOperadorAtualizacao()) + Constantes.EM
                + notaVigente.getData(notaVigente.getUltimaAtualizacao());
    }

    public String dataHoraUsuarioAjusteRascunho(NotaAjustadaAEF notaRascunho, NotaAjustadaAEF notaVigente) {
        if (notaRascunho == null || notaRascunho.getPk() == null) {
            return SEM_ALTERACOES_SALVAS;
        } else if (exibirDataHoraRascunho(notaRascunho, notaVigente)) {
            return SEM_ALTERACOES_SALVAS;
        } else {
            return ATUALIZADO_POR + Util.nomeOperador(notaRascunho.getOperadorAtualizacao()) + Constantes.EM
                    + notaRascunho.getData(notaRascunho.getUltimaAtualizacao());
        }
    }

    @Transactional
    public String salvarNotaAjustadaAEF(NotaAjustadaAEF nota) {
        notaAjustadaAEFDAO.saveOrUpdate(nota);
        return "Nota ajustada salva com sucesso.";
    }

    @Transactional
    public String confirmarNotaAjustadaAEF(NotaAjustadaAEF nota, String notaCalculada) {
        new RegraNotaAjustadaAEF(nota, notaCalculada).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(nota.getJustificativa())) {
            nota.setJustificativa(null);
        }
        duplicarNota(nota);

        PerfilRisco perfilAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(nota.getCiclo().getPk());
        NotaAjustadaAEF notaVigentePerfilAtual = buscarNotaAjustadaAEF(nota.getCiclo(), perfilAtual);
        PerfilRisco novoPerfilRisco =
                PerfilRiscoMediator.get().gerarVersaoPerfilRiscoPublicacaoSinteseAQT(nota.getCiclo(), perfilAtual);

        if (notaVigentePerfilAtual != null) {
            novoPerfilRisco.getVersoesPerfilRisco().remove(notaVigentePerfilAtual.getVersaoPerfilRisco());
        }

        VersaoPerfilRisco novaVersaoNota =
                VersaoPerfilRiscoMediator.get().criarVersao(nota, TipoObjetoVersionadorEnum.NOTA_AQT);
        novoPerfilRisco.getVersoesPerfilRisco().add(novaVersaoNota);
        nota.setVersaoPerfilRisco(novaVersaoNota);
        PerfilRiscoMediator.get().saveOrUpdate(novoPerfilRisco);

        notaAjustadaAEFDAO.update(nota);
        notaAjustadaAEFDAO.flush();

        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(perfilAtual.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.NOTA_GERAL_ANEF);

        return "Ajuste de nota final confirmado com sucesso.";
    }

    @Transactional
    private void duplicarNota(NotaAjustadaAEF nota) {
        NotaAjustadaAEF notaRascunho = new NotaAjustadaAEF();
        notaRascunho.setParamentroNotaAQT(nota.getParamentroNotaAQT());
        notaRascunho.setJustificativa(nota.getJustificativa());
        notaRascunho.setCiclo(nota.getCiclo());
        notaRascunho.setUltimaAtualizacao(nota.getUltimaAtualizacao());
        notaRascunho.setOperadorAtualizacao(nota.getOperadorAtualizacao());
        notaRascunho.setVersaoPerfilRisco(null);
        notaAjustadaAEFDAO.saveOrUpdate(notaRascunho);
        notaAjustadaAEFDAO.flush();
    }

    public boolean isHabilitarbotaoConfirmar(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return compararNotaVigenteRascunho(notaAjustadaAEFRascunho, notaAjustadaAEFVigente);
    }

    private boolean compararNotaVigenteRascunho(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        if (notaAjustadaAEFRascunho == null && notaAjustadaAEFVigente == null) {
            return false;
        } else if (notaAjustadaAEFRascunho != null && notaAjustadaAEFVigente == null) {
            return true;
        } else if (notaAjustadaAEFRascunho != null && notaAjustadaAEFVigente != null) {
            if (paramentrosDiferentesNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && notaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && justificativaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && dataIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)) {
                return false;
            } else if (notaAjustadaAEFRascunho.getPk() != null
                    && notaAjustadaAEFRascunho.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFRascunho) && notaAjustadaAEFVigente.getPk() != null
                    && notaAjustadaAEFVigente.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFVigente)) {
                return false;
            } else if (notaAjustadaAEFRascunho.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFRascunho)
                    && notaAjustadaAEFVigente.getParamentroNotaAQT() != null
                    && notaAjustadaAEFVigente.getJustificativa() != null) {
                return true;
            } else if (notaAjustadaAEFVigente.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFVigente)
                    && notaAjustadaAEFRascunho.getParamentroNotaAQT() != null
                    && notaAjustadaAEFRascunho.getJustificativa() != null) {
                return true;
            } else if (paramentrosDiferentesNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && notaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && !justificativaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)) {
                return true;
            } else if (paramentrosDiferentesNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && justificativaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && !notaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)) {
                return true;
            } else if (paramentrosDiferentesNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && notaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && justificativaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                    && !dataIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)) {
                return true;
            } else if (notaAjustadaAEFRascunho.getPk() == null
                    && notaAjustadaAEFRascunho.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFRascunho) && notaAjustadaAEFVigente.getPk() == null
                    && notaAjustadaAEFVigente.getParamentroNotaAQT() == null
                    && justificativaVaziaOuNula(notaAjustadaAEFVigente)) {
                return false;
            }
        }
        return true;
    }

    private boolean exibirDataHoraRascunho(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        if (dadosIguaisNuloRascunhoVigente(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                || dadosDiferentesNuloRascunhoVigente(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)) {
            return true;
        }
        return false;
    }

    private boolean dadosDiferentesNuloRascunhoVigente(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return paramentrosDiferentesNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && notaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && justificativaIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && dataIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && operadorIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente);
    }

    private boolean dadosIguaisNuloRascunhoVigente(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return paramentrosIguaisNulo(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && justificativaNulasIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && dataIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente)
                && operadorIgual(notaAjustadaAEFRascunho, notaAjustadaAEFVigente);
    }

    private boolean justificativaVaziaOuNula(NotaAjustadaAEF notaAjustadaAEFRascunho) {
        return notaAjustadaAEFRascunho.getJustificativa() == null
                || notaAjustadaAEFRascunho.getJustificativa().isEmpty();
    }

    private boolean paramentrosDiferentesNulo(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return notaAjustadaAEFRascunho.getParamentroNotaAQT() != null
                && notaAjustadaAEFVigente.getParamentroNotaAQT() != null;
    }

    private boolean paramentrosIguaisNulo(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return notaAjustadaAEFRascunho.getParamentroNotaAQT() == null
                && notaAjustadaAEFVigente.getParamentroNotaAQT() == null;
    }

    private boolean dataIgual(NotaAjustadaAEF notaAjustadaAEFRascunho, NotaAjustadaAEF notaAjustadaAEFVigente) {
        return notaAjustadaAEFRascunho.getData(notaAjustadaAEFRascunho.getUltimaAtualizacao()).equals(
                notaAjustadaAEFVigente.getData(notaAjustadaAEFVigente.getUltimaAtualizacao()));
    }

    private boolean operadorIgual(NotaAjustadaAEF notaAjustadaAEFVigente, NotaAjustadaAEF notaAjustadaAEFRascunho) {
        return notaAjustadaAEFVigente.getOperadorAtualizacao().equals(notaAjustadaAEFRascunho.getOperadorAtualizacao());
    }

    private boolean justificativaIgual(NotaAjustadaAEF notaAjustadaAEFRascunho, NotaAjustadaAEF notaAjustadaAEFVigente) {
        return notaAjustadaAEFRascunho.getJustificativa() != null
                && notaAjustadaAEFVigente.getJustificativa() != null
                && notaAjustadaAEFVigente.getJustificativa().replaceAll(REGEX, REPLACEMENT)
                        .equals(notaAjustadaAEFRascunho.getJustificativa().replaceAll(REGEX, REPLACEMENT));
    }

    private boolean justificativaNulasIgual(NotaAjustadaAEF notaAjustadaAEFRascunho,
            NotaAjustadaAEF notaAjustadaAEFVigente) {
        return (notaAjustadaAEFRascunho.getJustificativa() == null && notaAjustadaAEFVigente.getJustificativa() == null)
                || (notaAjustadaAEFRascunho.getJustificativa().equals(REPLACEMENT) && notaAjustadaAEFVigente
                        .getJustificativa().equals(REPLACEMENT));
    }

    private boolean notaIgual(NotaAjustadaAEF notaAjustadaAEFRascunho, NotaAjustadaAEF notaAjustadaAEFVigente) {
        return notaAjustadaAEFVigente.getParamentroNotaAQT().getValor()
                .equals(notaAjustadaAEFRascunho.getParamentroNotaAQT().getValor());
    }
    
    public List<NotaAjustadaAEF> buscarNotaAjustadaAEFPorCiclo(Integer pkCiclo) {
        return notaAjustadaAEFDAO.buscarNotaAjustadaAEFPorCiclo(pkCiclo);
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<NotaAjustadaAEF> notas, Metodologia metodologia) {
        for (NotaAjustadaAEF nota : notas) {
            if (nota != null) {
                if (nota.getParamentroNotaAQT() != null) {
                    ParametroNotaAQT novaNota =
                            ParametroNotaAQTMediator.get().buscarPorDescricao(metodologia,
                                    nota.getParamentroNotaAQT().getDescricaoValor());
                    nota.setParamentroNotaAQT(novaNota);
                }
            }
            nota.setAlterarDataUltimaAtualizacao(false);
            notaAjustadaAEFDAO.update(nota);
        }
    }

}
