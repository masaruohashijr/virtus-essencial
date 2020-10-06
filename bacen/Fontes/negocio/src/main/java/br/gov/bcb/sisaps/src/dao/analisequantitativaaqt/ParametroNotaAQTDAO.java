package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import java.math.BigDecimal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class ParametroNotaAQTDAO extends GenericDAOLocal<ParametroNotaAQT, Integer> {

    private static final String VALOR = "valor";
    private static final String DESCRICAO = "descricao";
    private static final String LIMITE_INFERIOR = "limiteInferior";
    private static final String LIMITE_SUPERIOR = "limiteSuperior";
    private static final String METODOLOGIA = "metodologia";

    public ParametroNotaAQTDAO() {
        super(ParametroNotaAQT.class);
    }
    
    public ParametroNotaAQT buscarPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", id));
        return (ParametroNotaAQT) criteria.uniqueResult();
    }
    
    public ParametroNotaAQT buscarPorMetodologiaENota(Metodologia metodologia, BigDecimal nota) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        if (metodologia.getIsCalculoMedia() != null && metodologia.getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            criteria.add(Restrictions.eq("isNotaElemento", SimNaoEnum.NAO));
        }
        criteria.add(Restrictions.and(Restrictions.le(LIMITE_INFERIOR, nota),
                Restrictions.ge(LIMITE_SUPERIOR, nota)));
        return (ParametroNotaAQT) criteria.uniqueResult();
    }

    public ParametroNotaAQT buscarCorPorMetodologiaENota(Metodologia metodologia, BigDecimal nota) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        if (metodologia.getIsCalculoMedia() != null && metodologia.getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            criteria.add(Restrictions.eq("isNotaElemento", SimNaoEnum.SIM));
        }
        criteria.add(Restrictions.and(Restrictions.le(LIMITE_INFERIOR, nota), Restrictions.ge(LIMITE_SUPERIOR, nota)));
        return (ParametroNotaAQT) criteria.uniqueResult();
    }
    
    public ParametroNotaAQT buscarPorNota(Metodologia metodologia, BigDecimal nota) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        if (metodologia.getIsCalculoMedia() != null && metodologia.getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            criteria.add(Restrictions.eq("isNotaElemento", SimNaoEnum.SIM));
        }
        criteria.add(Restrictions.eq(VALOR, nota));
        return (ParametroNotaAQT) criteria.uniqueResult();
    }
    
    public ParametroNotaAQT buscarPorDescricao(Metodologia metodologia, String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        criteria.add(Restrictions.eq("isNotaElemento", SimNaoEnum.NAO));
        criteria.add(Restrictions.eq(DESCRICAO, descricao));
        return (ParametroNotaAQT) criteria.uniqueResult();
    }

}
