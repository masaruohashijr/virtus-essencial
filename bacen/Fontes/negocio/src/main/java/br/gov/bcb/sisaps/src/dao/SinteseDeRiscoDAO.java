package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;

@Repository
public class SinteseDeRiscoDAO extends GenericDAO<SinteseDeRisco, Integer> {

    private static final String PROP_VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String PROP_PARAMETRO_GRUPO_RISCO_CONTROLE = "parametroGrupoRiscoControle";
    private static final String CICLO = "ciclo";
    private static final String CICLO_PK = "ciclo.pk";

    public SinteseDeRiscoDAO() {
        super(SinteseDeRisco.class);
    }

    @SuppressWarnings("unchecked")
    public SinteseDeRisco getUltimaSinteseParametroGrupoRiscoEdicao(ParametroGrupoRiscoControle parametroGrupoRisco,
            Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, parametroGrupoRisco));
        if (ciclo != null) {
            criteria.add(Restrictions.eq(CICLO, ciclo));
        }

        criteria.addOrder(Order.desc(SinteseDeRisco.PROP_ULTIMA_ATUALIZACAO));
        List<SinteseDeRisco> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (SinteseDeRisco) resultado.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    public SinteseDeRisco getUltimaSinteseParametroGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco,
            Ciclo ciclo, List<VersaoPerfilRisco> versoesPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, parametroGrupoRisco));
        if (ciclo != null) {
            criteria.add(Restrictions.eq(CICLO, ciclo));
        }
        if (CollectionUtils.isNotEmpty(versoesPerfilRisco)) {
            criteria.add(Restrictions.in(PROP_VERSAO_PERFIL_RISCO, versoesPerfilRisco));
        }
        criteria.addOrder(Order.desc(SinteseDeRisco.PROP_ULTIMA_ATUALIZACAO));
        List<SinteseDeRisco> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (SinteseDeRisco) resultado.get(0) : null;
    }

    public SinteseDeRisco buscarSinteseMatrizPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return (SinteseDeRisco) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<SinteseDeRisco> buscarSintesesDeRiscoPerfilRisco(List<VersaoPerfilRisco> versoesPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.add(Restrictions.in(PROP_VERSAO_PERFIL_RISCO, versoesPerfilRisco));
        criteria.addOrder(Order.asc("parametroGrupoRiscoControle.ordem"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<SinteseDeRisco> buscarSintesesDeRiscoCiclo(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PROP_PARAMETRO_GRUPO_RISCO_CONTROLE, PROP_PARAMETRO_GRUPO_RISCO_CONTROLE);
        criteria.add(Restrictions.eq(CICLO, ciclo));

        return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<SinteseDeRisco> buscarSintesesPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO_PK, pkCiclo));
        return criteria.list();
    }
}
