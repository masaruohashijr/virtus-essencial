package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasElementosQuantitativa;

@Repository
@Transactional
public class NotasElementosQuantitativaDAO extends GenericDAOLocal<NotasElementosQuantitativa, Integer> {

    public NotasElementosQuantitativaDAO() {
        super(NotasElementosQuantitativa.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasElementosQuantitativa ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
