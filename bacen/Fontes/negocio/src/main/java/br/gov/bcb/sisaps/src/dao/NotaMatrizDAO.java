package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;

@Repository
public class NotaMatrizDAO extends GenericDAO<NotaMatriz, Integer> {


    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PROP_PK = "pk";

    public NotaMatrizDAO() {
        super(NotaMatriz.class);
    }

    @SuppressWarnings("unchecked")
    public NotaMatriz getUltimaNotaMatriz(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("matriz", matriz));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<NotaMatriz> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (NotaMatriz) resultado.get(0) : null;
    }
    
    public NotaMatriz buscarNotaMatrizPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pk));
        return (NotaMatriz) criteria.uniqueResult();
    }
    
    public void flush() {
        getCurrentSession().flush();
    }

    public NotaMatriz buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return (NotaMatriz) criteria.uniqueResult();
    }

    public NotaMatriz buscarNotaMatrizRascunho(Matriz matriz) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("matriz", "m");
        criteria.add(Restrictions.eq("m.pk", matriz.getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (NotaMatriz) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<NotaMatriz> buscarPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("matriz", "m");
        criteria.add(Restrictions.eq("m.ciclo.pk", pkCiclo));
        return criteria.list();
    }

}
