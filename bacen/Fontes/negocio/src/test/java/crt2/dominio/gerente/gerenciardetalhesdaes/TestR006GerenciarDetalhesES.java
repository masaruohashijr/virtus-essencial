package crt2.dominio.gerente.gerenciardetalhesdaes;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.stubs.BcMailStub;
import crt2.ConfiguracaoTestesNegocio;

public class TestR006GerenciarDetalhesES extends ConfiguracaoTestesNegocio {

    private String mensagem = "";

    public void confirmar(int ciclopk) {
        BcMailStub.limpaListaEMails();
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(ciclopk);
        GrauPreocupacaoES grauPreocupacaoES = GrauPreocupacaoESMediator.get().getUltimoGrauPreocupacaoES(ciclo);
        mensagem = GrauPreocupacaoESMediator.get().confirmarGrauPreocupacaoGerente(grauPreocupacaoES); 
    }

    public String getMensagem() {
        return mensagem;
    }
}