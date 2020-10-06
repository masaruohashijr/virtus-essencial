package br.gov.bcb.sisaps.src.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.SupervisaoCiclo;

@Repository
public class SupervisaoCicloDao extends GenericDAOLocal<SupervisaoCiclo, Integer> {

    private static final String UNCHECKED = "unchecked";

    public SupervisaoCicloDao() {
        super(SupervisaoCiclo.class);
    }

    @SuppressWarnings(UNCHECKED)
    public SupervisaoCiclo buscarSupervisao(Date dataBase, String localizacao) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("localizacao", localizacao));
        criteria.add(Restrictions.and(Restrictions.le("dataInicio", dataBase),
                Restrictions.ge("dataFim", dataBase)));
        List<SupervisaoCiclo> resultado = criteria.list();
        return CollectionUtils.isNotEmpty(resultado) ? (SupervisaoCiclo) resultado.get(0) : null;
    }
    
}
