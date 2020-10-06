package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;

@Repository
public class AvaliacaoARCDAO extends GenericDAO<AvaliacaoARC, Integer> {

    public AvaliacaoARC buscarArcPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(AvaliacaoARC.class);
        criteria.add(Restrictions.eq("pk", id));
        return (AvaliacaoARC) criteria.uniqueResult();
    }
    
    public AvaliacaoARC buscarPorIdArcEtipo(Integer pk, PerfisNotificacaoEnum tipo) {
        Criteria criteria = getCurrentSession().createCriteria(AvaliacaoARC.class);
        criteria.add(Restrictions.eq("avaliacaoRiscoControle.pk", pk));
        criteria.add(Restrictions.eq("perfil", tipo));
        return (AvaliacaoARC) criteria.uniqueResult();
    }

}
