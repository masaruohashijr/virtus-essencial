package br.gov.bcb.sisaps.src.dao.analisequantitativa;

import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutAnaliseQuantitativa;

@Repository
public class LayoutAnaliseQuantitativaDAO extends GenericDAOLocal<LayoutAnaliseQuantitativa, Integer> {

    public LayoutAnaliseQuantitativaDAO() {
        super(LayoutAnaliseQuantitativa.class);
    }

}
