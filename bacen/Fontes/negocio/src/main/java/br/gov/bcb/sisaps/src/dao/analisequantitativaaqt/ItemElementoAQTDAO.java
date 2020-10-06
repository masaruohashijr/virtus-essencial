package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;

@Repository
public class ItemElementoAQTDAO extends GenericDAOLocal<ItemElementoAQT, Integer> {

    private static final String PROP_PARAMETRO_ITEM_ELEMENTO = "parametroItemElemento";

    @Autowired
    private ParametroItemElementoAQTDAO parametroItemElementoAQTDAO;

    public ItemElementoAQTDAO() {
        super(ItemElementoAQT.class);
    }

    public List<ParametroItemElementoAQT> buscarItemElementoAQT(ParametroElementoAQT parametroElementoAQT) {
        return parametroItemElementoAQTDAO.buscarItemElementoAQT(parametroElementoAQT);
    }

    @SuppressWarnings("unchecked")
    public List<ItemElementoAQT> buscarItensOrdenadosDoElemento(ElementoAQT elemento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_ITEM_ELEMENTO, PROP_PARAMETRO_ITEM_ELEMENTO);
        ElementoAQT elementoBase = ElementoAQTMediator.get().buscarPorPk(elemento.getPk());
        criteria.add(Restrictions.eq("elemento", elementoBase));
        criteria.addOrder(Order.asc(PROP_PARAMETRO_ITEM_ELEMENTO + ".ordem"));
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
    
    public ItemElementoAQT obterItemElementoPorDocumento(Documento documento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("documento", documento));
        return (ItemElementoAQT) criteria.uniqueResult();
    }

}
