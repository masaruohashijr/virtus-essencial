package crt2.dominio.corec.alterarstatusciclo;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AlterarStatusCiclo extends ConfiguracaoTestesNegocio {

    public void alterarCiclo(int ciclo) {
        erro = null;
        try {
            Ciclo cicloInic = CicloMediator.get().buscarCicloPorPK(ciclo);
            CicloMediator.get().alterarStatusCiclo(cicloInic);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
