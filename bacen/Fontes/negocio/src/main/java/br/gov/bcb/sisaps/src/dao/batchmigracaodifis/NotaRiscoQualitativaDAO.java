package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasRiscoControleQualitativa;

@Repository
@Transactional
public class NotaRiscoQualitativaDAO extends GenericDAOLocal<NotasRiscoControleQualitativa, Integer> {

    public NotaRiscoQualitativaDAO() {
        super(NotasRiscoControleQualitativa.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM NotasRiscoControleQualitativa ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
