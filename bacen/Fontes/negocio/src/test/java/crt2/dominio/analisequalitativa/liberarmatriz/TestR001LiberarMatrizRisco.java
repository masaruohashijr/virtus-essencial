package crt2.dominio.analisequalitativa.liberarmatriz;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001LiberarMatrizRisco extends ConfiguracaoTestesNegocio {

    private String mensagem;

    public void liberarMatriz(Integer idMatriz, Integer idCiclo) {
        Matriz matriz = MatrizCicloMediator.get().buscar(idMatriz);
        mensagem = MatrizCicloMediator.get().liberarMatrizCiclo(matriz);
    }

    public String getMensagem() {
        return mensagem;
    }

}
