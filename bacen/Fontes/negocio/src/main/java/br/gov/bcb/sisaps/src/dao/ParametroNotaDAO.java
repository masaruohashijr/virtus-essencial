package br.gov.bcb.sisaps.src.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Repository
public class ParametroNotaDAO extends GenericDAO<ParametroNota, Integer> {

    private static final String IS_NOTA_ELEMENTO = "isNotaElemento";
    private static final String VALOR = "valor";
    private static final String METODOLOGIA_PK = "metodologia.pk";
    private static final String METODOLOGIA = "metodologia";
    private static final String DESCRICAO = "descricao";

    public ParametroNotaDAO() {
        super(ParametroNota.class);
    }

    public ParametroNota buscarPorPK(Integer id) {
        return load(id);
    }

    public ParametroNota buscarPorMetodologiaENota(Metodologia metodologia, BigDecimal nota, boolean isNotaElemento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        if (metodologia.getIsCalculoMedia() != null && metodologia.getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            criteria.add(Restrictions.eq(IS_NOTA_ELEMENTO, SimNaoEnum.getTipo(isNotaElemento)));
        }
        criteria.add(Restrictions.and(Restrictions.le("limiteInferior", nota), Restrictions.ge("limiteSuperior", nota)));
        return (ParametroNota) criteria.uniqueResult();
    }
    
    public ParametroNota buscarNota(Metodologia metodologia, BigDecimal nota) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        if (metodologia.getIsCalculoMedia() != null && metodologia.getIsCalculoMedia().equals(SimNaoEnum.SIM)) {
            criteria.add(Restrictions.eq(IS_NOTA_ELEMENTO, SimNaoEnum.SIM));
        }
        criteria.add(Restrictions.eq(VALOR, nota));
        return (ParametroNota) criteria.uniqueResult();
    }

    public List<ParametroNota> buscarParametrosNota(Integer pkMetodologia) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        return cast(criteria.list());
    }
    
    public ParametroNota buscarParametrosMetodologiaEDescricao(Integer pkMetodologia , String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.ilike(DESCRICAO, descricao, MatchMode.EXACT));
        return (ParametroNota) criteria.uniqueResult();
    }
    
    
    public List<ParametroNota> buscarParametrosNotaCorec(Integer pkMetodologia, boolean isNotaElemento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA_PK, pkMetodologia));
        criteria.add(Restrictions.or(Restrictions.isNull(IS_NOTA_ELEMENTO), 
                Restrictions.eq(IS_NOTA_ELEMENTO, SimNaoEnum.getTipo(isNotaElemento))));
        criteria.add(Restrictions.ne(VALOR, new BigDecimal("-1")));
        criteria.addOrder(Order.asc(VALOR));
        return cast(criteria.list());
    }
    
    public ParametroNota buscarPorDescricao(Metodologia metodologia, String descricao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(METODOLOGIA, metodologia));
        criteria.add(Restrictions.eq(IS_NOTA_ELEMENTO, SimNaoEnum.NAO));
        criteria.add(Restrictions.eq(DESCRICAO, descricao));
        return (ParametroNota) criteria.uniqueResult();
    }
}
