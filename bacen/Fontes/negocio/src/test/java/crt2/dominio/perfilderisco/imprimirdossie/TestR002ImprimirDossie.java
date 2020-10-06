package crt2.dominio.perfilderisco.imprimirdossie;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002ImprimirDossie extends ConfiguracaoTestesNegocio {
    public String incluirAta(int idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);

        return SimNaoEnum.getTipo(CicloMediator.get().cicloPosCorecEncerrado(ciclo)).getDescricao();
    }
}
