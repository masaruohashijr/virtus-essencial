package br.gov.bcb.sisaps.src.dao;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.vo.ConsultaMatrizVO;
import br.gov.bcb.sisaps.src.vo.MatrizVO;

@Repository
@Transactional(readOnly = true)
public class MatrizCicloDAO extends GenericDAOParaListagens<Matriz, Integer, MatrizVO, ConsultaMatrizVO> {

    private static final String PROP_CICLO = "ciclo";
    private static final String PROP_ULTIMA_ATUALIZACAO = "ultimaAtualizacao";

    public MatrizCicloDAO() {
        super(Matriz.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaMatrizVO consulta) {
        //TODO não precisa implementar
    }

    public Matriz getUltimaMatrizCiclo(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO, ciclo));
        criteria.addOrder(Order.desc(PROP_ULTIMA_ATUALIZACAO));
        criteria.setMaxResults(1);
        return CollectionUtils.isNotEmpty(criteria.list()) ? (Matriz) criteria.list().get(0) : null;
    }

    public Matriz getMatrizEmEdicao(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO, ciclo));
        criteria.add(Restrictions.isNull("versaoPerfilRisco"));
        return (Matriz) criteria.uniqueResult();
    }

    public boolean existeMatrizAnterior(Ciclo ciclo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(PROP_CICLO, ciclo));
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()) > 1;
    }

    public Matriz buscarPorVersaoPerfilRisco(Integer pkVersaoPerfilRisco) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("versaoPerfilRisco.pk", pkVersaoPerfilRisco));
        return (Matriz) criteria.uniqueResult();
    }

    public Matriz buscar(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", pk));
        return (Matriz) criteria.uniqueResult();
    }

}
