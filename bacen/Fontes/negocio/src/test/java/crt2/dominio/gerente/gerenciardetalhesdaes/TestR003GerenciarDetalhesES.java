package crt2.dominio.gerente.gerenciardetalhesdaes;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003GerenciarDetalhesES extends ConfiguracaoTestesNegocio {

    private String mensagem = "";

    public void confirmar(int ciclopk) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(ciclopk);
        ConclusaoES conclusaoES = ConclusaoESMediator.get().getUltimaConclusaoES(ciclo);
        mensagem = ConclusaoESMediator.get().confirmarNovaConclusao(conclusaoES);
    }
    
    public String getMensagem() {
        return mensagem;
    }
}
