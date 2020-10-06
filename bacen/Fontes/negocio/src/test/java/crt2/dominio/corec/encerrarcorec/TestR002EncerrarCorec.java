package crt2.dominio.corec.encerrarcorec;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002EncerrarCorec extends ConfiguracaoTestesNegocio {

    public String concluirCorec(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return EncerrarCorecMediator.get().encerrarCorecCriarNovoCiclo(ciclo);
    }
    
}
