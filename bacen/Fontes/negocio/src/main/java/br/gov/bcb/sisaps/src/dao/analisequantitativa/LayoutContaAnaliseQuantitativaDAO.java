package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;

@Repository
public class LayoutContaAnaliseQuantitativaDAO extends GenericDAOLocal<LayoutContaAnaliseQuantitativa, Integer> {

    private static final String LAQ = "laq";
    private static final String LAYOUT_ANALISE_QUANTITATIVA = "layoutAnaliseQuantitativa";
    private static final String CONTA_ANALISE_QUANTITATIVA = "contaAnaliseQuantitativa";

    public LayoutContaAnaliseQuantitativaDAO() {
        super(LayoutContaAnaliseQuantitativa.class);
    }

    @SuppressWarnings("unchecked")
    public List<LayoutContaAnaliseQuantitativa> obterLayout(DataBaseES dataBaseES) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria criteriaProjetada = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(LAYOUT_ANALISE_QUANTITATIVA, LAQ);
        criteria.createAlias(CONTA_ANALISE_QUANTITATIVA, CONTA_ANALISE_QUANTITATIVA);
        criteriaProjetada.createAlias(LAYOUT_ANALISE_QUANTITATIVA, LAQ);

        String propriedadeDataFim = "laq.codigoDataBaseFim";
        String propriedadeDataInicio = "laq.codigoDataBaseInicio";

        SimpleExpression geDataFim = Restrictions.ge(propriedadeDataFim, dataBaseES.getCodigoDataBase());
        SimpleExpression leDataIni = Restrictions.le(propriedadeDataInicio, dataBaseES.getCodigoDataBase());

        criteriaProjetada.add(Restrictions.or(Restrictions.and(leDataIni, geDataFim),
                Restrictions.and(leDataIni, Restrictions.isNull(propriedadeDataFim))));
        criteriaProjetada.setProjection(Projections.max(propriedadeDataInicio));

        criteria.add(Restrictions.eq(propriedadeDataInicio, criteriaProjetada.uniqueResult()));
        
        criteria.addOrder(Order.asc("contaAnaliseQuantitativa.pk"));
        criteria.addOrder(Order.asc("sequencial"));
        return (List<LayoutContaAnaliseQuantitativa>) criteria.list();
    }

    public LayoutContaAnaliseQuantitativa obterLayoutPai(Integer pkContaPai, Integer pklayoutAnalise,
            TipoConta tipoConta) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(
                LayoutContaAnaliseQuantitativa.class).getContaAnaliseQuantitativaPai()), "contaPai");

        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(
                LayoutContaAnaliseQuantitativa.class).getContaAnaliseQuantitativa()), "conta");
        criteria.add(Restrictions.eq("conta.tipoConta", tipoConta));
        criteria.add(Restrictions.eq("conta.pk", pkContaPai));
        criteria.createAlias(PropertyUtils.property(PropertyUtils.getPropertyObject(
                LayoutContaAnaliseQuantitativa.class).getLayoutAnaliseQuantitativa()), "layout_analise");
        criteria.add(Restrictions.eq("layout_analise.pk", pklayoutAnalise));
        return (LayoutContaAnaliseQuantitativa) criteria.uniqueResult();
    }

}
