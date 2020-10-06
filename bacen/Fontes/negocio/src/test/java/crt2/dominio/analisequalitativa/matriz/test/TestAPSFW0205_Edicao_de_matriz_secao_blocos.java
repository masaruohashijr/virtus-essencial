package crt2.dominio.analisequalitativa.matriz.test;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestAPSFW0205_Edicao_de_matriz_secao_blocos extends ConfiguracaoTestesNegocio {

    @Autowired
    private MatrizCicloMediator matrizMediator;
    private Matriz matriz;

    public void salvar(int pkMatriz, String percentual) {
        matriz = matrizMediator.loadPK(pkMatriz);
        matriz.setNumeroFatorRelevanciaUC(new BigDecimal(percentual).divide(new BigDecimal(100)));

        alterarDadosPercentual();
    }

    private void alterarDadosPercentual() {
        erro = null;
        try {
            matrizMediator.alterarPercentualUC(matriz, matriz.getNumeroFatorRelevanciaUC());
        } catch (NegocioException e) {
            erro = e;
        }

    }

}