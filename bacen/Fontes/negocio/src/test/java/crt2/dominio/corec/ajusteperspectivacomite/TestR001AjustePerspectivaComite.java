package crt2.dominio.corec.ajusteperspectivacomite;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ParametroPerspectiva;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPerspectivaMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AjustePerspectivaComite extends ConfiguracaoTestesNegocio {

    public List<ParametroPerspectiva> buscarNotasAjusteCorec(int cicloPK) {
        Ciclo ciclo = CicloMediator.get().loadPK(cicloPK);
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloPK);

        return ParametroPerspectivaMediator.get().buscarParametrosPerspectivaCorec(
                ciclo.getMetodologia(), ciclo, ajusteCorec);
    }

    public String getDescricaoNota(ParametroPerspectiva parametroPerspectiva) {
        return parametroPerspectiva.getDescricao();
    }

}
