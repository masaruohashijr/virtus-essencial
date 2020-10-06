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
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;

@Repository
@Transactional
public class OutraInformacaoQuadroPosicaoFinanceiraDAO 
    extends GenericDAOLocal<OutraInformacaoQuadroPosicaoFinanceira, Integer> {

    private static final String PERIODO = "periodo";
    private static final String LAYOUT_PK = "layout.pk";

    public OutraInformacaoQuadroPosicaoFinanceiraDAO() {
        super(OutraInformacaoQuadroPosicaoFinanceira.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<OutraInformacaoQuadroPosicaoFinanceira> buscarOutraInformacaoQuadroPorTipo(
            Integer pkQuadro, TipoInformacaoEnum tipoInformacao, boolean ordemCrescentePeriodo) {
        Criteria criteria = criarCriteria();
        Criteria criteriaOutraInfo = criteria.createCriteria("layout.outraInformacaoAnaliseQuantitativa", "outraInfo");
        criteriaOutraInfo.add(Restrictions.eq("outraInfo.tipoInformacao", tipoInformacao));
        addFiltroQuadro(pkQuadro, criteria);
        criteria.addOrder(Order.asc("layout.sequencial"));
        criteria.addOrder(Order.desc(LAYOUT_PK));
        addOrdemPorPeriodo(criteria, ordemCrescentePeriodo);
        return criteria.list();
    }

    private void addOrdemPorPeriodo(Criteria criteria, boolean ordemCrescentePeriodo) {
        if (ordemCrescentePeriodo) {
            criteria.addOrder(Order.asc(PERIODO));
        } else {
            criteria.addOrder(Order.desc(PERIODO));
        }
    }

    private Criteria criarCriteria() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias("quadroPosicaoFinanceira", "quadro");
        criteria.createAlias("layoutOutraInformacaoAnaliseQuantitativa", "layout");
        return criteria;
    }
    
    public OutraInformacaoQuadroPosicaoFinanceira buscarPorPk(Integer pk) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(property(
                getPropertyObject(OutraInformacaoQuadroPosicaoFinanceira.class).getPk()), pk));
        return (OutraInformacaoQuadroPosicaoFinanceira) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<OutraInformacaoQuadroPosicaoFinanceira> buscarPorQuadroELayout(Integer pkQuadro,
            Integer pkLayout, boolean ordemCrescentePeriodo) {
        Criteria criteria = criarCriteria();
        addFiltroQuadro(pkQuadro, criteria);
        if (pkLayout != null) {
            criteria.add(Restrictions.eq(LAYOUT_PK, pkLayout));
        }
        addOrdemPorPeriodo(criteria, ordemCrescentePeriodo);
        return criteria.list();
    }

    private void addFiltroQuadro(Integer pkQuadro, Criteria criteria) {
        criteria.add(Restrictions.eq("quadro.pk", pkQuadro));
    }

}
