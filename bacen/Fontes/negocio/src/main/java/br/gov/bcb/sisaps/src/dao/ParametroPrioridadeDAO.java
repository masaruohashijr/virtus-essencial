package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;

@Repository
public class ParametroPrioridadeDAO extends GenericDAO<ParametroPrioridade, Integer> {

    public ParametroPrioridadeDAO() {
        super(ParametroPrioridade.class);
    }

    public List<ParametroPrioridade> buscarTodasPrioridades() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.addOrder(Order.asc("codigo"));
        return cast(criteria.list());
    }

    public ParametroPrioridade buscarPrioridadeNome(String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("descricao", descricao));
        return (ParametroPrioridade) criteria.uniqueResult();
    }
    
    public boolean buscarParametro(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return cast(criteria.list()).isEmpty() ?  false : true;
    }
    
}
