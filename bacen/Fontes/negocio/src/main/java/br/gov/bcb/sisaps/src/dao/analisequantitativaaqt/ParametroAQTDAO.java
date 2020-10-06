package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;

@Repository
public class ParametroAQTDAO extends GenericDAOLocal<ParametroAQT, Integer> {

    private static final String ORDEM = "ordem";

    public ParametroAQTDAO() {
        super(ParametroAQT.class);
    }

    public List<ParametroAQT> buscarParamentos(Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("metodologia", metodologia));
        criteria.addOrder(Order.asc(ORDEM));
        return cast(criteria.list());
    }

    public boolean existeAQTAnalisadoGrupoRisco(ParametroAQT parametroAqt, Matriz matriz) {
        return false;
    }

    public List<ParametroAQT> buscarGruposRiscoDaMatrizESinteseObrigatoria() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.addOrder(Order.asc(ORDEM));
        return cast(criteria.list());
    }
    
    public ParametroAQT buscarParemetroAQT(AnaliseQuantitativaAQT quantitativaAQT) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", quantitativaAQT.getParametroAQT().getPk()));
        return (ParametroAQT) criteria.uniqueResult();
    }

}
