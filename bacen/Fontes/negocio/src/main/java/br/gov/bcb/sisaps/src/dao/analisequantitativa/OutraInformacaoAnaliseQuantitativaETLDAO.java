package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativaETL;
import br.gov.bcb.sisaps.util.Constantes;

@Repository
public class OutraInformacaoAnaliseQuantitativaETLDAO 
    extends GenericDAOLocal<OutraInformacaoAnaliseQuantitativaETL, Integer> {

    private static final String CODIGO_DATA_BASE = "codigoDataBase";
    private static final String DATA_BASE_ES = "dataBaseES";
    private static final String OUTRA_INFO = "outraInfo";

    public OutraInformacaoAnaliseQuantitativaETLDAO() {
        super(OutraInformacaoAnaliseQuantitativaETL.class);
    }

    public OutraInformacaoAnaliseQuantitativaETL extrair(DataBaseES dataBaseES, String cnpjES, 
            OutraInformacaoAnaliseQuantitativa outraInformacao) {
        Criteria criteria = criarCriteriaExtracao(cnpjES, outraInformacao);
        criteria.add(Restrictions.eq(DATA_BASE_ES + Constantes.PONTO + CODIGO_DATA_BASE, dataBaseES.getCodigoDataBase()));
        return (OutraInformacaoAnaliseQuantitativaETL) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<OutraInformacaoAnaliseQuantitativaETL> buscarValoresSemestraisPorDataBaseExtracao(
            DataBaseES dataBaseES, String cnpjES, OutraInformacaoAnaliseQuantitativa outraInformacao, 
            List<Integer> codigosSemestres) {
        Criteria criteria = criarCriteriaExtracao(cnpjES, outraInformacao);
        criteria.add(Restrictions.in(DATA_BASE_ES + Constantes.PONTO + CODIGO_DATA_BASE, codigosSemestres));
        return criteria.list();
    }

    private Criteria criarCriteriaExtracao(String cnpjES, OutraInformacaoAnaliseQuantitativa outraInformacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(OutraInformacaoAnaliseQuantitativaETL.class)
                .getOutraInformacaoAnaliseQuantitativa()), OUTRA_INFO);
        criteria.createAlias(property(getPropertyObject(OutraInformacaoAnaliseQuantitativaETL.class)
                .getDataBaseES()), DATA_BASE_ES);
        criteria.add(Restrictions.eq(DATA_BASE_ES + Constantes.PONTO + "cnpjES", cnpjES));
        criteria.add(Restrictions.eq(OUTRA_INFO + Constantes.PONTO + "pk", outraInformacao.getPk()));
        return criteria;
    }
    
}
