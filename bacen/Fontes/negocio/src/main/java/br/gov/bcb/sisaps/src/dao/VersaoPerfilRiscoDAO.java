package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;

@Repository
public class VersaoPerfilRiscoDAO extends GenericDAO<VersaoPerfilRisco, Integer> {

    private static final String PROP_TIPO_OBJETO_VERSIONADOR = "tipoObjetoVersionador";
    private static final String PROP_PERFIS_RISCO = "perfisRisco";
    private static final String PROP_PK = "pk";

    public VersaoPerfilRiscoDAO() {
        super(VersaoPerfilRisco.class);
    }

    @SuppressWarnings("unchecked")
    public List<VersaoPerfilRisco> buscarVersoesPerfilRisco(Integer pkPerfilRisco,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria perfisRiscoCriteria = criteria.createCriteria(PROP_PERFIS_RISCO);
        perfisRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        if (tipoObjetoVersionador != null) {
            criteria.add(Restrictions.eq(PROP_TIPO_OBJETO_VERSIONADOR, tipoObjetoVersionador));
        }
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public VersaoPerfilRisco buscarVersaoPerfilRisco(Integer pkPerfilRisco,
            TipoObjetoVersionadorEnum tipoObjetoVersionadorEnum) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria perfisRiscoCriteria = criteria.createCriteria(PROP_PERFIS_RISCO);
        perfisRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        criteria.add(Restrictions.eq(PROP_TIPO_OBJETO_VERSIONADOR, tipoObjetoVersionadorEnum));
        criteria.addOrder(Order.desc(PROP_PK));
        List<VersaoPerfilRisco> listaVersao = criteria.list();
        return listaVersao.isEmpty() ? null : listaVersao.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> buscarCodigosVersoesPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria perfisRiscoCriteria = criteria.createCriteria(PROP_PERFIS_RISCO);
        perfisRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        criteria.setProjection(Projections.groupProperty(PROP_PK));
        return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<VersaoPerfilRisco> buscarVersaoAnefPerfilRisco(Integer pkPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria perfisRiscoCriteria = criteria.createCriteria(PROP_PERFIS_RISCO);
        perfisRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        criteria.add(Restrictions.eq(PROP_TIPO_OBJETO_VERSIONADOR, TipoObjetoVersionadorEnum.AQT));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<VersaoPerfilRisco> buscarVersoesCriadasPerfilAtual(Integer pkPerfilRisco,
            List<Integer> versoesPerfilAnterior) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria perfisRiscoCriteria = criteria.createCriteria(PROP_PERFIS_RISCO);
        perfisRiscoCriteria.add(Restrictions.eq(PROP_PK, pkPerfilRisco));
        criteria.add(Restrictions.not(Restrictions.in(PROP_PK, versoesPerfilAnterior)));
        return criteria.list();
    }

}
