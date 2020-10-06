package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.CachePersistente;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Repository
@Transactional(readOnly = true)
public class CacheDAO extends GenericDAO<CachePersistente<?>, Integer> {

    public CacheDAO() {
        super();
    }
    
    public CachePersistente<?> buscarPorChave(String chave) {
        Criteria criteria = getCurrentSession().createCriteria(CachePersistente.class);
        criteria.add(Restrictions.eq("chave", chave));
        return (CachePersistente<?>) criteria.uniqueResult();
    }
    
    @Transactional
    public void salvar(CachePersistente<?> objeto) {
        if (!SisapsUtil.isExecucaoTeste()) {
            CachePersistente<?> cachePersistente = buscarPorChave(objeto.getChave());
            if (cachePersistente == null) {
                save(objeto);
            } else {
                cachePersistente.setElemento(objeto.getElemento());
                cachePersistente.setDataHora(objeto.getDataHora());
                update(cachePersistente);
                getCurrentSession().flush();
            }
        }
    }
}
