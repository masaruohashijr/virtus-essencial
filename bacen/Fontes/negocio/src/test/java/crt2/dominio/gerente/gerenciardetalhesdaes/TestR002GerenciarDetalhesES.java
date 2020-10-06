package crt2.dominio.gerente.gerenciardetalhesdaes;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002GerenciarDetalhesES extends ConfiguracaoTestesNegocio {

    private String mensagem = "";

    public void confirmar(int ciclopk) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(ciclopk);
        PerfilAtuacaoES perfilAtuacaoES = PerfilAtuacaoESMediator.get().getUltimoPerfilAtuacaoES(ciclo);
        mensagem = PerfilAtuacaoESMediator.get().confirmarNovoPerfilAtuacao(perfilAtuacaoES);
    }

    public String getMensagem() {
        return mensagem;
    }
}