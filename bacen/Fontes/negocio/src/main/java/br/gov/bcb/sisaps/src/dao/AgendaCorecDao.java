package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;

@Repository
public class AgendaCorecDao extends GenericDAOLocal<AgendaCorec, Integer> {

    public AgendaCorecDao() {
        super(AgendaCorec.class);
    }
    
    public AgendaCorec buscarAgendaCorecPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        return (AgendaCorec) criteria.uniqueResult();
    }
    
    public AgendaCorec buscarAgendaCorec(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return (AgendaCorec) criteria.uniqueResult();
    }

}
