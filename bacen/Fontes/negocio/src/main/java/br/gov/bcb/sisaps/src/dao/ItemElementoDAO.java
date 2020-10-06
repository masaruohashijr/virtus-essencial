package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;

@Repository
@Transactional(readOnly = true)
public class ItemElementoDAO extends GenericDAO<ItemElemento, Integer> {

    private static final String PROP_PARAMETRO_ITEM_ELEMENTO = "parametroItemElemento";



    public ItemElementoDAO() {
        super(ItemElemento.class);
    }

    public ItemElemento obterItemElementoPorDocumento(Documento documento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("documento", documento));
        return (ItemElemento) criteria.uniqueResult();
    }
    
    
    
    @SuppressWarnings("unchecked")
    public List<ItemElemento> buscarItensOrdenadosDoElemento(Elemento elemento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_ITEM_ELEMENTO, PROP_PARAMETRO_ITEM_ELEMENTO);
        Elemento elementoBase = ElementoMediator.get().buscarPorPk(elemento.getPk());
        criteria.add(Restrictions.eq("elemento", elementoBase));
        criteria.addOrder(Order.asc(PROP_PARAMETRO_ITEM_ELEMENTO + ".ordem"));
        return  criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list(); 
    }
    
    @Transactional
    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }

}
