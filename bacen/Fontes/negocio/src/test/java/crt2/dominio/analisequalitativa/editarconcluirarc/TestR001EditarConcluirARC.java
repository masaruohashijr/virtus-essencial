package crt2.dominio.analisequalitativa.editarconcluirarc;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001EditarConcluirARC extends ConfiguracaoTestesNegocio {
    
    private String mensagem;
    
    public void concluirEdicaoArc(Integer idArc) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(2);
        mensagem = AvaliacaoRiscoControleMediator.get().concluirEdicaoARCInspetor(ciclo, idArc);
    }
    
    public String getMensagem() {
        return mensagem;
    }

}
