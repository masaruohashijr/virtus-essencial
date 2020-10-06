package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class PerfilAtuacaoESDAO extends GenericDAO<PerfilAtuacaoES, Integer> {

    private static final String UNCHECKED = "unchecked";
    private static final String VERSAO_PERFIL_RISCO = "versaoPerfilRisco";
    private static final String CICLO2 = "ciclo";
    private static final String PROP_PK = "pk";

    public PerfilAtuacaoESDAO() {
        super(PerfilAtuacaoES.class);
    }

    @SuppressWarnings(UNCHECKED)
    public PerfilAtuacaoES getUltimoPerfilAtuacaoES(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.addOrder(Order.desc(NotaMatriz.PROP_ULTIMA_ATUALIZACAO));
        List<PerfilAtuacaoES> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (PerfilAtuacaoES) resultado.get(0) : null;
    }

    public PerfilAtuacaoES buscarPerfilAtuacaoESPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_PK, pk));
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    public PerfilAtuacaoES obterPerfilAtuacaoESPorDocumento(Documento documento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("documento", documento));
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    public PerfilAtuacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria versaoPerfilRiscoCriteria = criteria.createCriteria(VERSAO_PERFIL_RISCO);
        Criteria perfilRiscoCriteria = versaoPerfilRiscoCriteria.createCriteria("perfisRisco");
        perfilRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        criteria.list();
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    public PerfilAtuacaoES buscarPorPendencia(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.eq("pendente", SimNaoEnum.SIM));
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    public PerfilAtuacaoES buscarSemPerfil(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(CICLO2, ciclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    public PerfilAtuacaoES buscarPerfilAtuacaoESRascunho(Integer pkCiclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        criteria.add(Restrictions.isNull(VERSAO_PERFIL_RISCO));
        return (PerfilAtuacaoES) criteria.uniqueResult();
    }

    @SuppressWarnings(UNCHECKED)
    public List<PerfilAtuacaoES> buscarTodosPerfisPublicados() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.isNotNull(VERSAO_PERFIL_RISCO));
        return criteria.list();
    }
    
}
