package crt2.dominio.analisequalitativa.retomaranalisearc;

import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001RetomarAnaliseARC extends ConfiguracaoTestesNegocio {

    public void retomarARC(int arcpk) {

        AvaliacaoRiscoControleMediator.get().alterarEstadoARCBotaoAnalisar(
                AvaliacaoRiscoControleMediator.get().buscar(arcpk));
    }
}
