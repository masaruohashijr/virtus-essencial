package crt2.dominio.corec.verificacaoregrasiniciarcorec;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.VerificarPendenciasCorecMediator;
import crt2.ConfiguracaoTestesNegocio;
public class TestR004VerificacaoRegrasIniciarCorecMensagensInformativas extends ConfiguracaoTestesNegocio {

    public String verificarPendencias(Integer idCiclo) {
        String msg = "";
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
         ArrayList<String> mensagensAlerta = VerificarPendenciasCorecMediator.get().mensagensAlerta(ciclo);
         for (String string : mensagensAlerta) {
             msg = string;
        }
        return msg;
    }
    
    public ArrayList<String> verificarPendenciasDetalhesES(Integer idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return VerificarPendenciasCorecMediator.get().mensagensAlerta(ciclo);
    }

    public String getErro(String erro) {
        return erro;
    }
}
