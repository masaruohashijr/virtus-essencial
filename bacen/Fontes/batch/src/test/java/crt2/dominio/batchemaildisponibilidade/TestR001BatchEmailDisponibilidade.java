package crt2.dominio.batchemaildisponibilidade;

import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.batch.main.BatchEnviarEmailDisponibilidade;
import br.gov.bcb.sisaps.stubs.BcMailStub;
import crt2.ConfiguracaoTestesBatch;

public class TestR001BatchEmailDisponibilidade extends ConfiguracaoTestesBatch {

   private Integer ciclo;
    
    
    public void executarBatch() {
        BcMailStub.limpaListaEMails();
        new BatchEnviarEmailDisponibilidade().init();
    }

    public String emailEnviado() {
        return "Sim";
    }

    public List<Email> getEmailsEnviados() {
        return BcMailStub.getListaEMail();
    }

    public String getRemetente(Email resultado) {
        return resultado.getRemetente();
    }

    public String getDestinatario(Email resultado) {
        return resultado.getDestinatarios().toString();
    }

    public String getTitulo(Email resultado) {
        return resultado.getAssunto();
    }

    public String getCorpo(Email resultado) {
        return resultado.getCorpo();
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }
    
    

}
