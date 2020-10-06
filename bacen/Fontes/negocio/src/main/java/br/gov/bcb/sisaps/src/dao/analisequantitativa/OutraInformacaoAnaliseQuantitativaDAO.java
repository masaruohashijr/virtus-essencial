package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;

@Repository
@Transactional(readOnly = true)
public class OutraInformacaoAnaliseQuantitativaDAO 
    extends GenericDAOLocal<OutraInformacaoAnaliseQuantitativa, Integer> {

    public OutraInformacaoAnaliseQuantitativaDAO() {
        super(OutraInformacaoAnaliseQuantitativa.class);
    }
    
    public OutraInformacaoAnaliseQuantitativa buscarPorNome(String nome, TipoInformacaoEnum tipoInformacaoEnum) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("nome", nome));
        criteria.add(Restrictions.eq("tipoInformacao", tipoInformacaoEnum));
        return (OutraInformacaoAnaliseQuantitativa) criteria.uniqueResult();
    }

}
