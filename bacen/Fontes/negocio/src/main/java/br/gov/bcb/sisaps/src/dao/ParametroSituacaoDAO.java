package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.ParametroSituacao;

@Repository
public class ParametroSituacaoDAO extends GenericDAO<ParametroSituacao, Integer> {

    private static final String NOME = "nome";
    private static final String METODOLOGIA_PK = "metodologia.pk";

    public ParametroSituacaoDAO() {
        super(ParametroSituacao.class);
    }

    public ParametroSituacao buscarPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", id));
        return (ParametroSituacao) criteria.uniqueResult();
    }
    
    public ParametroSituacao buscarParametrosMetodologiaEDescricao(Integer pkMetodologia , String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.EXACT));
        return (ParametroSituacao) criteria.uniqueResult();
    }

   
}
