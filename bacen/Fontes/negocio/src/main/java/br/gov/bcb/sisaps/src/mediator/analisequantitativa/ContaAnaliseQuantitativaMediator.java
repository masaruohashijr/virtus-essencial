package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.ContaAnaliseQuantitativaETLDAO;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaAnaliseQuantitativaETL;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class ContaAnaliseQuantitativaMediator {
    @Autowired
    private ContaAnaliseQuantitativaETLDAO contaAnaliseQuantitativaETLDAO;

    public static ContaAnaliseQuantitativaMediator get() {
        return SpringUtilsExtended.get().getBean(ContaAnaliseQuantitativaMediator.class);
    }

    @Transactional(readOnly = true)
    public ContaAnaliseQuantitativaETL extrairContaAnaliseQuantitativaETL(ContaAnaliseQuantitativa conta,
            DataBaseES dataBaseES, PerfilRisco perfilRisco) {
        return contaAnaliseQuantitativaETLDAO.extrair(conta, dataBaseES, perfilRisco.getCiclo()
                .getEntidadeSupervisionavel().getConglomeradoOuCnpj());
    }

}
