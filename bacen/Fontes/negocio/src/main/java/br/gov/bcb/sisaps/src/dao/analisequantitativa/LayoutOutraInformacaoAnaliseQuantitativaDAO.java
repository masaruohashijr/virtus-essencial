package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;

@Repository
@Transactional(readOnly = true)
public class LayoutOutraInformacaoAnaliseQuantitativaDAO 
    extends GenericDAOLocal<LayoutOutraInformacaoAnaliseQuantitativa, Integer> {

    private static final String LAQ_CODIGO_DATA_BASE_INICIO = "laq.codigoDataBaseInicio";
    private static final String SEQUENCIAL = "sequencial";
    private static final String INFO_TIPO_INFORMACAO = "info.tipoInformacao";
    private static final String INFO = "info";
    private static final String OUTRA_INFORMACAO_ANALISE_QUANTITATIVA = "outraInformacaoAnaliseQuantitativa";
    private static final String LAQ = "laq";
    private static final String LAYOUT_ANALISE_QUANTITATIVA = "layoutAnaliseQuantitativa";

    public LayoutOutraInformacaoAnaliseQuantitativaDAO() {
        super(LayoutOutraInformacaoAnaliseQuantitativa.class);
    }

    @SuppressWarnings("unchecked")
    public List<LayoutOutraInformacaoAnaliseQuantitativa> obterLayout(
            DataBaseES dataBaseES, TipoInformacaoEnum tipoInformacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        Criteria criteriaProjetada = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(OUTRA_INFORMACAO_ANALISE_QUANTITATIVA, INFO);
        criteria.createAlias(LAYOUT_ANALISE_QUANTITATIVA, LAQ);
        criteriaProjetada.createAlias(LAYOUT_ANALISE_QUANTITATIVA, LAQ);

        String propriedadeDataFim = "laq.codigoDataBaseFim";
        String propriedadeDataInicio = LAQ_CODIGO_DATA_BASE_INICIO;

        SimpleExpression geDataFim = Restrictions.ge(propriedadeDataFim, dataBaseES.getCodigoDataBase());
        SimpleExpression leDataIni = Restrictions.le(propriedadeDataInicio, dataBaseES.getCodigoDataBase());

        criteriaProjetada.add(Restrictions.or(Restrictions.and(leDataIni, geDataFim),
                Restrictions.and(leDataIni, Restrictions.isNull(propriedadeDataFim))));
        criteriaProjetada.setProjection(Projections.max(propriedadeDataInicio));

        criteria.add(Restrictions.eq(propriedadeDataInicio, criteriaProjetada.uniqueResult()));
        
        criteria.add(Restrictions.eq(INFO_TIPO_INFORMACAO, tipoInformacao));
        criteria.addOrder(Order.asc(SEQUENCIAL));
        return (List<LayoutOutraInformacaoAnaliseQuantitativa>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<LayoutOutraInformacaoAnaliseQuantitativa> obterLayoutsOutraInformacaoQuadro(Integer pkQuadro,
            TipoInformacaoEnum tipoInformacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(OUTRA_INFORMACAO_ANALISE_QUANTITATIVA, INFO);
        Criteria criteriaOutraInfo = criteria.createCriteria("outrasInformacaoQuadroPosicaoFinanceiras");
        criteriaOutraInfo.createAlias("quadroPosicaoFinanceira", "quadro");
        criteria.add(Restrictions.eq(INFO_TIPO_INFORMACAO, tipoInformacao));
        criteriaOutraInfo.add(Restrictions.eq("quadro.pk", pkQuadro));
        criteria.addOrder(Order.asc(SEQUENCIAL));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
    
    public LayoutOutraInformacaoAnaliseQuantitativa obterLayoutOutraInformacaoQuadro(Integer codigoDataBase,
            TipoInformacaoEnum tipoInformacao, String nomeOutraInfo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(OUTRA_INFORMACAO_ANALISE_QUANTITATIVA, INFO);
        criteria.createAlias(LAYOUT_ANALISE_QUANTITATIVA, LAQ);
        
        Criteria criteriaProjetada = getCurrentSession().createCriteria(LayoutAnaliseQuantitativa.class);
        String propriedadeDataFim = "codigoDataBaseFim";
        String propriedadeDataInicio = "codigoDataBaseInicio";
        SimpleExpression geDataFim = Restrictions.ge(propriedadeDataFim, codigoDataBase);
        SimpleExpression leDataIni = Restrictions.le(propriedadeDataInicio, codigoDataBase);
        criteriaProjetada.add(Restrictions.or(Restrictions.and(leDataIni, geDataFim),
                Restrictions.and(leDataIni, Restrictions.isNull(propriedadeDataFim))));
        criteriaProjetada.setProjection(Projections.max(propriedadeDataInicio));
        
        criteria.add(Restrictions.eq(LAQ_CODIGO_DATA_BASE_INICIO, criteriaProjetada.uniqueResult()));
        criteria.add(Restrictions.eq(INFO_TIPO_INFORMACAO, tipoInformacao));
        criteria.add(Restrictions.eq("info.nome", nomeOutraInfo));
        return (LayoutOutraInformacaoAnaliseQuantitativa) criteria.uniqueResult();
    }

}
