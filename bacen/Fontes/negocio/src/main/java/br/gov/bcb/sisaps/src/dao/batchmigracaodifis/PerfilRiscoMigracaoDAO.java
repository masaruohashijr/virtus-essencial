package br.gov.bcb.sisaps.src.dao.batchmigracaodifis;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.PerfilRiscoMigracao;

@Repository
@Transactional
public class PerfilRiscoMigracaoDAO extends GenericDAOLocal<PerfilRiscoMigracao, Integer> {

    public PerfilRiscoMigracaoDAO() {
        super(PerfilRiscoMigracao.class);
    }

    public void excluirNotas() {
        StringBuilder hql = new StringBuilder();
        hql.append("DELETE FROM PerfilRiscoMigracao ");
        Query query = getCurrentSession().createQuery(hql.toString());
        query.executeUpdate();
    }

}
