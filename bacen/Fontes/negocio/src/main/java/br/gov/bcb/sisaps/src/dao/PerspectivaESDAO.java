package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class PerspectivaESDAO extends GenericDAO<PerspectivaES, Integer> {

    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String CICLO2 = "ciclo";
    private static final String PROP_PK = "pk";
    private static final String CICLO_PK = "ciclo.pk";

    public PerspectivaESDAO() {
        super(PerspectivaES.class);
    }

    @SuppressWarnings("unchecked")
    public PerspectivaES getUltimaPerspectivaES(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, ciclo.getPk()));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<PerspectivaES> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (PerspectivaES) resultado.get(0) : null;
    }
    
    public PerspectivaES buscarPerspectivaESPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pk));
        return (PerspectivaES) criteria.uniqueResult();
    }

    public PerspectivaES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return (PerspectivaES) criteria.uniqueResult();
    }
    
    public PerspectivaES buscarPorPendencia(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.eq("pendente", SimNaoEnum.SIM));
        return (PerspectivaES) criteria.uniqueResult();
    }

    public PerspectivaES buscarSemPerfil(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (PerspectivaES) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<PerspectivaES> buscarPerspectivasVigentesPorPerfilRisco(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        return criteria.list();
    }

    public PerspectivaES buscarPerspectivaESRascunho(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (PerspectivaES) criteria.uniqueResult();
    }

}
