package crt2.dominio.poscorec.botaoencerrarciclo;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001BotaoEncerrarCiclo extends ConfiguracaoTestesNegocio {

    public List<ErrorMessage> encerrarCiclo(Integer idCiclo) {
        erro = null;
        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        try {
            CicloMediator.get().encerrarCiclo(ciclo);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? new ArrayList<ErrorMessage>() : erro.getMensagens();
    }

    public String getErro(ErrorMessage erro) {
        return erro.getMessage();
    }
}
