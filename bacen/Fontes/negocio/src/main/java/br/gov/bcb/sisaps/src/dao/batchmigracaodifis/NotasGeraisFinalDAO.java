package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasGeraisFinal;

@Repository
@Transactional
public class NotasGeraisFinalDAO extends GenericDAOLocal<NotasGeraisFinal, Integer> {

    public NotasGeraisFinalDAO() {
        super(NotasGeraisFinal.class);
    }

    @SuppressWarnings("unchecked")
    public List<NotasGeraisFinal> getNotasGerais() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return criteria.list();
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasGeraisFinal ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }
    
}
