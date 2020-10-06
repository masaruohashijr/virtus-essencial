package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Delegacao;
import br.gov.bcb.sisaps.src.vo.ConsultaDelegacaoVO;
import br.gov.bcb.sisaps.src.vo.DelegacaoVO;

@Repository
@Transactional(readOnly = true)
public class DelegacaoDAO extends GenericDAOParaListagens<Delegacao, Integer, DelegacaoVO, ConsultaDelegacaoVO> {

    private static final long serialVersionUID = 1L;

    public DelegacaoDAO() {
        super(Delegacao.class, DelegacaoVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaDelegacaoVO consulta) {
        //TODO não precisa implementar
    }

    public boolean isARCDelegado(AvaliacaoRiscoControle avaliacaoRiscoControle, String matriculaServidor) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("avaliacaoRiscoControle", avaliacaoRiscoControle));
        criteria.add(Restrictions.eq("matriculaServidor", matriculaServidor));
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()) > 0;
    }

    public List<Delegacao> buscarDelegacoes() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }
}
