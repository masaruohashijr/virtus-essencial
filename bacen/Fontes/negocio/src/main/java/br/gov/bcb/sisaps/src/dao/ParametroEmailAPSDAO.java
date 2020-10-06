package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.ParametroEmailAPS;

@Repository
public class ParametroEmailAPSDAO extends GenericDAOLocal<ParametroEmailAPS, Integer> {

    public ParametroEmailAPSDAO() {
        super(ParametroEmailAPS.class);
    }

    public ParametroEmailAPS buscarEmailParticipante(String localizacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("parametro", localizacao));
        return (ParametroEmailAPS) criteria.uniqueResult();
    }

}
