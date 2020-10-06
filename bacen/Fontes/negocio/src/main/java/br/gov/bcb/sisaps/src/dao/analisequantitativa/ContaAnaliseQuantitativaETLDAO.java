package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;

@Repository
public class ContaAnaliseQuantitativaETLDAO extends GenericDAOLocal<ContaAnaliseQuantitativaETL, Integer> {

    private static final String CONTA = "conta";
    private static final String DATA_BASE_ES = "dataBaseES";
    private static final String PONTO = ".";

    public ContaAnaliseQuantitativaETLDAO() {
        super(ContaAnaliseQuantitativaETL.class);
    }

    public ContaAnaliseQuantitativaETL extrair(ContaAnaliseQuantitativa conta, DataBaseES dataBaseES, String cnpjES) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        if (dataBaseES != null && StringUtils.isNotBlank(cnpjES)) {
            criteria.createAlias(property(getPropertyObject(ContaAnaliseQuantitativaETL.class).getDataBaseES()),
                    DATA_BASE_ES);
            criteria.add(Restrictions.eq(DATA_BASE_ES + PONTO
                    + property(getPropertyObject(DataBaseES.class).getCodigoDataBase()), dataBaseES.getCodigoDataBase()));
            criteria.add(Restrictions.eq(DATA_BASE_ES + PONTO
                    + property(getPropertyObject(DataBaseES.class).getCnpjES()), cnpjES));
            criteria.createAlias(property(getPropertyObject(ContaAnaliseQuantitativaETL.class)
                    .getContaAnaliseQuantitativa()), CONTA);
            criteria.add(Restrictions.eq(CONTA + PONTO + "pk", conta.getPk()));
            return (ContaAnaliseQuantitativaETL) criteria.uniqueResult();
        }
        return null;
    }
}
