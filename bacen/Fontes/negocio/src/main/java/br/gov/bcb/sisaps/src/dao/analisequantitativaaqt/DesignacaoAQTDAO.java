package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.DesignacaoAQT;

@Repository
public class DesignacaoAQTDAO extends GenericDAOLocal<DesignacaoAQT, Integer> {

    public DesignacaoAQTDAO() {
        super(DesignacaoAQT.class);
    }

}
