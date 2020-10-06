package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AtividadeCiclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;

@Repository
public class AnexoAQTDAO extends GenericDAOLocal<AnexoAQT, Integer> {

    private static final String ANALISE_QUANTITATIVA_AQT = "analiseQuantitativaAQT";

    public AnexoAQTDAO() {
        super(AnexoAQT.class);
    }

    public List<AnexoAQT> buscarAnexosAqt(AnaliseQuantitativaAQT aqt) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(ANALISE_QUANTITATIVA_AQT, aqt));
        return cast(criteria.list());
    }

    public AnexoAQT buscarAnexoArcMesmoNome(AnaliseQuantitativaAQT anef, String link) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AnaliseQuantitativaAQT.PROP_ANALISE_QUANTITATIVA_AQT, anef));
        criteria.add(Restrictions.eq("link", link));
        return (AnexoAQT) criteria.uniqueResult();
    }

    public AnexoAQT buscarUltimoAnexoANEF(AnaliseQuantitativaAQT anef) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(ANALISE_QUANTITATIVA_AQT, anef));
        criteria.addOrder(Order.desc(AtividadeCiclo.PROP_ULTIMA_ATUALIZACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (AnexoAQT) criteria.list().get(0) : null;
    }

    public List<AnexoAQT> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }
}
