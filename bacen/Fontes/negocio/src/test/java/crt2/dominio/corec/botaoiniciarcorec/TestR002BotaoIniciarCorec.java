package crt2.dominio.corec.botaoiniciarcorec;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002BotaoIniciarCorec extends ConfiguracaoTestesNegocio {

    public void iniciarCiclo(int ciclo) {
        erro = null;
        try {
            Ciclo cicloInic = CicloMediator.get().buscarCicloPorPK(ciclo);
            CicloMediator.get().iniciarCorec(cicloInic);
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
