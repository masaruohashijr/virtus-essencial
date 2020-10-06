package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.EventoOpiniaoConclusiva;

@Repository
public class EventoOpiniaoConclusivaDao extends GenericDAOLocal<EventoOpiniaoConclusiva, Integer> {

    public EventoOpiniaoConclusivaDao() {
        super(EventoOpiniaoConclusiva.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<EventoOpiniaoConclusiva> buscarEventosOpiniaoConclusiva(String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("cnpjCodigoOrigemSrc", cnpj));
        return criteria.list();
    }

}
