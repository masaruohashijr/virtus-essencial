package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;

@Repository
public class AvaliacaoAQTDAO extends GenericDAOLocal<AvaliacaoAQT, Integer> {

    public AvaliacaoAQTDAO() {
        super(AvaliacaoAQT.class);
    }

    public AvaliacaoAQT buscarNotaAvaliacaoANEF(AnaliseQuantitativaAQT aqt, PerfisNotificacaoEnum perfil) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("analiseQuantitativaAQT", aqt));
        criteria.add(Restrictions.eq("perfil", perfil));
        return (AvaliacaoAQT) criteria.uniqueResult();
    }

    
    
    public AvaliacaoAQT buscarAvaliacaoPorPK(Integer id) {
        Criteria criteria = getCurrentSession().createCriteria(AvaliacaoAQT.class);
        criteria.add(Restrictions.eq("pk", id));
        return (AvaliacaoAQT) criteria.uniqueResult();
    }
}
