package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class ConclusaoESDAO extends GenericDAO<ConclusaoES, Integer> {

    private static final String UNCHECKED = "unchecked";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String CICLO2 = "ciclo";
    private static final String PROP_PK = "pk";

    public ConclusaoESDAO() {
        super(ConclusaoES.class);
    }

    @SuppressWarnings(UNCHECKED)
    public ConclusaoES getUltimaConclusaoES(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<ConclusaoES> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (ConclusaoES) resultado.get(0) : null;
    }
    
    public ConclusaoES buscarConclusaoESPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pk));
        return (ConclusaoES) criteria.uniqueResult();
    }

    public ConclusaoES obterConclusaoESPorDocumento(Documento documento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("documento", documento));
        return (ConclusaoES) criteria.uniqueResult();
    }

    public ConclusaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        return (ConclusaoES) criteria.uniqueResult();
    }
    
    public ConclusaoES buscarPorPendencia(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.eq("pendente", SimNaoEnum.SIM));
        return (ConclusaoES) criteria.uniqueResult();
    }
    
    public ConclusaoES buscarSemPerfil(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (ConclusaoES) criteria.uniqueResult();
    }

    public ConclusaoES buscarConclusaoESRascunho(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (ConclusaoES) criteria.uniqueResult();
    }

    @SuppressWarnings(UNCHECKED)
    public List<ConclusaoES> buscarTodasOpinioesPublicadas() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        return criteria.list();
    }

}
