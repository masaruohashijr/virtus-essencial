package br.gov.bcb.sisaps.src.dao;

import org.springframework.stereotype.Repository;

import br.gov.bcb.app.stuff.dao.GenericDAO;
import br.gov.bcb.sisaps.src.dominio.Documento;

@Repository
public class DocumentoDAO extends GenericDAO<Documento, Integer> {
    
    private static final long serialVersionUID = 1L;

    public DocumentoDAO() {
        super(Documento.class);
    }
    
    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }

}
