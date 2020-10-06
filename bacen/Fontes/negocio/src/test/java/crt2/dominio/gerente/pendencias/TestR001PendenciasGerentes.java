package crt2.dominio.gerente.pendencias;

import java.util.List;

import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001PendenciasGerentes extends ConfiguracaoTestesNegocio {

    public List<CicloVO> getPendenciasGerente() {
        return CicloMediator.get().getPendenciasGerente();
    }
    
    public String getID(CicloVO ciclo) {
        return ciclo.getPk().toString();
    }

    public String getES(CicloVO ciclo) {
        return ciclo.getNomeES();
    }
    
}
