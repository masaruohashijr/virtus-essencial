package crt2.dominio.poscorec.botaoencerrarciclo;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002BotaoEncerrarCiclo extends ConfiguracaoTestesNegocio {
    public String encerrarCiclo(Integer pkCiclo) {
        Ciclo loadPK = CicloMediator.get().loadPK(pkCiclo);
        return CicloMediator.get().encerrarCiclo(loadPK);
    }
}
