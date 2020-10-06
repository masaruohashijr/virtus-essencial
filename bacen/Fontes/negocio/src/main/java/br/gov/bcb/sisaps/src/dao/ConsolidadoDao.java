package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Consolidado;

@Repository
public class ConsolidadoDao extends GenericDAOLocal<Consolidado, Integer> {

    public ConsolidadoDao() {
        super(Consolidado.class);
    }

    public Consolidado buscarbuscarConsolidado(Integer codigo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", codigo));
        return (Consolidado) criteria.uniqueResult();
    }

}
