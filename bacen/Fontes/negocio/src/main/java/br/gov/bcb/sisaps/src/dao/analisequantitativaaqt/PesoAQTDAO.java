package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;

@Repository
public class PesoAQTDAO extends GenericDAOLocal<PesoAQT, Integer> {

    private static final String PK = "pk";
    private static final String PERFIS_RISCO = "perfisRisco";
    private static final String CICLO = "ciclo";
    private static final String PARAMENTRO_AQT = "parametroAQT";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";

    public PesoAQTDAO() {
        super(PesoAQT.class);
    }

    public PesoAQT buscarPesoAQT(ParametroAQT parametroAQT, Ciclo ciclo, List<VersaoPerfilRisco> versoesPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        
        if (ciclo != null) {
            criteria.add(Restrictions.eq(CICLO, ciclo));
        }
        
        if (parametroAQT != null) {
            criteria.add(Restrictions.eq(PARAMENTRO_AQT, parametroAQT));
        }
        
        if (versoesPerfilRisco != null) {
            criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, versoesPerfilRisco));
        }
        
        return (PesoAQT) criteria.uniqueResult();
    }

    public PesoAQT obterPesoRascunho(ParametroAQT parametroAQT, Ciclo ciclo) {
        Criteria criteria = criarCriteriaAnef(parametroAQT, ciclo);
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (PesoAQT) criteria.uniqueResult();
    }

    public PesoAQT obterPesoVigente(ParametroAQT parametroAQT, PerfilRisco perfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria(PERFIS_RISCO);
        perfilRiscoCriteria.add(Restrictions.eq(PK, perfilRisco.getPk()));
        criteria.add(Restrictions.eq(PARAMENTRO_AQT, parametroAQT));
        return (PesoAQT) criteria.uniqueResult();
    }

    private Criteria criarCriteriaAnef(ParametroAQT parametroAQT, Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(PesoAQT.class);
        criteria.createAlias(PARAMENTRO_AQT, "p");
        criteria.createAlias(CICLO, "c");
        criteria.add(Restrictions.eq("p.pk", parametroAQT.getPk()));
        criteria.add(Restrictions.eq("c.pk", ciclo.getPk()));
        return criteria;
    }
    
    
    public PesoAQT getPesoAqtPorVersaoPerfil(ParametroAQT parametroAQT, Ciclo ciclo,
            List<VersaoPerfilRisco> listaVersao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        if (ciclo != null) {
            criteria.add(Restrictions.eq(CICLO, ciclo));
        }
        if (parametroAQT != null) {
            criteria.add(Restrictions.eq(PARAMENTRO_AQT, parametroAQT));
        }
        criteria.add(Restrictions.in(VERSAO_PERFIL_RISCO, listaVersao));
        
        return (PesoAQT) criteria.uniqueResult();
    }

    public List<PesoAQT> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria(PERFIS_RISCO);
        perfilRiscoCriteria.add(Restrictions.eq(PK, pkPerfilRisco));
        return cast(criteria.list());
    }

    public List<PesoAQT> buscarPesosRascunho(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO, CICLO);
        criteria.add(Restrictions.eq("ciclo.pk", ciclo.getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return cast(criteria.list());
    }

}

