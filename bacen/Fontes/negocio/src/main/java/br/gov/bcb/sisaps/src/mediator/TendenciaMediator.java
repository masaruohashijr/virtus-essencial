package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.LinkedList;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.TendenciaARCDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseARCSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoARCInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class TendenciaMediator {

    @Autowired
    private TendenciaARCDAO tendenciaARCDAO;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public static TendenciaMediator get() {
        return SpringUtils.get().getBean(TendenciaMediator.class);
    }

    public TendenciaARC loadPK(Integer pk) {
        TendenciaARC result = tendenciaARCDAO.load(pk);
        inicializarDependencias(result);
        return result;
    }

    public TendenciaARC buscarPorPk(Integer pk) {
        return tendenciaARCDAO.buscarPorPK(pk);
    }

    @Transactional
    public void salvarTendenciaARC(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle,
            TendenciaARC tendenciaARC, PerfisNotificacaoEnum perfil) {
        new RegraEdicaoARCInspetorPermissaoAlteracao(ciclo, avaliacaoRiscoControle).validar();
        salvarAtualizarExcluirTendencia(tendenciaARC, avaliacaoRiscoControle, perfil);
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmEdicao(avaliacaoRiscoControle);
        tendenciaARCDAO.getSessionFactory().getCurrentSession().flush();
    }

    @Transactional
    public void salvarTendenciaARCEmAnalise(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle,
            TendenciaARC tendenciaARC, PerfisNotificacaoEnum perfil) {
        new RegraAnaliseARCSupervisorPermissaoAlteracao(ciclo, avaliacaoRiscoControle).validar();
        salvarAtualizarExcluirTendencia(tendenciaARC, avaliacaoRiscoControle, perfil);
        if (tendenciaARC.getParametroTendencia() == null && avaliacaoRiscoControle.getTendenciaARCInspetor() != null) {
            tendenciaARC
                    .setParametroTendencia(avaliacaoRiscoControle.getTendenciaARCInspetor().getParametroTendencia());
        }
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmAnalise(avaliacaoRiscoControle);
        tendenciaARCDAO.getSessionFactory().getCurrentSession().flush();

    }

    private void salvarAtualizarExcluirTendencia(TendenciaARC tendenciaARC,
            AvaliacaoRiscoControle avaliacaoRiscoControle, PerfisNotificacaoEnum perfil) {
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(tendenciaARC.getJustificativa())) {
            tendenciaARC.setJustificativa(null);
        }
        if (tendenciaARC.getParametroTendencia() == null && tendenciaARC.getJustificativa() == null) {
            excluirTendencia(tendenciaARC, avaliacaoRiscoControle, perfil);
        } else if (tendenciaARC.getPk() == null) {
            salvarTendencia(tendenciaARC, avaliacaoRiscoControle);
        } else if (tendenciaARC.getPk() != null) {
            atualizarTendencia(tendenciaARC);
        }
    }

    private void salvarTendencia(TendenciaARC tendenciaARC, AvaliacaoRiscoControle avaliacaoRiscoControle) {
        tendenciaARCDAO.save(tendenciaARC);
        tendenciaARCDAO.getSessionFactory().getCurrentSession().flush();
        avaliacaoRiscoControle.getTendenciasArc().add(tendenciaARC);
    }

    private void atualizarTendencia(TendenciaARC tendenciaARC) {
        TendenciaARC tendenciaARCDB = tendenciaARCDAO.buscarPorPK(tendenciaARC.getPk());
        if (tendenciaARCDB != null) {
            tendenciaARCDB.setParametroTendencia(tendenciaARC.getParametroTendencia() == null ? null : tendenciaARC
                    .getParametroTendencia());
            tendenciaARCDB.setJustificativa(tendenciaARC.getJustificativa() == null ? null : tendenciaARC
                    .getJustificativa());
            tendenciaARCDAO.update(tendenciaARCDB);
            tendenciaARCDAO.getSessionFactory().getCurrentSession().flush();
        }
    }

    private void excluirTendencia(TendenciaARC tendenciaARC, AvaliacaoRiscoControle avaliacaoRiscoControle,
            PerfisNotificacaoEnum perfil) {
        TendenciaARC tendenciaARCDB = tendenciaARCDAO.buscarPorPK(tendenciaARC.getPk());
        if (tendenciaARCDB != null) {
            AvaliacaoRiscoControle avaliacaoRiscoControle2 =
                    avaliacaoRiscoControleMediator.buscar(avaliacaoRiscoControle.getPk());
            for (TendenciaARC tendenciaARC2 : new LinkedList<TendenciaARC>(avaliacaoRiscoControle2.getTendenciasArc())) {
                if (tendenciaARC2.getPerfil().equals(perfil)) {
                    avaliacaoRiscoControle2.getTendenciasArc().remove(tendenciaARC2);
                }
            }
            tendenciaARCDAO.delete(tendenciaARCDB);
        }
    }

    @Transactional
    public void excluirTendencia(TendenciaARC tendencia) {
        tendenciaARCDAO.delete(tendencia);
    }

    private void inicializarDependencias(TendenciaARC result) {
        if (result.getParametroTendencia() != null) {
            Hibernate.initialize(result.getParametroTendencia());
        }
    }

    @Transactional
    public void duplicarTendenciaARCConclusaoAnalise(AvaliacaoRiscoControle arc, AvaliacaoRiscoControle novoARC, 
            boolean isCopiarUsuarioAnterior) {
        TendenciaARC tendenciaVigenteARCAnterior = arc.getTendenciaARCInspetorOuSupervisor();
        if (tendenciaVigenteARCAnterior != null) {
            TendenciaARC novaTendencia = new TendenciaARC();
            novaTendencia.setAvaliacaoRiscoControle(novoARC);
            novaTendencia.setJustificativa(tendenciaVigenteARCAnterior.getJustificativa());
            novaTendencia.setPerfil(PerfisNotificacaoEnum.INSPETOR);
            if (isCopiarUsuarioAnterior) {
                novaTendencia.setAlterarDataUltimaAtualizacao(false);
                novaTendencia.setUltimaAtualizacao(tendenciaVigenteARCAnterior.getUltimaAtualizacao());
                novaTendencia.setOperadorAtualizacao(tendenciaVigenteARCAnterior.getOperadorAtualizacao());
            }
            tendenciaARCDAO.save(novaTendencia);
        }
    }

    public ArrayList<ErrorMessage> validarTendencia(TendenciaARC tendenciaARC) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (tendenciaARC != null) {
            if (tendenciaARC.getJustificativa() != null && tendenciaARC.getParametroTendencia() == null) {
                SisapsUtil.validarObrigatoriedade(tendenciaARC.getParametroTendencia(), "Tendência inspetor", erros);
            }
        }
        return erros;
    }

    @Transactional
    public void copiarDadosTendenciaARCAnterior(AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        copiarDadosTendenciaARCInspetor(arcPerfilRiscoVigente, arcAnterior);
        copiarDadosTendenciaARCSupervisor(arcPerfilRiscoVigente, arcAnterior);
    }
    
    private void copiarDadosTendenciaARCSupervisor(AvaliacaoRiscoControle arcPerfilRiscoVigente, 
            AvaliacaoRiscoControle arcAnterior) {
        if (arcAnterior.getTendenciaARCSupervisor() == null && arcPerfilRiscoVigente.getTendenciaARCSupervisor() != null) {
            tendenciaARCDAO.delete(arcPerfilRiscoVigente.getTendenciaARCSupervisor());
        } else if (arcAnterior.getTendenciaARCSupervisor() != null 
                && arcPerfilRiscoVigente.getTendenciaARCSupervisor() == null) {
            TendenciaARC tendenciaARCSupervisor = new TendenciaARC();
            copiarDadosTendencia(arcPerfilRiscoVigente, arcAnterior.getTendenciaARCSupervisor(), tendenciaARCSupervisor);
            tendenciaARCDAO.save(tendenciaARCSupervisor);
        } else if (arcAnterior.getTendenciaARCSupervisor() != null 
                && arcPerfilRiscoVigente.getTendenciaARCSupervisor() != null) {
            TendenciaARC tendenciaARCInspetor = arcPerfilRiscoVigente.getTendenciaARCSupervisor();
            copiarDadosTendencia(arcPerfilRiscoVigente, arcAnterior.getTendenciaARCSupervisor(), tendenciaARCInspetor);
            tendenciaARCDAO.saveOrUpdate(tendenciaARCInspetor);
        }
    }

    private void copiarDadosTendenciaARCInspetor(AvaliacaoRiscoControle arcPerfilRiscoVigente, 
            AvaliacaoRiscoControle arcAnterior) {
        if (arcAnterior.getTendenciaARCInspetor() == null && arcPerfilRiscoVigente.getTendenciaARCInspetor() != null) {
            tendenciaARCDAO.delete(arcPerfilRiscoVigente.getTendenciaARCInspetor());
        } else if (arcAnterior.getTendenciaARCInspetor() != null 
                && arcPerfilRiscoVigente.getTendenciaARCInspetor() == null) {
            TendenciaARC tendenciaARCInspetor = new TendenciaARC();
            copiarDadosTendencia(arcPerfilRiscoVigente, arcAnterior.getTendenciaARCInspetor(), tendenciaARCInspetor);
            tendenciaARCDAO.save(tendenciaARCInspetor);
        } else if (arcAnterior.getTendenciaARCInspetor() != null 
                && arcPerfilRiscoVigente.getTendenciaARCInspetor() != null) {
            TendenciaARC tendenciaARCInspetor = arcPerfilRiscoVigente.getTendenciaARCInspetor();
            copiarDadosTendencia(arcPerfilRiscoVigente, arcAnterior.getTendenciaARCInspetor(), tendenciaARCInspetor);
            tendenciaARCDAO.saveOrUpdate(tendenciaARCInspetor);
        }
    }

    private void copiarDadosTendencia(AvaliacaoRiscoControle arcPerfilRiscoVigente,
            TendenciaARC avaliacaoInspetorARCAnterior, TendenciaARC tendenciaARCInspetor) {
        tendenciaARCInspetor.setAvaliacaoRiscoControle(arcPerfilRiscoVigente);
        tendenciaARCInspetor.setParametroTendencia(avaliacaoInspetorARCAnterior.getParametroTendencia());
        tendenciaARCInspetor.setPerfil(avaliacaoInspetorARCAnterior.getPerfil());
        tendenciaARCInspetor.setJustificativa(avaliacaoInspetorARCAnterior.getJustificativa());
        tendenciaARCInspetor.setUltimaAtualizacao(avaliacaoInspetorARCAnterior.getUltimaAtualizacao());
        tendenciaARCInspetor.setOperadorAtualizacao(avaliacaoInspetorARCAnterior.getOperadorAtualizacao());
        tendenciaARCInspetor.setAlterarDataUltimaAtualizacao(false);
    }
}
