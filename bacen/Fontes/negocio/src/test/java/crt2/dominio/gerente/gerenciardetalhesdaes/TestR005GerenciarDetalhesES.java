package crt2.dominio.gerente.gerenciardetalhesdaes;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.SituacaoESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005GerenciarDetalhesES extends ConfiguracaoTestesNegocio {

    private String mensagem = "";
    
    public void confirmar(int ciclopk) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(ciclopk);
        SituacaoES situacaoES = SituacaoESMediator.get().getUltimaSituacaoES(ciclo);
        mensagem = SituacaoESMediator.get().confirmarNovaSituacao(situacaoES);
    }
    
    public String getMensagem() {
        return mensagem;
    }
}