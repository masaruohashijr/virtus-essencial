package br.gov.bcb.sisaps.dao;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.app.stuff.hibernate.IObjetoPersistente;

public abstract class GenericDAOLocal<T extends IObjetoPersistente<PK>, PK extends Serializable> extends GenericDAO<T, PK> {

    public GenericDAOLocal(Class<T> type) {
        super(type);
    }
    
    @Transactional
    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }
    
    @Transactional
    public void refresh(Object obj) {
        getSessionFactory().getCurrentSession().refresh(obj);
    }
    
    public <D extends Serializable> void desconectar(D obj) {
        if (getCurrentSession().contains(obj)) {
            getCurrentSession().evict(obj);
        }
    }
}
