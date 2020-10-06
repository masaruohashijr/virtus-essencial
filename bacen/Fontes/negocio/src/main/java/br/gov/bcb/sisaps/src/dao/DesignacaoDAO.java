package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.vo.ConsultaDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.DesignacaoVO;

@Repository
public class DesignacaoDAO extends GenericDAOParaListagens<Designacao, Integer, DesignacaoVO, ConsultaDesignacaoVO> {

    private static final long serialVersionUID = 1L;

    public DesignacaoDAO() {
        super(Designacao.class, DesignacaoVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaDesignacaoVO consulta) {
        //TODO método não precisa ser implementado
    }

    public boolean isARCDesignado(AvaliacaoRiscoControle avaliacaoRiscoControle, String matriculaServidor) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("avaliacaoRiscoControle", avaliacaoRiscoControle));
        criteria.add(Restrictions.eq("matriculaServidor", matriculaServidor));
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()) > 0;
    }
    
    public Designacao buscarDesignacaoPorARC(Integer pkARC) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("avaliacaoRiscoControle.pk", pkARC));
        return (Designacao) criteria.uniqueResult();
    }

    public List<Designacao> buscarDesignacoes() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }
}
