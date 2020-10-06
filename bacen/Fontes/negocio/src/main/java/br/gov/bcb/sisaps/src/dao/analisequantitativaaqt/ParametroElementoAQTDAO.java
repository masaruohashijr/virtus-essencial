package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;

@Repository
public class ParametroElementoAQTDAO extends GenericDAOLocal<ParametroElementoAQT, Integer> {

    public ParametroElementoAQTDAO() {
        super(ParametroElementoAQT.class);
    }

    public List<ParametroElementoAQT> buscarParamentos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

    public List<ParametroElementoAQT> buscarParElementoPorParAQT(ParametroAQT parametroAQT) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("paramentoAqt", "p");
        criteria.add(Restrictions.eq("p.pk", parametroAQT.getPk()));
        return cast(criteria.list());
    }

}
