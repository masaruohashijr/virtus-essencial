package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;

@Repository
public class ElementoAQTDAO extends GenericDAOLocal<ElementoAQT, Integer> {

    private static final String PROP_PARAMETRO_ELEMENTO = "parametroElemento";

    public ElementoAQTDAO() {
        super(ElementoAQT.class);
    }

    @SuppressWarnings("unchecked")
    public List<ElementoAQT> buscarElementosOrdenadosDoAnef(Integer idAQT) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_ELEMENTO, PROP_PARAMETRO_ELEMENTO);
        criteria.add(Restrictions.eq("analiseQuantitativaAQT", AnaliseQuantitativaAQTMediator.get().buscar(idAQT)));
        criteria.addOrder(Order.asc(PROP_PARAMETRO_ELEMENTO + ".ordem"));
        return criteria.list();
    }

}
