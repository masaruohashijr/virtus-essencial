package crt2.dominio.corec.botaoconcluircorec;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import br.gov.bcb.sisaps.stubs.BcMailStub;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002BotaoConcluirCorec extends ConfiguracaoTestesNegocio {
    
    public String concluirCorec(Integer idCiclo) {
        BcMailStub.limpaListaEMails();
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return EncerrarCorecMediator.get().agendarEncerramentoCorec(ciclo);
    }
    
    public List<Email> getEmailsEnviados() {
        return BcMailStub.getListaEMail();
    }
    
    public String getRemetente(Email resultado) {
        return resultado.getRemetente();
    }

    public String getDestinatario(Email resultado) {
        return resultado.getDestinatario();
    }

    public String getTitulo(Email resultado) {
        return resultado.getAssunto();
    }

    public String getCorpo(Email resultado) {
        return resultado.getCorpo();
    }

}
