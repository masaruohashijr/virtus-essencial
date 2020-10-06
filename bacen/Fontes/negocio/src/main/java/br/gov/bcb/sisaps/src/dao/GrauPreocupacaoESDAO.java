package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class GrauPreocupacaoESDAO extends GenericDAO<GrauPreocupacaoES, Integer> {


    private static final String CICLO_PK = "ciclo.pk";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String CICLO2 = "ciclo";


    public GrauPreocupacaoESDAO() {
        super(GrauPreocupacaoES.class);
    }

    @SuppressWarnings("unchecked")
    public GrauPreocupacaoES getUltimoGrauPreocupacaoES(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<GrauPreocupacaoES> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (GrauPreocupacaoES) resultado.get(0) : null;
    }

    public GrauPreocupacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq("pk", pkPerfilRisco));
        return (GrauPreocupacaoES) criteria.uniqueResult();
    }
    
    
    public GrauPreocupacaoES buscarPorPendencia(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, ciclo.getPk()));
        criteria.add(Restrictions.eq("pendente", SimNaoEnum.SIM));
        return (GrauPreocupacaoES) criteria.uniqueResult();
    }

    public GrauPreocupacaoES buscarGrauPreocupacaoESRascunho(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (GrauPreocupacaoES) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> quantidadeGrausCicloPorPerfilRisco(PerfilRisco perfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, perfilRisco.getCiclo().getPk()));
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.le("pk", perfilRisco.getPk()));
        criteria.setProjection(Projections.groupProperty("pk"));
        return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<GrauPreocupacaoES> buscarPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        return criteria.list();
    }

}
