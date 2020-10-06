package br.gov.bcb.sisaps.src.dao;

import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumentoAPS;

@Repository
public class AnexoDocumentoApsDao extends GenericDAOLocal<AnexoDocumentoAPS, Integer> {

    public AnexoDocumentoApsDao() {
        super(AnexoDocumentoAPS.class);
    }
    

}
