package crt2.dominio.analisequalitativa.matriz.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroItemElementoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_18LiberacaoVersionamentoMatriz extends ConfiguracaoTestesNegocio {

    @Autowired
    private MatrizCicloMediator matrizCicloMediator;

    @Autowired
    private AtividadeMediator atividadeMediator;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Autowired
    private ParametroElementoMediator parametroElementoMediator;

    @Autowired
    private ParametroItemElementoMediator parametroItemElementoMediator;

    public void liberarMatriz(int pkMatriz) {
        Matriz matriz = matrizCicloMediator.loadPK(pkMatriz);
        matrizCicloMediator.inicializarDependencias(matriz);
        liberarEditarMatriz(matriz, true);
    }

    public void editarMatriz(int pkMatriz) {
        Matriz matriz = matrizCicloMediator.loadPK(pkMatriz);
        matrizCicloMediator.inicializarDependencias(matriz);
        liberarEditarMatriz(matriz, false);
    }

    private void liberarEditarMatriz(Matriz matriz, boolean isLiberar) {
        erro = null;
        try {
            if (isLiberar) {
                matrizCicloMediator.liberarMatrizCiclo(matriz);
            } else {
                matrizCicloMediator.editarMatrizCiclo(matriz,
                        PerfilRiscoMediator.get().obterPerfilRiscoAtual(matriz.getCiclo().getPk()));
            }
        } catch (NegocioException e) {
            erro = e;
        }
    }

   
}