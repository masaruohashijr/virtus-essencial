package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;

@Repository
public class ParametroItemElementoAQTDAO extends GenericDAOLocal<ParametroItemElementoAQT, Integer> {

    public ParametroItemElementoAQTDAO() {
        super(ParametroItemElementoAQT.class);
    }

    public List<ParametroItemElementoAQT> buscarItemElementoAQT(ParametroElementoAQT parametroElementoAQT) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("parametroElemento", "p");
        criteria.add(Restrictions.eq("p.pk", parametroElementoAQT.getPk()));
        return cast(criteria.list());
    }
}
