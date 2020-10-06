package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;

@Repository
public class ObservacaoAgendaCorecDao extends GenericDAOLocal<ObservacaoAgendaCorec, Integer> {

    public ObservacaoAgendaCorecDao() {
        super(ObservacaoAgendaCorec.class);
    }

    public ObservacaoAgendaCorec buscarObservacao(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return (ObservacaoAgendaCorec) criteria.uniqueResult();
    }
    
    public List<ObservacaoAgendaCorec> buscarObservacaoAgenda(Integer pkAgenda) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("agenda.pk", pkAgenda));
        return criteria.list();
    }

}
