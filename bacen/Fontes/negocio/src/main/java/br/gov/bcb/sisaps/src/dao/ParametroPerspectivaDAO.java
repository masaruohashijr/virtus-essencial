package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroPerspectiva;

@Repository
public class ParametroPerspectivaDAO extends GenericDAO<ParametroPerspectiva, Integer> {

    private static final String NOME = "nome";
    private static final String METODOLOGIA_PK = "metodologia.pk";

    public ParametroPerspectivaDAO() {
        super(ParametroPerspectiva.class);
    }

    public ParametroPerspectiva buscarPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", id));
        return (ParametroPerspectiva) criteria.uniqueResult();
    }
    
    public ParametroPerspectiva buscarParametrosMetodologiaEDescricao(Integer pkMetodologia , String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.EXACT));
        return (ParametroPerspectiva) criteria.uniqueResult();
    }

    public ParametroPerspectiva buscarPorMetodologiaENota(Metodologia metodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("metodologia", metodologia));
        return (ParametroPerspectiva) criteria.uniqueResult();
    }

    public List<ParametroPerspectiva> buscarParametrosNota(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        return cast(criteria.list());
    }

    public List<ParametroPerspectiva> buscarParametrosNotaCorec(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ne(NOME, "Não aplicável"));
        criteria.addOrder(Order.asc(NOME));
        return cast(criteria.list());
    }
    
    
    
}
