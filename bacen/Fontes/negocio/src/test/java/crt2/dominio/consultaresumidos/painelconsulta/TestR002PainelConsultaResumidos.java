package crt2.dominio.consultaresumidos.painelconsulta;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002PainelConsultaResumidos extends ConfiguracaoTestesNegocio {
    private Integer ciclo;

    public String mensagem() {
        Ciclo cicloLoad = CicloMediator.get().buscarCicloPorPK(ciclo);
        return EntidadeSupervisionavelMediator.get().possuiBloqueioES(cicloLoad);
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

}
