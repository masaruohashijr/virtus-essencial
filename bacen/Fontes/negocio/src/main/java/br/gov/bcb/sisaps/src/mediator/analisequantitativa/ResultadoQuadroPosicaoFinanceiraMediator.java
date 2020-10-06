package br.gov.bcb.sisaps.src.mediator.analisequantitativa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dao.analisequantitativa.ResultadoQuadroPosicaoFinanceiraDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

@Service
public class ResultadoQuadroPosicaoFinanceiraMediator {

    @Autowired
    private ResultadoQuadroPosicaoFinanceiraDAO resultadoQuadroPosicaoFinanceiraDAO;

    public static ResultadoQuadroPosicaoFinanceiraMediator get() {
        return SpringUtilsExtended.get().getBean(ResultadoQuadroPosicaoFinanceiraMediator.class);
    }

    @Transactional(readOnly = true)
    public List<ResultadoQuadroPosicaoFinanceira> obterResultados(QuadroPosicaoFinanceira quadro) {
        return resultadoQuadroPosicaoFinanceiraDAO.obterResultados(quadro);
    }

    @Transactional(readOnly = true)
    public ResultadoQuadroPosicaoFinanceira buscarResultadoPorPk(Integer pk) {
        return resultadoQuadroPosicaoFinanceiraDAO.load(pk);
    }

    @Transactional
    public void alterar(ResultadoQuadroPosicaoFinanceira resultado) {
        resultadoQuadroPosicaoFinanceiraDAO.update(resultado);
    }
}
