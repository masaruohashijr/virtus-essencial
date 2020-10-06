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
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class SituacaoESDAO extends GenericDAO<SituacaoES, Integer> {


    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String CICLO2 = "ciclo";
    private static final String PROP_PK = "pk";

    public SituacaoESDAO() {
        super(SituacaoES.class);
    }

    @SuppressWarnings("unchecked")
    public SituacaoES getUltimaSituacaoES(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<SituacaoES> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (SituacaoES) resultado.get(0) : null;
    }
    
    public SituacaoES buscarSituacaoESPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pk));
        return (SituacaoES) criteria.uniqueResult();
    }

    public SituacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return (SituacaoES) criteria.uniqueResult();
    }
    
    public SituacaoES buscarPorPendencia(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.eq("pendente", SimNaoEnum.SIM));
        return (SituacaoES) criteria.uniqueResult();
    }

    public SituacaoES buscarSemPerfil(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (SituacaoES) criteria.uniqueResult();
    }


    public SituacaoES buscarSituacaoESRascunho(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (SituacaoES) criteria.uniqueResult();
    }
}
