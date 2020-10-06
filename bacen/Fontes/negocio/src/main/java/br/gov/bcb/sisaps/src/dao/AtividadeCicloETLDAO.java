package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.AtividadeCicloETL;

@Repository
@Transactional
public class AtividadeCicloETLDAO extends GenericDAO<AtividadeCicloETL, Integer> {

    private static final String PROP_CNPJ_ES = "cnpjES";
    private static final long serialVersionUID = 1L;

    public AtividadeCicloETLDAO() {
        super(AtividadeCicloETL.class);
    }
    
    public List<AtividadeCicloETL> consultarTodasAtividadesCicloETL() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }
    
    public AtividadeCicloETL buscarAtividadeCicloETL(String cnpjES, String codigoAtividade) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CNPJ_ES, cnpjES));
        criteria.add(Restrictions.eq("codigo", codigoAtividade));
        return (AtividadeCicloETL) criteria.uniqueResult();
    }

}
