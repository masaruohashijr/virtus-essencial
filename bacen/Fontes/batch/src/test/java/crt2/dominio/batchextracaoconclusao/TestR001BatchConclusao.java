package crt2.dominio.batchextracaoconclusao;

import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001BatchConclusao extends ConfiguracaoTestesNegocio {

    public void executarBatch() {
        PerfilAtuacaoESMediator.get().rotinaBatchGerarPerfilsOpinioes();
    }
}
