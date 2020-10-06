package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;

@Repository
public class ElementoDAO extends GenericDAO<Elemento, Integer> {

    private static final String PROP_PARAMETRO_ELEMENTO = "parametroElemento";
    private static final long serialVersionUID = 1L;

    public ElementoDAO() {
        super(Elemento.class);
    }

    @SuppressWarnings("unchecked")
    public List<Elemento> buscarElementosOrdenadosDoArc(Integer idAvaliacaoRiscoControle) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_ELEMENTO, PROP_PARAMETRO_ELEMENTO);
        criteria.add(Restrictions.eq("avaliacaoRiscoControle",
                AvaliacaoRiscoControleMediator.get().buscar(idAvaliacaoRiscoControle)));
        criteria.addOrder(Order.asc(PROP_PARAMETRO_ELEMENTO + ".ordem"));
        return criteria.list();
    }
    
    @Transactional
    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }
    
    @Transactional
    public void refresh(Object obj) {
        getSessionFactory().getCurrentSession().refresh(obj);
    }
    

}
