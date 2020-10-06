package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AvaliacaoARCDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseARCSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoARCInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class AvaliacaoARCMediator {

    @Autowired
    private AvaliacaoARCDAO avaliacaoARCDAO;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public static AvaliacaoARCMediator get() {
        return SpringUtils.get().getBean(AvaliacaoARCMediator.class);
    }

    public AvaliacaoARC loadPK(Integer pk) {
        return avaliacaoARCDAO.buscarArcPorPK(pk);
    }

    public AvaliacaoARC buscarPorIdArcEtipo(Integer pk, PerfisNotificacaoEnum tipo) {
        return avaliacaoARCDAO.buscarPorIdArcEtipo(pk, tipo);
    }

    @Transient
    public String getAvaliacaoArcDescricaoValor(Integer pk) {
        AvaliacaoARC avaliacaoARC;
        AvaliacaoARC supervisor = buscarPorIdArcEtipo(pk, PerfisNotificacaoEnum.SUPERVISOR);

        if (supervisor == null || (supervisor != null && supervisor.getParametroNota() == null)) {
            avaliacaoARC = buscarPorIdArcEtipo(pk, PerfisNotificacaoEnum.INSPETOR);
        } else {
            avaliacaoARC = supervisor;
        }

        return avaliacaoARC == null || avaliacaoARC.getParametroNota() == null ? "" : avaliacaoARC.getParametroNota()
                .getDescricaoValor();
    }

    @Transactional
    public void salvarNovaNotaARC(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle, AvaliacaoARC avaliacaoARC) {
        new RegraEdicaoARCInspetorPermissaoAlteracao(ciclo, avaliacaoRiscoControle).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(avaliacaoARC.getJustificativa())) {
            avaliacaoARC.setJustificativa(null);
        }
        incluirExcluirAvaliacaoARC(avaliacaoARC, avaliacaoRiscoControle, PerfisNotificacaoEnum.INSPETOR);
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmEdicao(avaliacaoRiscoControle);
    }

    @Transactional
    public void salvarAvaliacaoARCSupervisor(Ciclo ciclo, AvaliacaoRiscoControle avaliacao, AvaliacaoARC avaliacaoARC) {
        new RegraAnaliseARCSupervisorPermissaoAlteracao(ciclo, avaliacaoARC.getAvaliacaoRiscoControle()).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(avaliacaoARC.getJustificativa())) {
            avaliacaoARC.setJustificativa(null);
        }

        incluirExcluirAvaliacaoARC(avaliacaoARC, avaliacao, PerfisNotificacaoEnum.SUPERVISOR);
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmAnalise(avaliacao);
    }

    private void incluirExcluirAvaliacaoARC(AvaliacaoARC avaliacaoARC, AvaliacaoRiscoControle avaliacaoRiscoControle,
            PerfisNotificacaoEnum perfil) {

        if (avaliacaoARC.getPk() != null && avaliacaoARC.getJustificativa() == null
                && avaliacaoARC.getParametroNota() == null) {
            AvaliacaoARC ava = loadPK(avaliacaoARC.getPk());
            if (ava != null) {
                avaliacaoARCDAO.delete(ava);
                flush();
            }
        } else if (avaliacaoARC.getJustificativa() != null || avaliacaoARC.getParametroNota() != null) {
            AvaliacaoARC ava = loadPK(avaliacaoARC.getPk());
            if (ava == null) {
                ava = avaliacaoARC;
                ava.setAvaliacaoRiscoControle(avaliacaoRiscoControle);
                ava.setPerfil(perfil);
                avaliacaoARCDAO.save(ava);
            } else {
                ava.setJustificativa(avaliacaoARC.getJustificativa());
                ava.setParametroNota(avaliacaoARC.getParametroNota());
                avaliacaoARCDAO.update(ava);
            }
            avaliacaoRiscoControle.getAvaliacoesArc().add(ava);
        }
    }

    @Transactional
    public void incluir(AvaliacaoARC avaliacaoARC) {
        avaliacaoARCDAO.save(avaliacaoARC);
    }

    @Transactional
    public void excluir(AvaliacaoARC avaliacaoARC) {
        avaliacaoARCDAO.delete(avaliacaoARC);
    }

    @Transactional
    public void duplicarAvaliacoesARCConclusaoAnalise(AvaliacaoRiscoControle arc) {
        for (AvaliacaoARC avaliacaoARC : arc.getAvaliacoesArc()) {
            if (avaliacaoARC.getJustificativa() != null) {
                AvaliacaoARC novaAvaliacao = new AvaliacaoARC();
                novaAvaliacao.setAvaliacaoRiscoControle(arc);
                novaAvaliacao.setParametroNota(avaliacaoARC.getParametroNota());
                novaAvaliacao.setJustificativa(avaliacaoARC.getJustificativa());
                novaAvaliacao.setPerfil(avaliacaoARC.getPerfil());
                avaliacaoARCDAO.save(novaAvaliacao);
            }
        }
    }

    @Transactional
    public void copiarDadosAvaliacaoARCAnterior(AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        copiarDadosAvaliacaoInspetor(arcPerfilRiscoVigente, arcAnterior);
        copiarDadosAvaliacaoSupervisor(arcPerfilRiscoVigente, arcAnterior);
    }

    private void copiarDadosAvaliacaoSupervisor(AvaliacaoRiscoControle arcPerfilRiscoVigente,
            AvaliacaoRiscoControle arcAnterior) {
        if (arcAnterior.avaliacaoSupervisor() == null && arcPerfilRiscoVigente.avaliacaoSupervisor() != null) {
            avaliacaoARCDAO.delete(arcPerfilRiscoVigente.avaliacaoSupervisor());
        } else if (arcAnterior.avaliacaoSupervisor() != null && arcPerfilRiscoVigente.avaliacaoSupervisor() == null) {
            AvaliacaoARC avaliacaoARCSupervisor = new AvaliacaoARC();
            copiarDadosAvaliacao(arcPerfilRiscoVigente, arcAnterior.avaliacaoSupervisor(), avaliacaoARCSupervisor);
            avaliacaoARCDAO.save(avaliacaoARCSupervisor);
        } else if (arcAnterior.avaliacaoSupervisor() != null && arcPerfilRiscoVigente.avaliacaoSupervisor() != null) {
            AvaliacaoARC avaliacaoARCInspetor = arcPerfilRiscoVigente.avaliacaoSupervisor();
            copiarDadosAvaliacao(arcPerfilRiscoVigente, arcAnterior.avaliacaoSupervisor(), avaliacaoARCInspetor);
            avaliacaoARCDAO.saveOrUpdate(avaliacaoARCInspetor);
        }
    }

    private void copiarDadosAvaliacaoInspetor(AvaliacaoRiscoControle arcPerfilRiscoVigente,
            AvaliacaoRiscoControle arcAnterior) {
        if (arcAnterior.getAvaliacaoARCInspetor() == null && arcPerfilRiscoVigente.getAvaliacaoARCInspetor() != null) {
            avaliacaoARCDAO.delete(arcPerfilRiscoVigente.getAvaliacaoARCInspetor());
        } else if (arcAnterior.getAvaliacaoARCInspetor() != null
                && arcPerfilRiscoVigente.getAvaliacaoARCInspetor() == null) {
            AvaliacaoARC avaliacaoARCInspetor = new AvaliacaoARC();
            copiarDadosAvaliacao(arcPerfilRiscoVigente, arcAnterior.getAvaliacaoARCInspetor(), avaliacaoARCInspetor);
            avaliacaoARCDAO.save(avaliacaoARCInspetor);
        } else if (arcAnterior.getAvaliacaoARCInspetor() != null
                && arcPerfilRiscoVigente.getAvaliacaoARCInspetor() != null) {
            AvaliacaoARC avaliacaoARCInspetor = arcPerfilRiscoVigente.getAvaliacaoARCInspetor();
            copiarDadosAvaliacao(arcPerfilRiscoVigente, arcAnterior.getAvaliacaoARCInspetor(), avaliacaoARCInspetor);
            avaliacaoARCDAO.saveOrUpdate(avaliacaoARCInspetor);
        }
    }

    private void copiarDadosAvaliacao(AvaliacaoRiscoControle arcPerfilRiscoVigente,
            AvaliacaoARC avaliacaoInspetorARCAnterior, AvaliacaoARC avaliacaoARCInspetor) {
        avaliacaoARCInspetor.setAvaliacaoRiscoControle(arcPerfilRiscoVigente);
        avaliacaoARCInspetor.setParametroNota(avaliacaoInspetorARCAnterior.getParametroNota());
        avaliacaoARCInspetor.setPerfil(avaliacaoInspetorARCAnterior.getPerfil());
        avaliacaoARCInspetor.setJustificativa(avaliacaoInspetorARCAnterior.getJustificativa());
        avaliacaoARCInspetor.setUltimaAtualizacao(avaliacaoInspetorARCAnterior.getUltimaAtualizacao());
        avaliacaoARCInspetor.setOperadorAtualizacao(avaliacaoInspetorARCAnterior.getOperadorAtualizacao());
        avaliacaoARCInspetor.setAlterarDataUltimaAtualizacao(false);
    }

    public void flush() {
        avaliacaoARCDAO.getSessionFactory().getCurrentSession().flush();

    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<AvaliacaoARC> avaliacoes, Metodologia metodologia) {
        for (AvaliacaoARC avaliacao : avaliacoes) {
            ParametroNota notaAntiga = avaliacao.getParametroNota();
            if (notaAntiga != null) {
                ParametroNota novaNota =
                        ParametroNotaMediator.get().buscarNota(metodologia,
                                notaAntiga.getValor());
                avaliacao.setParametroNota(novaNota);
                avaliacao.setAlterarDataUltimaAtualizacao(false);
                avaliacaoARCDAO.update(avaliacao);
            }
        }
    }

}
