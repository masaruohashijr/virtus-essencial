package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroTendencia;

@Repository
public class ParametroTendenciaDAO extends GenericDAO<ParametroTendencia, Integer> {

    
    
    private static final String METODOLOGIA_PK = "metodologia.pk";
    public ParametroTendenciaDAO() {
        super(ParametroTendencia.class);
    }
    
    

    public List<ParametroTendencia> buscarParametros(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        return cast(criteria.list());
    }
}
