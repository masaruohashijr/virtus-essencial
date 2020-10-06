package crt2.dominio.consultanaobloqueados.painelconsulta;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002PainelConsultaNaoBloqueados extends ConfiguracaoTestesNegocio {
    
    private Integer idCiclo;
    
    public String mensagem() {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return EntidadeSupervisionavelMediator.get().possuiBloqueioES(ciclo);
    }
    
    public void setCiclo(Integer ciclo) {
        this.idCiclo = ciclo;
    }

}
