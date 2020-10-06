package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;

@Repository
public class AjusteCorecDAO extends GenericDAOLocal<AjusteCorec, Integer> {

    public AjusteCorecDAO() {
        super(AjusteCorec.class);
    }

    public AjusteCorec buscarPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        return (AjusteCorec) criteria.uniqueResult();
    }

}
