package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;

@Repository
public class TendenciaARCDAO extends GenericDAO<TendenciaARC, Integer> {

    public TendenciaARCDAO() {
        super(TendenciaARC.class);
    }

    public TendenciaARC buscarPorPK(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return (TendenciaARC) criteria.uniqueResult();
    }
    
}
