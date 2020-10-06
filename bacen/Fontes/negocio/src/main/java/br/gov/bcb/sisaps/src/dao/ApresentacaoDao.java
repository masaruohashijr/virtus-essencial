package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;

@Transactional
@Repository
public class ApresentacaoDao extends GenericDAOLocal<Apresentacao, Integer> {

	// Construtor
	public ApresentacaoDao() {
		super(Apresentacao.class);
	}
	
	
	public Apresentacao buscarPorCiclo(Integer pkCiclo){
	    Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("ciclo.pk", pkCiclo));
        return (Apresentacao) criteria.uniqueResult();
	}
	

}
