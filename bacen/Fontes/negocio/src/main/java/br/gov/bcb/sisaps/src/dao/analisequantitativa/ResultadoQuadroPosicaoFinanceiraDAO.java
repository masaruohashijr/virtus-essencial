package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;

@Repository
@Transactional(readOnly = true)
public class ResultadoQuadroPosicaoFinanceiraDAO extends GenericDAOLocal<ResultadoQuadroPosicaoFinanceira, Integer> {
    private static final String PONTO = ".";
    private static final String PK = "pk";
    private static final String QUADRO = "quadro";

    public ResultadoQuadroPosicaoFinanceiraDAO() {
        super(ResultadoQuadroPosicaoFinanceira.class);
    }

    @Transactional
    public void salvar(ResultadoQuadroPosicaoFinanceira resultado) {
        save(resultado);
        flush();
    }

    @SuppressWarnings("unchecked")
    public List<ResultadoQuadroPosicaoFinanceira> obterResultados(QuadroPosicaoFinanceira quadro) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class)
                .getQuadroPosicaoFinanceira()), QUADRO);
        criteria.add(Restrictions.eq(QUADRO + PONTO + PK, quadro.getPk()));
        criteria.addOrder(Order.asc(property(getPropertyObject(ResultadoQuadroPosicaoFinanceira.class).getPeriodo())));
        return (List<ResultadoQuadroPosicaoFinanceira>) criteria.list();
    }
}
