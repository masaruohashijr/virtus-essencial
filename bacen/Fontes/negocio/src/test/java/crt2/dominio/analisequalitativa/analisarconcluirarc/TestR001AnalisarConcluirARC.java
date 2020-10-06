package crt2.dominio.analisequalitativa.analisarconcluirarc;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AnalisarConcluirARC extends ConfiguracaoTestesNegocio {
    
    private String mensagem;
    
    public void concluirAnaliseArc(Integer idArc) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(1);
        mensagem = AvaliacaoRiscoControleMediator.get().concluirAnaliseARCSupervisor(ciclo, idArc);
    }
    
    public String getMensagem() {
        return mensagem;
    }

}
