package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroEmail;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;

@Repository
public class ParametroEmailDAO extends GenericDAO<ParametroEmail, Integer> {

    public ParametroEmailDAO() {
        super(ParametroEmail.class);
    }

    public ParametroEmail buscarPorPK(Integer id) {
        return load(id);
    }

    public ParametroEmail buscarPorTipo(TipoEmailCorecEnum tipo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("tipo", tipo));
        return (ParametroEmail) criteria.uniqueResult();
    }

}
