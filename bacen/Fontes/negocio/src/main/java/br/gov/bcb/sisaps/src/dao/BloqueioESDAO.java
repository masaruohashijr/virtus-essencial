package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.BloqueioES;

@Repository
public class BloqueioESDAO extends GenericDAOLocal<BloqueioES, Integer> {

    public BloqueioESDAO() {
        super(BloqueioES.class);
    }
    
    public boolean isESBloqueado(String cnpj) {
        BloqueioES resultado = buscarUltimoBloqueioES(cnpj);
        return resultado != null;
    }

    public BloqueioES buscarUltimoBloqueioES(String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("cnpj", cnpj));
        criteria.add(Restrictions.isNull("dataDesbloqueio"));
        return (BloqueioES) criteria.uniqueResult();
    }

}
