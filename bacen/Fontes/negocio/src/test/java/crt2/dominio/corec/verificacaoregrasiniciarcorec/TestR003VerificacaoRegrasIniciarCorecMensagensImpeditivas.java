package crt2.dominio.corec.verificacaoregrasiniciarcorec;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.VerificarPendenciasCorecMediator;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003VerificacaoRegrasIniciarCorecMensagensImpeditivas extends ConfiguracaoTestesNegocio {

    public ArrayList<ErrorMessage> verificarPendencias(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return VerificarPendenciasCorecMediator.get().mostraAlertaBotaoVerificarPendencia(ciclo);
    }

    public String getErro(ErrorMessage erro) {
        return erro.getMessage();
    }
}
