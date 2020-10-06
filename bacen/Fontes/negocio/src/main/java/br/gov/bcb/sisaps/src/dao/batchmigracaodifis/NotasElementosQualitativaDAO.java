package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasElementosQualitativa;

@Repository
@Transactional
public class NotasElementosQualitativaDAO extends GenericDAOLocal<NotasElementosQualitativa, Integer> {

    public NotasElementosQualitativaDAO() {
        super(NotasElementosQualitativa.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasElementosQualitativa ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
