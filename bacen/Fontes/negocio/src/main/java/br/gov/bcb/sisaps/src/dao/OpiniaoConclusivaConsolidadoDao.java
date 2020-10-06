package br.gov.bcb.sisaps.src.dao;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.util.enumeracoes.TipoStatusDocumento;

@Repository
public class OpiniaoConclusivaConsolidadoDao extends GenericDAOLocal<OpiniaoConclusivaConsolidado, Integer> {

    private static final String WHERE_CON_PK = "where con.pk = ";
    private static final String ORDER_BY_OPINIAO_ULTIMA_ATUALIZACAO_DESC = "order by opiniao.ultimaAtualizacao desc";
    private static final String LEFT_JOIN_OPINIAO_CONSOLIDADO_AS_CON = "left join opiniao.consolidado as con ";
    private static final String SELECT_OPINIAO = "select opiniao.pk from OpiniaoConclusivaConsolidado as opiniao ";
    private static final String STATUS = "status";
    private static final String PK = ".pk";
    private static final String CONSOLIDADO = "consolidado";
    private static final String REGISTRO_NAO_ENCONTRADO = "Registro não encontrado.";

    
    public OpiniaoConclusivaConsolidadoDao() {
        super(OpiniaoConclusivaConsolidado.class);
    }
    
    public OpiniaoConclusivaConsolidado buscarPublicado(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_OPINIAO);
        hql.append(LEFT_JOIN_OPINIAO_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and opiniao.status = 1 and opiniao.opiniaoPosterior is null ");
        hql.append(ORDER_BY_OPINIAO_ULTIMA_ATUALIZACAO_DESC);
        Query query = getCurrentSession().createQuery(hql.toString());

        OpiniaoConclusivaConsolidado opiniao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                opiniao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return opiniao;        
    }

    public OpiniaoConclusivaConsolidado buscarPublicadoBatch(Integer pkConsolidado) {
        StringBuilder hql = new StringBuilder();
        hql.append(SELECT_OPINIAO);
        hql.append(LEFT_JOIN_OPINIAO_CONSOLIDADO_AS_CON);
        hql.append(WHERE_CON_PK + pkConsolidado + " and opiniao.status = 1 ");
        hql.append("order by opiniao.pk desc");
        Query query = getCurrentSession().createQuery(hql.toString());
        
        OpiniaoConclusivaConsolidado opiniao = null;
        
        try {
            Object object = query.list();
            if (object != null && !query.list().isEmpty()) {
                Object obj = query.list().get(0);
                Integer pk = ((Integer) (obj));
                opiniao = load(pk);
            }
        } catch (NoResultException e) {
            System.out.println(REGISTRO_NAO_ENCONTRADO);
        }
        
        return opiniao;        
    }
    
    public OpiniaoConclusivaConsolidado buscarEmEdicao(Integer pkConsolidado) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CONSOLIDADO, CONSOLIDADO);
        criteria.add(Restrictions.eq(CONSOLIDADO + PK, pkConsolidado));
        criteria.add(Restrictions.eq(STATUS, TipoStatusDocumento.EM_EDICAO));
        return (OpiniaoConclusivaConsolidado) criteria.uniqueResult();
    }

    public OpiniaoConclusivaConsolidado buscarEmHomologacao(Integer pkConsolidado) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CONSOLIDADO, CONSOLIDADO);
        criteria.add(Restrictions.eq(CONSOLIDADO + PK, pkConsolidado));
        criteria.add(Restrictions.eq(STATUS, TipoStatusDocumento.EM_HOMOLOGACAO));
        return (OpiniaoConclusivaConsolidado) criteria.uniqueResult();
    }

}
