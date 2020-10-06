package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasComponentesQuantitativa;

@Repository
@Transactional
public class NotasComponentesQuantitativaDAO extends GenericDAOLocal<NotasComponentesQuantitativa, Integer> {

    public NotasComponentesQuantitativaDAO() {
        super(NotasComponentesQuantitativa.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasComponentesQuantitativa ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
