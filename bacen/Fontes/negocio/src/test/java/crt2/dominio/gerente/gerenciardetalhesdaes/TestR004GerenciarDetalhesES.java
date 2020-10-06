package crt2.dominio.gerente.gerenciardetalhesdaes;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004GerenciarDetalhesES extends ConfiguracaoTestesNegocio {

    private String mensagem = "";
    
    public void confirmar(int ciclopk) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(ciclopk);
        PerspectivaES perspectivaES = PerspectivaESMediator.get().getUltimaPerspectivaES(ciclo);
        mensagem = PerspectivaESMediator.get().confirmarNovaPerspectiva(perspectivaES);
    }
    
    public String getMensagem() {
        return mensagem;
    }
}