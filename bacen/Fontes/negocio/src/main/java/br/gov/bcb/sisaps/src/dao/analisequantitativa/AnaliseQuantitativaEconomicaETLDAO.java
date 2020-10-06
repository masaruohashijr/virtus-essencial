package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.AnaliseQuantitativaEconomicaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;

@Repository
public class AnaliseQuantitativaEconomicaETLDAO extends GenericDAO<AnaliseQuantitativaEconomicaETL, Integer> {

    private static final int QTD_3_SEMESTRES_ANTERIORES = 3;
    private static final String CODIGO_DATA_BASE = property(getPropertyObject(DataBaseES.class).getCodigoDataBase());
    private static final String CODIGO_CNPJ_DATA_BASE = property(getPropertyObject(DataBaseES.class).getCnpjES());
    private static final String DATA_BASE_ES = "dataBaseES";
    private static final String PONTO = ".";
    private int contador;

    public AnaliseQuantitativaEconomicaETLDAO() {
        super(AnaliseQuantitativaEconomicaETL.class);
    }

    @Transactional(readOnly = true)
    public AnaliseQuantitativaEconomicaETL extrair(DataBaseES dataBaseES, String cnpjES) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        if (dataBaseES != null && StringUtils.isNotBlank(cnpjES)) {
            criteria.createAlias(property(getPropertyObject(AnaliseQuantitativaEconomicaETL.class).getDataBaseES()),
                    DATA_BASE_ES);
            criteria.add(Restrictions.eq(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE, dataBaseES.getCodigoDataBase()));
            criteria.add(Restrictions.eq(DATA_BASE_ES + PONTO + CODIGO_CNPJ_DATA_BASE, cnpjES));
            return (AnaliseQuantitativaEconomicaETL) criteria.uniqueResult();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<AnaliseQuantitativaEconomicaETL> buscarValoresSemestraisPorDataBaseExtracao(
            DataBaseES dataBaseExtracao, String cnpj) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.createAlias(property(getPropertyObject(AnaliseQuantitativaEconomicaETL.class).getDataBaseES()),
                DATA_BASE_ES);
        List<Integer> identificadores = new ArrayList<Integer>();
        identificadores.add(dataBaseExtracao.getCodigoDataBase());
        this.contador = QTD_3_SEMESTRES_ANTERIORES;
        buildCriteriaProjectionsBetween(identificadores, dataBaseExtracao.getCodigoDataBase());
        criteria.add(Restrictions.in(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE, identificadores));
        criteria.add(Restrictions.eq(DATA_BASE_ES + PONTO + CODIGO_CNPJ_DATA_BASE, cnpj));
        criteria.addOrder(Order.desc(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE));
        return (List<AnaliseQuantitativaEconomicaETL>) criteria.list();
    }

    private List<Integer> buildCriteriaProjectionsBetween(List<Integer> identificadores, Integer data) {
        if (data != null) {
            for (int i = 0; i < contador; i++) {
                Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
                criteria.createAlias(
                        property(getPropertyObject(AnaliseQuantitativaEconomicaETL.class).getDataBaseES()),
                        DATA_BASE_ES);
                String identificador = String.valueOf(data);
                int mes = Integer.valueOf(identificador.substring(4));
                int ano = Integer.valueOf(identificador.substring(0, 4));
                if (mes >= 1 && mes <= 6) {
                    criteria.add(Restrictions.between(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE,
                            Integer.valueOf(String.valueOf(ano - 1 + "07")),
                            Integer.valueOf(String.valueOf(ano - 1 + "12"))));
                } else {
                    criteria.add(Restrictions.between(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE,
                            Integer.valueOf(String.valueOf(ano - 1 + "01")),
                            Integer.valueOf(String.valueOf(ano + "06"))));
                }
                criteria.setProjection(Projections.max(DATA_BASE_ES + PONTO + CODIGO_DATA_BASE));
                Integer dataresultado = (Integer) criteria.uniqueResult();
                identificadores.add(dataresultado);
                contador--;
                buildCriteriaProjectionsBetween(identificadores, dataresultado);
            }
        }

        return identificadores;
    }
}
