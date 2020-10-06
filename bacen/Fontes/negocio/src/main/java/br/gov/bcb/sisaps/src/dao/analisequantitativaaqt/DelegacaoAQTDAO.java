package br.gov.bcb.sisaps.src.dao.analisequantitativaaqt;

import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.DelegacaoAQT;

@Repository
public class DelegacaoAQTDAO extends GenericDAOLocal<DelegacaoAQT, Integer> {

    public DelegacaoAQTDAO() {
        super(DelegacaoAQT.class);
    }

}
