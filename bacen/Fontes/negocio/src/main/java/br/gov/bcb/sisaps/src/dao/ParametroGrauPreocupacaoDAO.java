package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroGrauPreocupacao;

@Repository
public class ParametroGrauPreocupacaoDAO extends GenericDAO<ParametroGrauPreocupacao, Integer> {
    private static final String VALOR = "valor";
    private static final String METODOLOGIA_PK = "metodologia.pk";

    public ParametroGrauPreocupacaoDAO() {
        super(ParametroGrauPreocupacao.class);
    }

    public ParametroGrauPreocupacao buscarPorPK(Integer id) {
        return load(id);
    }

    public List<ParametroGrauPreocupacao> buscarParametrosGrauPreocupacao(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        return cast(criteria.list());
    }

    public ParametroGrauPreocupacao buscarPorMetodologiaENota(Integer pkMetodologia, Short nota) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.eq(VALOR, nota));
        return (ParametroGrauPreocupacao) criteria.uniqueResult();
    }

    public ParametroGrauPreocupacao buscarPorMetodologiaEDescricao(Integer pkMetodologia, String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.EXACT));
        return (ParametroGrauPreocupacao) criteria.uniqueResult();
    }

    public List<ParametroGrauPreocupacao> buscarParametrosNotaCorec(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ne(VALOR, new Short("-1")));
        criteria.addOrder(Order.asc(VALOR));

        return cast(criteria.list());
    }
}
