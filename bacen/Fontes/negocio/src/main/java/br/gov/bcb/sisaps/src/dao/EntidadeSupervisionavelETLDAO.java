package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavelETL;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
@Transactional(readOnly = true)
public class EntidadeSupervisionavelETLDAO extends GenericDAO<EntidadeSupervisionavelETL, Integer> {

    private static final long serialVersionUID = 1L;

    public EntidadeSupervisionavelETLDAO() {
        super(EntidadeSupervisionavelETL.class);
    }
    
    public List<EntidadeSupervisionavelETL> buscarEntidadesETLPertenceSRC() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pertenceSrc", SimNaoEnum.SIM));
        return cast(criteria.list());
    }

}
