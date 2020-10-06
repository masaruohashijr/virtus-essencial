package crt2.dominio.analisequalitativa.liberarmatriz;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002LiberarMatrizRisco extends ConfiguracaoTestesNegocio {

    private String mensagem;

    public void liberarMatriz(Integer idMatriz) {
        erro = null;
        try {
            Matriz matriz = MatrizCicloMediator.get().buscar(idMatriz);
            MatrizCicloMediator.get().liberarMatrizCiclo(matriz);
        } catch (NegocioException e) {
            erro = e;
            mensagem = e.getMessage();
        }
    }

    public String getMensagem() {
        return formatarMensagem(mensagem);
    }

}
