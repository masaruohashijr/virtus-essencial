package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroAtaCorec;

@Repository
public class ParametroAtaCorecDAO extends GenericDAO<ParametroAtaCorec, Integer> {

    public ParametroAtaCorecDAO() {
        super(ParametroAtaCorec.class);
    }

    public ParametroAtaCorec buscarPorPK(Integer id) {
        return load(id);
    }

    public ParametroAtaCorec buscar() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return (ParametroAtaCorec) criteria.uniqueResult();
    }

}
