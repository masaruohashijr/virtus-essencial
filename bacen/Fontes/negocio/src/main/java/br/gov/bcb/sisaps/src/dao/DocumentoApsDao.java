package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.DocumentoAPS;

@Repository
public class DocumentoApsDao extends GenericDAOLocal<DocumentoAPS, Integer> {

    public DocumentoApsDao() {
        super(DocumentoAPS.class);
    }
    
    public DocumentoAPS buscarDocumento(Integer codigo) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("pk", codigo));
        return (DocumentoAPS) criteria.uniqueResult();
    }

}
