package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasArcsQualitativa;

@Repository
@Transactional
public class NotasArcsQualitativaDAO extends GenericDAOLocal<NotasArcsQualitativa, Integer> {

    public NotasArcsQualitativaDAO() {
        super(NotasArcsQualitativa.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasArcsQualitativa ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
