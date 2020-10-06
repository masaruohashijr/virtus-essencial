package crt2.dominio.corec.botaoconcluircorec;

import org.junit.Ignore;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;
@Ignore
public class TestR001BotaoConcluirCorec extends ConfiguracaoTestesNegocio {
    
    public String encerrarCorec(int idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        try {
            return EncerrarCorecMediator.get().agendarEncerramentoCorec(ciclo);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMensagens().get(0).getMessage();
    }

}
