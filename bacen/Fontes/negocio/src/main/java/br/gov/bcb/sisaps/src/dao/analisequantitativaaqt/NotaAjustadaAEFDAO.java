package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.NotaAjustadaAEF;

@Repository
public class NotaAjustadaAEFDAO extends GenericDAOLocal<NotaAjustadaAEF, Integer> {

    private static final String C_PK = "c.pk";
    private static final String C = "c";
    private static final String CICLO2 = "ciclo";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";

    public NotaAjustadaAEFDAO() {
        super(NotaAjustadaAEF.class);
    }

    public NotaAjustadaAEF buscarNotaAjustadaAEF(Ciclo ciclo, VersaoPerfilRisco versaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO2, C);
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        criteria.add(Restrictions.eq(VERSAO_PERFIL_RISCO, versaoPerfilRisco));
        return (NotaAjustadaAEF) criteria.uniqueResult();
    }

    public NotaAjustadaAEF buscarNotaAjustadaAEFRascunho(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO2, C);
        criteria.add(Restrictions.eq(C_PK, ciclo.getPk()));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (NotaAjustadaAEF) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<NotaAjustadaAEF> buscarNotaAjustadaAEFPorCiclo(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(CICLO2, C);
        criteria.add(Restrictions.eq(C_PK, pkCiclo));
        return criteria.list();
    }


}
