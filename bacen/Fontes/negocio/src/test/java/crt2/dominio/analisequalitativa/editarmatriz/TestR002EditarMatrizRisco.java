package crt2.dominio.analisequalitativa.editarmatriz;

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002EditarMatrizRisco extends ConfiguracaoTestesNegocio {

    private String mensagem;

    public void liberarMatriz(Integer idMatriz, BigDecimal percentual) {
        erro = null;
        try {
            BigDecimal percFinal = null;
            Matriz matriz = MatrizCicloMediator.get().buscar(idMatriz);
            if (percentual.intValue() == 100) {
                percFinal = percentual.divide(new BigDecimal(100));
            } else {
                percFinal = percentual;
            }
            mensagem =    MatrizCicloMediator.get().alterarPercentualUC(matriz, percFinal);
        } catch (NegocioException e) {
            erro = e;
            mensagem = e.getMessage();
        }
    }

    public String getMensagem() {
        return formatarMensagem(mensagem);
    }
}
