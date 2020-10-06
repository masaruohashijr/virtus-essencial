package crt2.dominio.poscorec.gestaoposcorec;

import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR006GestaoPosCorec extends ConfiguracaoTestesNegocio {

    public String excluirOficio(Integer ciclo) {
        return AnexoPosCorecMediator.get().excluirAnexo(CicloMediator.get().buscarCicloPorPK(ciclo), "Ofício");
    }

}
