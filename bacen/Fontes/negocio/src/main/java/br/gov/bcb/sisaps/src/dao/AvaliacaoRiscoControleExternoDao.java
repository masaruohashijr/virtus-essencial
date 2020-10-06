package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;

@Repository
public class AvaliacaoRiscoControleExternoDao extends GenericDAOLocal<AvaliacaoRiscoControleExterno, Integer> {

    private static final String CICLO_PK = "ciclo.pk";

    public AvaliacaoRiscoControleExternoDao() {
        super(AvaliacaoRiscoControleExterno.class);
    }
    
    public boolean existeArcExternoCiclo(Integer pkCiclo, Integer pkParametroRiscoControleGovernanca) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        criteria.add(Restrictions.eq("parametroGrupoRiscoControle.pk", pkParametroRiscoControleGovernanca));
        return CollectionUtils.isNotEmpty(criteria.list());
    }

    public AvaliacaoRiscoControle buscarUltimoArcExterno(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        criteria.addOrder(Order.desc(AvaliacaoRiscoControleExterno.PROP_ULTIMA_ATUALIZACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isEmpty(criteria.list()) ? 
                null : ((AvaliacaoRiscoControleExterno) criteria.list().get(0)).getAvaliacaoRiscoControle();
    }

    public AvaliacaoRiscoControleExterno buscarArcExterno(Integer pkArc) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("avaliacaoRiscoControle.pk", pkArc));
        return (AvaliacaoRiscoControleExterno) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<AvaliacaoRiscoControleExterno> buscarArcExternoPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        return criteria.list();
    }

    public ArcNotasVO buscarUltimoArcExternoVO(Integer pkCiclo) {
        StringBuilder hql = new StringBuilder();
        hql.append("select distinct new br.gov.bcb.sisaps.src.vo.ArcNotasVO(");
        hql.append("arc.pk, arc.valorNota, notaSupervisor, ");
        hql.append("notaCorec, arcVigente.pk, arc.estado, ");
        hql.append("designacao.pk, delegacao.pk, arc.tipo, arcExterno.ultimaAtualizacao) ");
        hql.append("from AvaliacaoRiscoControleExterno arcExterno ");
        hql.append("inner join arcExterno.avaliacaoRiscoControle as arc ");
        hql.append("inner join arcExterno.ciclo as ciclo ");
        hql.append("left join arc.avaliacaoRiscoControleVigente as arcVigente ");
        hql.append("left join arc.designacao as designacao ");
        hql.append("left join arc.delegacao as delegacao ");
        hql.append("left join arc.notaSupervisor as notaSupervisor ");
        hql.append("left join arc.notaCorec as notaCorec ");
        hql.append("where ciclo.pk = :pkCiclo ");
        hql.append("order by arcExterno.ultimaAtualizacao desc ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.setInteger("pkCiclo", pkCiclo);
        return CollectionUtils.isEmpty(query.list()) ? null : (ArcNotasVO) query.list().get(0);
    }

}
