package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.integracao.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.src.dao.DelegacaoDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Delegacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.validacao.RegraDelegacaoInclusaoValidacaoCampos;
import br.gov.bcb.sisaps.src.vo.ConsultaDelegacaoVO;
import br.gov.bcb.sisaps.src.vo.DelegacaoVO;

@Service
public class DelegacaoMediator extends AbstractMediatorPaginado<DelegacaoVO, Integer, ConsultaDelegacaoVO> {

    @Autowired
    private DelegacaoDAO delegacaoDAO;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Override
    protected DelegacaoDAO getDao() {
        return delegacaoDAO;
    }

    public static DelegacaoMediator get() {
        return SpringUtils.get().getBean(DelegacaoMediator.class);
    }
    
    @Transactional(readOnly = true)
    public List<Delegacao> buscarDelegacoes() {
        return delegacaoDAO.buscarDelegacoes();
    }

    public Delegacao buscarDelegacaoPorPk(Integer pk) {
        return delegacaoDAO.load(pk);
    }

    @Transactional
    public String incluir(boolean usuarioEDesignado, AvaliacaoRiscoControle avaliacaoRiscoControle,
            ServidorVO servidorEquipe, ServidorVO servidorUnidade) {
        new RegraDelegacaoInclusaoValidacaoCampos(usuarioEDesignado, servidorEquipe, servidorUnidade).validar();
        String matriculaServidorDesignado = null;
        if (servidorEquipe == null) {
            matriculaServidorDesignado = servidorUnidade.getMatricula();
        } else {
            matriculaServidorDesignado = servidorEquipe.getMatricula();
        }
        Delegacao delegacao =
                avaliacaoRiscoControle.getDelegacao() == null ? new Delegacao() : avaliacaoRiscoControle.getDelegacao();
        delegacao.setMatriculaServidor(matriculaServidorDesignado);
        delegacao.setAvaliacaoRiscoControle(avaliacaoRiscoControle);
        avaliacaoRiscoControle.setAlterarDataUltimaAtualizacao(false);
        avaliacaoRiscoControle.setDelegacao(delegacao);
        incluir(delegacao);
        if (AvaliacaoRiscoControleMediator.get().estadoAnalisado(avaliacaoRiscoControle.getEstado())) {
            avaliacaoRiscoControle.setEstado(EstadoARCEnum.EM_ANALISE);
        } else if (AvaliacaoRiscoControleMediator.get().estadoPreenchido(avaliacaoRiscoControle.getEstado())) {
            avaliacaoRiscoControle.setEstado(EstadoARCEnum.ANALISE_DELEGADA);
        }
        avaliacaoRiscoControle.setOperadorAnalise(null);
        avaliacaoRiscoControle.setDataAnalise(null);
        avaliacaoRiscoControleMediator.alterar(avaliacaoRiscoControle);
        
        return "ARC delegado com sucesso.";
    }

    @Transactional
    private void incluir(Delegacao delegacao) {
        if (delegacao.getPk() == null) {
            delegacaoDAO.save(delegacao);
        } else {
            delegacaoDAO.merge(delegacao);
        }
        delegacaoDAO.getSessionFactory().getCurrentSession().flush();
    }

    public boolean isARCDelegado(AvaliacaoRiscoControle avaliacaoRiscoControle, String matriculaServidor) {
        return delegacaoDAO.isARCDelegado(avaliacaoRiscoControle, matriculaServidor);
    }

    public boolean isDelegado(Delegacao delegacao) {
        return UsuarioCorrente.get().getMatricula().equals(delegacao.getMatriculaServidor());
    }

    public boolean isARCDelegadoOUSupervisorTitula(AvaliacaoRiscoControle avaliacaoRiscoControle,
            String matriculaServidor) {

        Delegacao delagacao = avaliacaoRiscoControle.getDelegacao();
        if (matriculaServidor.equals(delagacao.getMatriculaServidor())) {
            return true;
        }

        return false;

    }
    
    public SessionFactory obterSessionFactory() {
        return delegacaoDAO.getSessionFactory();
    }
    
    @Transactional
    public void excluirDelegacaoARC(AvaliacaoRiscoControle arc) {
        if (arc.getDelegacao() != null) {
            DelegacaoMediator.get().excluir(arc.getDelegacao());
            arc.setDelegacao(null);
        }
    }
    
    @Transactional
    public void excluir(Delegacao delegacao) {
        delegacaoDAO.delete(delegacaoDAO.load(delegacao.getPk()));
    }

    @Transactional
    public void copiarDelegacaoARCAnterior(AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        if (arcAnterior.getDelegacao() != null) {
            if (arcPerfilRiscoVigente.getDelegacao() != null) {
                delegacaoDAO.delete(arcPerfilRiscoVigente.getDelegacao());
            }
            Delegacao nova = new Delegacao();
            nova.setAvaliacaoRiscoControle(arcPerfilRiscoVigente);
            nova.setMatriculaServidor(arcAnterior.getDelegacao().getMatriculaServidor());
            nova.setUltimaAtualizacao(arcAnterior.getDelegacao().getUltimaAtualizacao());
            nova.setOperadorAtualizacao(arcAnterior.getDelegacao().getOperadorAtualizacao());
            nova.setAlterarDataUltimaAtualizacao(false);
            delegacaoDAO.save(nova);
            arcPerfilRiscoVigente.setDelegacao(nova);
        }
    }

}
